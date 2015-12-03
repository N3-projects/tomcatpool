package com.n3.breaker;

public interface CircuitBreakerState {

	public Object handle(Object obj);
	
	public void destroy();
}
