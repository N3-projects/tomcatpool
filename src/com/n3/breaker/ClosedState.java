package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClosedState extends AbstractCircuitBreakerState {

	private static final long MAX_FAILURE_COUNT = 10;
	private static final BigDecimal MAX_FAILURE_RATE = new BigDecimal("0.00000001");
	
	private final ReentrantReadWriteLock lock;
	private final ScheduledExecutorService executor;

	private int hotspotCount = 0;
	private int totalCount = 0;
	
	public ClosedState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		this.lock = new ReentrantReadWriteLock();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleWithFixedDelay(new TestSchedule(), 500, 60, TimeUnit.SECONDS);
	}
	
	
	@Override
	public void handle(Object obj) {
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
			totalCount++;
			//如果返回失败，回写失败记录
			if(!result) {
				hotspotCount++;
				if(isThresholdReached()) {
					new OpenStateTransfer(circuitBreaker).transfer();
					System.out.println("circuitBreaker set to OpenState");
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected boolean isThresholdReached() {
		if(hotspotCount >= MAX_FAILURE_COUNT) {
			BigDecimal _t = new BigDecimal(hotspotCount).divide(new BigDecimal(totalCount),3,RoundingMode.HALF_UP);
			return _t.compareTo(MAX_FAILURE_RATE) >= 0;
		}
		return false;
	}
	
	private class TestSchedule implements Runnable {
		@Override
		public void run() {
			try {
				lock.writeLock().lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.interrupted();
			}
			try {
				ClosedState.this.hotspotCount = 0;
				ClosedState.this.totalCount = 0;
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
