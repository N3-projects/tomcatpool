package com.n3.breaker;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenState extends AbstractCircuitBreakerState {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenState.class);
	
	private final ScheduledThreadPoolExecutor executor;

	protected OpenState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		executor = null;
	}
	
	public OpenState(CircuitBreaker circuitBreaker, Long delayMinites) {
		super(circuitBreaker);
		executor = new ScheduledThreadPoolExecutor(1);
		executor.schedule(new OpenStateSchedule(), delayMinites, TimeUnit.SECONDS);
	}
	
	@Override
	public Future<ResponseDTO> handle(Callable<ResponseDTO> task) {
//		logger.debug("OpenState 拒绝请求，requestEntity="+task.);
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
				e.printStackTrace();
				Thread.interrupted();
			}
		}
	}

	@Override
	public void writeback(Object requestEntity, ResponseDTO responseDTO) {
		
	}
	
}
