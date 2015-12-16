package com.n3.breaker.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.core.DefaultCircuitBreaker;
import com.n3.breaker.core.StateManager;

public class CircuitBreakerFactoryBean implements FactoryBean<CircuitBreaker>,
		InitializingBean, DisposableBean {

	private CircuitBreaker targetObject;
	private String name;
	private int concurrency;
	private int bufferSize;
	private StateManager stateManager;
	
	@Override
	public void destroy() throws Exception {	
		if(targetObject != null) {
			targetObject.destroy();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(name==null) {
			throw new IllegalArgumentException("The name of CircuitBreaker "+name+" is required but actually null");
		}
		if(stateManager==null) {
			throw new IllegalArgumentException("The stateManager of CircuitBreaker "+name+" is required but actually null");
		}
		/*
		//load from database
		CircuitBreakerConfigLogic logic = (CircuitBreakerConfigLogic)ApplicationContextHolder.getApplicationContext().getBean("circuitBreakerConfigLogic");
		CircuitBreakerConfig cb = logic.getCircuitBreakerConfig(name);
		if(cb==null) {
			//name does not exists
			throw new IllegalArgumentException("CircuitBreakerConfig nameed "+name+" dose not exist");
		}
		*/
		targetObject = new DefaultCircuitBreaker(name, concurrency, bufferSize, stateManager);
		targetObject.init();
	}

	@Override
	public CircuitBreaker getObject() throws Exception {
		return targetObject;
	}

	@Override
	public Class<?> getObjectType() {
		return targetObject==null ? Object.class : CircuitBreaker.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}

}
