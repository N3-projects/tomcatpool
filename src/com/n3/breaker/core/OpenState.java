package com.n3.breaker.core;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n3.breaker.RequestHandler;
import com.n3.breaker.ResponseDTO;

public class OpenState extends AbstractCircuitBreakerState {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenState.class);
	
	private final ScheduledThreadPoolExecutor executor;

	protected OpenState(DefaultCircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		executor = null;
	}
	
	public OpenState(DefaultCircuitBreaker circuitBreaker, Long delaySeconds) {
		super(circuitBreaker);
		executor = new ScheduledThreadPoolExecutor(1);
		executor.schedule(new OpenStateSchedule(), delaySeconds, TimeUnit.SECONDS);
	}
	
	@Override
	public Future<ResponseDTO> handle(RequestHandler task) {
		throw new RejectedExecutionException("OpenState Threshold Reached");
	}

	protected boolean isThresholdReached() {
		return executor!=null && executor.isTerminated();
	}

	@Override
	public void destroy() {
		if(executor!=null && !executor.isTerminated()) {
			executor.shutdownNow();
		}
	}

	class OpenStateSchedule implements Runnable {
		@Override
		public void run() {
			try {
				logger.info("达到临界条件，切换至HalfOpenState");
				circuitBreaker.transferToHalfOpenState();
			} catch (Exception e) {
				logger.error(null, e);
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void writeback(Object requestEntity, ResponseDTO responseDTO) {
		
	}
	
}
