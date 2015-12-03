package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HalfOpenState extends AbstractCircuitBreakerState {

	private static final Logger logger = LoggerFactory.getLogger(HalfOpenState.class);
			
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
		this.latch = new CountDownLatch(20);
		this.executor = Executors.newSingleThreadExecutor();
		this.executor.submit(new HalfOpenStateTask());
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
		this.executor.submit(new HalfOpenStateTask());
		this.thresholdTimes = thresholdTimes;
		this.thresholdRate = thresholdRate;
	}
	
	@Override
	public Object handle(Object requestEntity) {
		if(tryTimes.incrementAndGet() > maxTryTimes) {
			logger.debug("HalfOpenState 拒绝请求，requestEntity="+requestEntity);
			return false;
		}
		
		//提交执行远程RPC
		boolean result = new Random().nextBoolean();
		logger.debug("HalfOpenState 处理完成：requestEntity="+requestEntity+" result="+result);
		
		if(result) {
			long currSuccess = successTimes.incrementAndGet();
			logger.debug("HalfOpenState 返回成功，成功次数"+currSuccess+" requestEntity="+requestEntity+" result="+result);
		}
		latch.countDown();
		return result;
	}

	@Override
	public void destroy() {
		if(executor!=null && !executor.isTerminated()) {
			executor.shutdownNow();
		}
	}

	private class HalfOpenStateTask implements Runnable {

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
			long totalCount = HalfOpenState.this.maxTryTimes<=Integer.MAX_VALUE ? (int)maxTryTimes : Integer.MAX_VALUE;
			logger.debug("HalfOpenState处理请求"+totalCount+"次，成功"+successCount+"次");
			if (successCount >= HalfOpenState.this.thresholdTimes) {
				toCloseState = thresholdRate == null ? true : (new BigDecimal(
						successCount).divide(new BigDecimal(totalCount), 3,
						RoundingMode.HALF_UP).compareTo(thresholdRate) >= 0);
			}
			if (toCloseState) {
				logger.info("达到临界条件，切换至ClosedState");
				circuitBreaker.transferToClosedState();
			} else {
				logger.info("未达到临界条件，切换至OpenState");
				circuitBreaker.transferToOpenState();
			}
		}
		
	}
}
