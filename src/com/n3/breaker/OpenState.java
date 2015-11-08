package com.n3.breaker;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OpenState extends AbstractCircuitBreakerState {
	
	private final ScheduledThreadPoolExecutor executor;

	protected OpenState(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
		executor = null;
	}
	
	public OpenState(CircuitBreaker circuitBreaker, Long delayMinites) {
		super(circuitBreaker);
		executor = new ScheduledThreadPoolExecutor(1);
		executor.schedule(new OpenStateSchedule(), delayMinites, TimeUnit.MINUTES);
	}
	
	@Override
	public void handle(Object obj) {
		
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
				new HalfOpenStateTransfer(circuitBreaker).transfer();
			} catch (Exception e) {
				e.printStackTrace();
				Thread.interrupted();
			}
		}
	}
	
}
