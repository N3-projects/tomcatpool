package com.n3.breaker.core;

import com.n3.breaker.CircuitBreakerState;

public abstract class AbstractCircuitBreakerState implements
		CircuitBreakerState {

	protected DefaultCircuitBreaker circuitBreaker;

	protected AbstractCircuitBreakerState(DefaultCircuitBreaker circuitBreaker) {
		this.circuitBreaker = circuitBreaker;
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
