package com.n3.breaker.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.CircuitBreakerState;
import com.n3.breaker.ResponseDTO;
import com.n3.logic.CircuitBreakerStateConfigLogic;
import com.n3.model.CircuitBreakerStateConfig;
import com.n3.util.ApplicationContextHolder;

public class DefaultCircuitBreaker implements CircuitBreaker {

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
	private final String name;
	private final int concurrency;
	private final int bufferSize;
	private volatile CircuitBreakerState state;
	
	public DefaultCircuitBreaker(String name, int concurrency, int bufferSize) {
		this.name = name;
		this.concurrency = concurrency;
		this.bufferSize = bufferSize;
		this.transferToClosedState();
	}
	
	public ResponseDTO syncHandle(Callable<ResponseDTO> task, Object requestEntity, long timeoutSeconds)
			throws RejectedExecutionException, TimeoutException, InterruptedException, ExecutionException {
		if(task==null) {
			throw new IllegalArgumentException("callable task can not be null");
		}
		ResponseDTO result = null;
		final CircuitBreakerState currentState = state;
		Future<ResponseDTO> future = null;
		try {
			future = currentState.handle(task);
		} catch(RejectedExecutionException e) {
			logger.error(currentState.getClass().getSimpleName()+"拒绝请求:" + requestEntity);
			throw e;
		} 
		try {
			result = future.get(timeoutSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			logger.error("请求超时:" + requestEntity);
			throw e;
		} catch (InterruptedException e) {
			logger.error("请求中断:" + requestEntity, e);
			throw e;
		} catch (ExecutionException e) {
			logger.error("处理失败:" + requestEntity, e);
			throw e;
		} finally {
			future.cancel(true);
			currentState.writeback(requestEntity, result);
		}
		return result;
	}
	
//	TODO
//	/**
//	 * required jersey2.2 tomcat7
//	 * @param task
//	 * @param asyncResponse
//	 * @return
//	 */
//	public void asyncHandle(Callable<?> task, AsyncResponse asyncResponse) throws Exception {
//		return state.handle(task);
//	}

	public String getName() {
		return name;
	}
	
	protected synchronized boolean transferToClosedState() {
		if(state!=null && state instanceof ClosedState) {
			logger.info("circuit breaker named "+name+" is already in closed state, ignore transfer command");
			return false;
		}
		CircuitBreakerStateConfig stateConfig = null;
		try {
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			stateConfig = circuitBreakerStateConfigLogic.getStateConfig(ClosedState.class, name);
		} catch (Exception e) {
			logger.error("读取CircuitBreakerStateConfig配置失败", e);
		}
		ClosedState newState = stateConfig == null ? new ClosedState(this)
				: new ClosedState(this,
						stateConfig.getThresholdFailureTimes(),
						stateConfig.getThresholdFailureRate(),
						stateConfig.getPeriodSeconds());
		if (state != null) {
			state.destroy();
		}
		state = newState;
		logger.info("CircuitBreaker "+name+" 已切换至ClosedState");
		return true;
	}
	
	protected synchronized boolean transferToOpenState() {
		if(state!=null && state instanceof OpenState) {
			logger.info("circuit breaker named "+name+" is already in open state, ignore transfer command");
			return false;
		}
		CircuitBreakerStateConfig stateConfig = null;
		try {
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			stateConfig = circuitBreakerStateConfigLogic.getStateConfig(OpenState.class, name);
		} catch (Exception e) {
			logger.error("读取CircuitBreakerStateConfig配置失败", e);
		}
		OpenState newState = stateConfig == null ? new OpenState(this)
		: new OpenState(this,stateConfig.getDelaySeconds());
		if(state != null) {
			state.destroy();
		}
		state = newState;
		logger.info("CircuitBreaker "+name+" 已切换至OpenState");
		return true;
	}
	
	protected synchronized boolean transferToHalfOpenState() {
		if(state!=null && state instanceof HalfOpenState) {
			logger.info("circuit breaker named "+name+" is already in open state, ignore transfer command");
			return false;
		}
		CircuitBreakerStateConfig stateConfig = null;
		try {
			CircuitBreakerStateConfigLogic circuitBreakerStateConfigLogic = 
					ApplicationContextHolder.getApplicationContext().getBean(CircuitBreakerStateConfigLogic.class);
			stateConfig = circuitBreakerStateConfigLogic.getStateConfig(HalfOpenState.class, name);
		} catch (Exception e) {
			logger.error("读取CircuitBreakerStateConfig配置失败", e);
		}
		HalfOpenState newState = stateConfig == null ? new HalfOpenState(
				this) : new HalfOpenState(this,
						stateConfig.getTargetTimes(),
						stateConfig.getThresholdSuccessTimes(),
						stateConfig.getThresholdSuccessRate());
				if (state != null) {
					state.destroy();
				}
				state = newState;
				logger.info("CircuitBreaker "+name+" 已切换至HalfOpenState");
		return true;
	}
	
	public void destroy() {
		if(state != null) {
			state.destroy();
		}
	}

	public int getConcurrency() {
		return concurrency;
	}

	public int getBufferSize() {
		return bufferSize;
	}
}
