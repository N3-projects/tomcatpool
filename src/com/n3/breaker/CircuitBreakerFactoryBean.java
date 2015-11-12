package com.n3.breaker;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class CircuitBreakerFactoryBean implements FactoryBean<CircuitBreaker>,
		InitializingBean, DisposableBean {

	private CircuitBreaker targetObject;
	private String name;
	
	@Override
	public void destroy() throws Exception {	
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//TODO load from database
		if(name==null) {
			//name does not exists
			throw new IllegalArgumentException("CircuitBreakerConfig nameed "+name+" dose not exist");
		}
		targetObject = new CircuitBreaker(name);
		new ClosedStateTransfer(targetObject).transfer();
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

}
