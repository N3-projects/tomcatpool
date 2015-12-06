package com.n3.breaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
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
	
	@Override
	public Future<ResponseDTO> handle(Callable<ResponseDTO> task) {
		if(tryTimes.incrementAndGet() > maxTryTimes) {
//			logger.debug("HalfOpenState 拒绝请求，requestEntity="+requestEntity);
			throw new RejectedExecutionException("HalfOpenState Threshold Reached");
		}
		
		//提交执行远程RPC
//		logger.debug("HalfOpenState 处理完成：requestEntity="+requestEntity+" result="+result);
		FutureTask<ResponseDTO> future = new FutureTask<ResponseDTO>(task);
		new Thread(future).start();
		return future;
	}
	
	@Override
	public void writeback(Object requestEntity, ResponseDTO responseDTO) {
		latch.countDown();
		if(responseDTO!=null && !responseDTO.isExceptionOccured()) {
			long currSuccess = successTimes.incrementAndGet();
			logger.debug("HalfOpenState 返回成功，成功次数"+currSuccess+" requestEntity="+requestEntity+" result="+responseDTO.getResponseEntity());
		}
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
