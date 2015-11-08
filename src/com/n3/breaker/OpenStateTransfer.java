package com.n3.breaker;

public class OpenStateTransfer extends AbstractStateTransfer {

	private static final long DEFAULT_DELAY_MINITES = 1L;
	
	public OpenStateTransfer(CircuitBreaker circuitBreaker) {
		super(circuitBreaker);
	}

	@Override
	protected boolean setToSpecialState(Object properties) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isInSpecialState() {
		return circuitBreaker.getState() instanceof OpenState;
	}

}
