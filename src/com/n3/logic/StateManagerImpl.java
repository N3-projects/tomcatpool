package com.n3.logic;

import org.springframework.beans.factory.annotation.Autowired;

import com.n3.breaker.core.ClosedState;
import com.n3.breaker.core.DefaultCircuitBreaker;
import com.n3.breaker.core.HalfOpenState;
import com.n3.breaker.core.OpenState;
import com.n3.breaker.core.StateManager;
import com.n3.model.CircuitBreakerStateConfig;

public class StateManagerImpl implements StateManager {

	private CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic;
	
	@Override
	public ClosedState newClosedState(DefaultCircuitBreaker circuitBreaker) {
		CircuitBreakerStateConfig config = 
				circuitBreakerStateConfigLogic.getStateConfig(ClosedState.class, circuitBreaker.getName());
		if(config != null) {
			return new ClosedState(circuitBreaker, config.getThresholdFailureTimes(), config.getThresholdFailureRate(), config.getPeriodSeconds());
		}
		return null;
	}

	@Override
	public OpenState newOpenState(DefaultCircuitBreaker circuitBreaker) {
		CircuitBreakerStateConfig config = 
				circuitBreakerStateConfigLogic.getStateConfig(OpenState.class, circuitBreaker.getName());
		if(config != null) {
			return new OpenState(circuitBreaker, config.getDelaySeconds());
		}
		return null;
	}

	@Override
	public HalfOpenState newHalfOpenState(DefaultCircuitBreaker circuitBreaker) {
		CircuitBreakerStateConfig config = 
				circuitBreakerStateConfigLogic.getStateConfig(HalfOpenState.class, circuitBreaker.getName());
		if(config != null) {
			return new HalfOpenState(circuitBreaker, config.getTargetTimes(), config.getThresholdSuccessTimes(), config.getThresholdSuccessRate());
		}
		return null;
	}

	@Autowired
	public void setCircuitBreakerStateConfigLogic(
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic) {
		this.circuitBreakerStateConfigLogic = circuitBreakerStateConfigLogic;
	}

}
