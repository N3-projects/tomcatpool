package com.n3.breaker.core;

public interface StateManager {

	public ClosedState newClosedState(DefaultCircuitBreaker circuitBreaker);
	
	public OpenState newOpenState(DefaultCircuitBreaker circuitBreaker);
	
	public HalfOpenState newHalfOpenState(DefaultCircuitBreaker circuitBreaker);
	
}