package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class HalfOpenState extends AbstractCircuitBreakerState {

	private final CountDownLatch latch;
	private final ExecutorService executor;
	private final long maxTryTimes;
	private final AtomicLong tryTimes;
	private final AtomicLong successTimes;
	private final BigDecimal thresholdRate;
	private final long thresholdTimes;
	
	protected HalfOpenState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		this.maxTryTimes = 1;
		this.tryTimes = new AtomicLong(0);
		this.successTimes = new AtomicLong(0);
		this.latch = new CountDownLatch(maxTryTimes<=Integer.MAX_VALUE ? (int)maxTryTimes : Integer.MAX_VALUE);
		this.executor = Executors.newSingleThreadExecutor();
		this.executor.submit(new HalfOpenTask());
		this.thresholdTimes = 0;
		this.thresholdRate = null;
	}
	
	public HalfOpenState(CircuitBreaker circuitBreaker, long maxTryTimes, long thresholdTimes, BigDecimal thresholdRate) {
		super(circuitBreaker);
		this.maxTryTimes = maxTryTimes;
		this.tryTimes = new AtomicLong(0);
		this.successTimes = new AtomicLong(0);
		this.latch = new CountDownLatch(maxTryTimes<=Integer.MAX_VALUE ? (int)maxTryTimes : Integer.MAX_VALUE);
		this.executor = Executors.newSingleThreadExecutor();
		this.executor.submit(new HalfOpenTask());
		this.thresholdTimes = thresholdTimes;
		this.thresholdRate = thresholdRate;
	}
	
	@Override
	public void handle(Object obj) {
		if(tryTimes.incrementAndGet() > maxTryTimes) {
			return;
		}
		
		//提交执行远程RPC
		boolean success = true;
		
		if(success) {
			successTimes.incrementAndGet();
		}
		latch.countDown();
	}

	@Override
	public void destroy() {
		if(executor!=null && !executor.isTerminated()) {
			executor.shutdownNow();
		}
	}

	private class HalfOpenTask implements Runnable {

		@Override
		public void run() {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				circuitBreaker.transferToOpenState();
				Thread.currentThread().interrupt();
				return;
			}
			boolean toCloseState = false;
			long successCount = HalfOpenState.this.successTimes.get();
			long totalCount = HalfOpenState.this.latch.getCount();
			if (successCount >= HalfOpenState.this.thresholdTimes) {
				toCloseState = thresholdRate == null ? true : (new BigDecimal(
						successCount).divide(new BigDecimal(totalCount), 3,
						RoundingMode.HALF_UP).compareTo(thresholdRate) >= 0);
			}
			if (toCloseState) {
				circuitBreaker.transferToClosedState();
			} else {
				circuitBreaker.transferToOpenState();
			}
		}
		
	}
}
