package com.n3.breaker;

public class ClosedStateTransfer extends AbstractStateTransfer {

	public ClosedStateTransfer(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
	}

	@Override
	protected boolean setToSpecialState(Object properties) {
		circuitBreaker.setState(new ClosedState(circuitBreaker));
		return true;
	}

	@Override
	protected boolean isInSpecialState() {
		return circuitBreaker.getState()!=null && circuitBreaker.getState() instanceof ClosedState;
	}

}
