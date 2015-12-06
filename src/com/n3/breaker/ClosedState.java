package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClosedState extends AbstractCircuitBreakerState {
	
	private static final Logger logger = LoggerFactory.getLogger(ClosedState.class);
			
	private final ReentrantReadWriteLock lock;
	private final ScheduledExecutorService executor;
	private final ExecutorService internalPool;

	private final long thresholdFailureTimes;
	private final BigDecimal thresholdFailureRate;

	private int failureTimes = 0;
	private int totalTimes = 0;
	
	public ClosedState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedStateTask(), 60, 60, TimeUnit.SECONDS);
		this.thresholdFailureTimes = 100;
		this.thresholdFailureRate = new BigDecimal("0.60");
		this.internalPool = Executors.newFixedThreadPool(200);
	}
	
	public ClosedState(CircuitBreaker circuitBreaker, long thresholdFailureTimes, BigDecimal thresholdFailureRate, long periodSeconds) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedStateTask(), periodSeconds, periodSeconds, TimeUnit.SECONDS);
		this.thresholdFailureTimes = thresholdFailureTimes;
		this.thresholdFailureRate = thresholdFailureRate;
		this.internalPool = new ThreadPoolExecutor(
				circuitBreaker.getConcurrency() / 2,
				circuitBreaker.getConcurrency(), 300, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(circuitBreaker
						.getBufferSize()), new ThreadPoolExecutor.AbortPolicy());
	}

	@Override
	public Future<ResponseDTO> handle(Callable<ResponseDTO> task) {
		lock.readLock().lock();
		try {
			//如果达到临界条件，返回
			if(isThresholdReached()) {
//				logger.debug("ClosedState 拒绝请求：requestEntity="+requestEntity);
				throw new RejectedExecutionException("ClosedState Threshold Reached");
			}
		} finally {
			lock.readLock().unlock();
		}
		return internalPool.submit(task);
	}

	@Override
	public void writeback(Object requestEntity, ResponseDTO responseDTO) {
		lock.writeLock().lock();
		try {
			totalTimes++;
			//如果返回失败，回写失败记录
			if(responseDTO!=null && responseDTO.isExceptionOccured()) {
				failureTimes++;
				logger.debug("返回错误，错误次数"+failureTimes+" 总数"+totalTimes+" requestEntity="+requestEntity+" result="+responseDTO.getResponseEntity());
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
