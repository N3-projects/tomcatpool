package com.n3.breaker;

public class HalfOpenStateTransfer extends AbstractStateTransfer {

	public HalfOpenStateTransfer(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
	}

	@Override
	protected boolean setToSpecialState(Object properties) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isInSpecialState() {
		return circuitBreaker.getState() instanceof HalfOpenState;
	}

}
