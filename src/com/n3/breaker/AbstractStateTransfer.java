package com.n3.breaker;

public abstract class AbstractStateTransfer {

	protected final CircuitBreaker circuitBreaker;
	
	public AbstractStateTransfer(CircuitBreaker circuitBreaker) {
		super();
		this.circuitBreaker = circuitBreaker;
	}
	
	public boolean transfer() {
		if(!isInSpecialState()) {
			synchronized(circuitBreaker) {
				if(!isInSpecialState()) {
					//TODO load properties from database
					circuitBreaker.getState().destroy();
					return setToSpecialState(new Object());
				}
			}
		}
		return false;
	}
	
	protected abstract boolean setToSpecialState(Object properties) ;
	
	protected abstract boolean isInSpecialState();
}
