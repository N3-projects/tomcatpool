package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClosedState extends AbstractCircuitBreakerState {
	
	private static final Logger logger = LoggerFactory.getLogger(ClosedState.class);
			
	private final ReentrantReadWriteLock lock;
	private final ScheduledExecutorService executor;

	private long thresholdFailureTimes;
	private BigDecimal thresholdFailureRate;

	private int failureTimes = 0;
	private int totalTimes = 0;
	
	public ClosedState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedLocalTask(), 3, 60, TimeUnit.SECONDS);
		thresholdFailureTimes = 100;
		thresholdFailureRate = new BigDecimal("0.60");
	}
	
	public ClosedState(CircuitBreaker circuitBreaker, long thresholdFailureTimes, BigDecimal thresholdFailureRate, 
			long delaySeconds, long periodSeconds) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleAtFixedRate(new ClosedLocalTask(), delaySeconds, periodSeconds, TimeUnit.SECONDS);
		this.thresholdFailureTimes = thresholdFailureTimes;
		this.thresholdFailureRate = thresholdFailureRate;
	}

	@Override
	public void handle(Object requestEntity) {
		lock.readLock().lock();
		try {
			//如果达到临界条件，返回
			if(isThresholdReached()) {
				return;
			}
		} finally {
			lock.readLock().unlock();
		}
		//未达到临界条件，提交任务，阻塞等待任务返回
		boolean result = new Random().nextBoolean();
		
		lock.writeLock().lock();
		try {
			totalTimes++;
			//如果返回失败，回写失败记录
			if(!result) {
				failureTimes++;
				logger.debug("返回错误，错误次数"+failureTimes+" 总数"+totalTimes+" requestEntity="+requestEntity);
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
	
	private class ClosedLocalTask implements Runnable {
		@Override
		public void run() {
			try {
				lock.writeLock().lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.interrupted();
			}
			try {
				ClosedState.this.failureTimes = 0;
				ClosedState.this.totalTimes = 0;
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
	}
}
