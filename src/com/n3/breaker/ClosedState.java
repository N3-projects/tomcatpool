package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClosedState extends AbstractCircuitBreakerState {
	
	private static final Logger logger = LoggerFactory.getLogger(ClosedState.class);
			
	private final ReentrantReadWriteLock lock;
	private final ScheduledExecutorService executor;
	private final ExecutorService internalPool;

	private long thresholdFailureTimes;
	private BigDecimal thresholdFailureRate;

	private int failureTimes = 0;
	private int totalTimes = 0;
	
	public ClosedState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedStateTask(), 3, 60, TimeUnit.SECONDS);
		this.thresholdFailureTimes = 100;
		this.thresholdFailureRate = new BigDecimal("0.60");
		this.internalPool = Executors.newFixedThreadPool(200);
	}
	
	public ClosedState(CircuitBreaker circuitBreaker, long thresholdFailureTimes, BigDecimal thresholdFailureRate, 
			long delaySeconds, long periodSeconds) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedStateTask(), delaySeconds, periodSeconds, TimeUnit.SECONDS);
		this.thresholdFailureTimes = thresholdFailureTimes;
		this.thresholdFailureRate = thresholdFailureRate;
		this.internalPool = new ThreadPoolExecutor(
				circuitBreaker.getConcurrency() / 2,
				circuitBreaker.getConcurrency(), 300, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(circuitBreaker
						.getBufferSize()), new ThreadPoolExecutor.AbortPolicy());
	}

	@Override
	public Object handle(Object requestEntity) {
		lock.readLock().lock();
		try {
			//如果达到临界条件，返回
			if(isThresholdReached()) {
				logger.debug("ClosedState 拒绝请求：requestEntity="+requestEntity);
				return false;
			}
		} finally {
			lock.readLock().unlock();
		}
		//未达到临界条件，提交任务，阻塞等待任务返回
		Future<Boolean> future = null;
		
		try {
			 future = internalPool.submit(new InternalCallable(requestEntity));
		} catch (RejectedExecutionException e) {
			logger.error("加入到任务队列出错 requestEntity="+requestEntity);
			return false;
		}
		
		/* 同步方式
		 */ 
		Boolean result;
		try {
			result = future.get(10L,TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			logger.error("请求超时:"+requestEntity);
			result = false;
		} catch (InterruptedException e) {
			logger.error("请求中断:"+requestEntity,e);
			result = false;
		} catch (ExecutionException e) {
			logger.error("处理失败:"+requestEntity,e);
			result = false;
		} finally {
			future.cancel(true);
		}
		return result;
	}

	private void writeback(Object requestEntity, Boolean result) {
		lock.writeLock().lock();
		try {
			totalTimes++;
			//如果返回失败，回写失败记录
			if(!result) {
				failureTimes++;
				logger.debug("返回错误，错误次数"+failureTimes+" 总数"+totalTimes+" requestEntity="+requestEntity+" result="+result);
				if(isThresholdReached()) {
					logger.info("达到临界条件，切换至OpenState");
					circuitBreaker.transferToOpenState();
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	protected boolean isThresholdReached() {
		if(failureTimes >= thresholdFailureTimes) {
			return thresholdFailureRate == null ? true : new BigDecimal(
					failureTimes).divide(new BigDecimal(totalTimes), 3,
					RoundingMode.HALF_UP).compareTo(thresholdFailureRate) >= 0;
		}
		return false;
	}
	
	private class InternalCallable implements Callable<Boolean> {

		Object requestEntity;
		
		InternalCallable(Object requestEntity) {
			this.requestEntity = requestEntity;
		}
		
		@Override
		public Boolean call() throws Exception {
			Boolean result = null;
			try {
				Thread.sleep(2000L);
				result = new Random().nextBoolean();
				logger.debug("ClosedState 处理完成：requestEntity="+requestEntity+" result="+result);
				ClosedState.this.writeback(requestEntity,result);
				return result;
			} catch (InterruptedException e) {
				logger.error("交易中断：requestEntity="+requestEntity);
				throw e;
			} catch (Exception e) {
				logger.error("交易失败：requestEntity="+requestEntity, e);
				throw e;
			}
		}
	
	}
	
	private class ClosedStateTask implements Runnable {
		
		@Override
		public void run() {
			try {
				lock.writeLock().lockInterruptibly();
			} catch (InterruptedException e) {
				logger.error("", e);
				Thread.interrupted();
			}
			try {
				ClosedState.this.failureTimes = 0;
				ClosedState.this.totalTimes = 0;
				logger.info("未达到临界条件，重置ClosedState计数器");
			} finally {
				lock.writeLock().unlock();
			}
		}
		
	}

	@Override
	public void destroy() {
		if(executor!=null && !executor.isTerminated()) {
			executor.shutdownNow();
		}
		if(internalPool!=null && !internalPool.isTerminated()) {
			// 已提交请求不会中断，根据其自身超时时间返回
			internalPool.shutdown();
		}
	}
}
