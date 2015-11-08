package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HalfOpenState extends AbstractCircuitBreakerState {

	private final CountDownLatch latch;
	private final ExecutorService executor;
	private final int maxTryTimes;
	private final AtomicInteger tryTimes;
	private final AtomicInteger successTimes;
	private final BigDecimal thresholdRate;
	private final int thresholdTimes;
	
	protected HalfOpenState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		maxTryTimes = 1;
		tryTimes = new AtomicInteger(0);
		successTimes = new AtomicInteger(0);
		latch = new CountDownLatch(maxTryTimes);
		executor = Executors.newSingleThreadExecutor();
		executor.submit(new HalfOpenTask());
		thresholdRate = null;
		thresholdTimes = 0;
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
		// TODO Auto-generated method stub

	}

	private class HalfOpenTask implements Runnable {

		@Override
		public void run() {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				new OpenStateTransfer(circuitBreaker).transfer();
				return;
			}
			boolean toCloseState = false;
			int successCount = HalfOpenState.this.successTimes.get();
			int totalCount = HalfOpenState.this.maxTryTimes;
			if (successCount >= HalfOpenState.this.thresholdTimes) {
				toCloseState = thresholdRate == null ? true : (new BigDecimal(
						successCount).divide(new BigDecimal(totalCount), 3,
						RoundingMode.HALF_UP).compareTo(thresholdRate) >= 0);
			}
			if (toCloseState) {
				new ClosedStateTransfer(circuitBreaker).transfer();
			} else {
				new OpenStateTransfer(circuitBreaker).transfer();
			}
		}
		
	}
}
