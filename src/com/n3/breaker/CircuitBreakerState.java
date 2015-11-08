package com.n3.breaker;

public interface CircuitBreakerState {

	public void handle(Object obj);
	
	public void destroy();
}
