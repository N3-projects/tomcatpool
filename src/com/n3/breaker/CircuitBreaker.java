package com.n3.breaker;

public class CircuitBreaker {

	private final String NAME;
	private volatile CircuitBreakerState state;
	
	public CircuitBreaker(String name) {
		NAME = name;
	}
	
	public void handleInCurrentState(Object request,Object asyncResponse) {
		try {
			state.handle(request);
//			asyncResponse.resume
		} catch (Exception e) {
			
		}
	}

	public CircuitBreakerState getState() {
		return state;
	}

	public void setState(CircuitBreakerState state) {
		this.state = state;
	}

	public String getNAME() {
		return NAME;
	}
	
}
