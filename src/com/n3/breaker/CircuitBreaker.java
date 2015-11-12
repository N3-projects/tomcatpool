package com.n3.breaker;

public class CircuitBreaker {

	private final String name;
	private volatile CircuitBreakerState state;
	
	public CircuitBreaker(String name) {
		this.name = name;
	}
	
	public void handleInCurrentState(Object request,Object asyncResponse) {
		try {
			state.handle(request);
//			asyncResponse.resume
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("handled "+request+" successfully");
	}

	public CircuitBreakerState getState() {
		return state;
	}

	public void setState(CircuitBreakerState state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}
	
}
