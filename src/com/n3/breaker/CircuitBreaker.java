package com.n3.breaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n3.logic.CircuitBreakerStateConfigLogic;
import com.n3.model.CircuitBreakerStateConfig;
import com.n3.util.ApplicationContextHolder;

public class CircuitBreaker {

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
	private final String name;
	private volatile CircuitBreakerState state;
	
	public CircuitBreaker(String name) {
		this.name = name;
	}
	
	public void handleInCurrentState(Object request,Object asyncResponse) {
		try {
			state.handle(request);
//			asyncResponse.resume
			System.out.println("handled "+request+" successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public CircuitBreakerState getState() {
		return state;
	}

	public void setState(CircuitBreakerState state) {
		this.state = state;
	}*/

	public String getName() {
		return name;
	}
	
	public boolean transferToClosedState() {
		synchronized(this) {
			if(state!=null && state instanceof ClosedState) {
				logger.debug("circuit breaker named "+name+" is already in closed state, ignore transfer command");
				return false;
			}
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			CircuitBreakerStateConfig stateConfig = 
					circuitBreakerStateConfigLogic.getStateConfig(ClosedState.class, name);
			ClosedState newState = stateConfig == null ? new ClosedState(this)
					: new ClosedState(this,
							stateConfig.getThresholdFailureTimes(),
							stateConfig.getThresholdFailureRate(),
							stateConfig.getDelaySeconds(),
							stateConfig.getPeriodSeconds());
			if (state != null) {
				state.destroy();
			}
			state = newState;
		}
		return true;
	}
	
	public boolean transferToOpenState() {
		synchronized(this) {
			if(state!=null && state instanceof OpenState) {
				logger.debug("circuit breaker named "+name+" is already in open state, ignore transfer command");
				return false;
			}
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			CircuitBreakerStateConfig stateConfig = 
					circuitBreakerStateConfigLogic.getStateConfig(OpenState.class, name);
			OpenState newState = stateConfig == null ? new OpenState(this)
					: new OpenState(this,stateConfig.getDelaySeconds());
			if(state != null) {
				state.destroy();
			}
			state = newState;
		}
		return true;
	}
	
	public boolean transferToHalfOpenState() {
		synchronized(this) {
			if(state!=null && state instanceof HalfOpenState) {
				logger.debug("circuit breaker named "+name+" is already in open state, ignore transfer command");
				return false;
			}
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			CircuitBreakerStateConfig stateConfig = 
					circuitBreakerStateConfigLogic.getStateConfig(HalfOpenState.class, name);
			HalfOpenState newState = stateConfig == null ? new HalfOpenState(this)
			: new HalfOpenState(this,stateConfig.getTargetTimes(),stateConfig.getThresholdSuccessTimes(), stateConfig.getThresholdSuccessRate());
			if(state != null) {
				state.destroy();
			}
			state = newState;
		}
		return true;
	}
	
	public void destroy() {
		if(state != null) {
			state.destroy();
		}
	}
}
