package com.n3.breaker;

public class ClosedStateTransfer extends AbstractStateTransfer {

	public ClosedStateTransfer(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
	}

	@Override
	protected boolean setToSpecialState(Object properties) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isInSpecialState() {
		return circuitBreaker.getState() instanceof ClosedState;
	}

}
