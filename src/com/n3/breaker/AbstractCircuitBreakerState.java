package com.n3.breaker;

public abstract class AbstractCircuitBreakerState implements
		CircuitBreakerState {

	protected CircuitBreaker circuitBreaker;

	protected AbstractCircuitBreakerState(CircuitBreaker circuitBreaker) {
		this.circuitBreaker = circuitBreaker;
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
