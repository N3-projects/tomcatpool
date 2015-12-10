package com.n3.breaker.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.core.DefaultCircuitBreaker;
import com.n3.logic.CircuitBreakerConfigLogic;
import com.n3.model.CircuitBreakerConfig;
import com.n3.util.ApplicationContextHolder;

public class CircuitBreakerFactoryBean implements FactoryBean<CircuitBreaker>,
		InitializingBean, DisposableBean {

	private CircuitBreaker targetObject;
	private String name;
	
	@Override
	public void destroy() throws Exception {	
		if(targetObject != null) {
			targetObject.destroy();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(name==null) {
			throw new IllegalArgumentException("CircuitBreakerConfig nameed "+name+" is required but actually null");
		}
		//load from database
		CircuitBreakerConfigLogic logic = (CircuitBreakerConfigLogic)ApplicationContextHolder.getApplicationContext().getBean("circuitBreakerConfigLogic");
		CircuitBreakerConfig cb = logic.getCircuitBreakerConfig(name);
		if(cb==null) {
			//name does not exists
			throw new IllegalArgumentException("CircuitBreakerConfig nameed "+name+" dose not exist");
		}
		targetObject = new DefaultCircuitBreaker(name, cb.getConcurrency(), cb.getBufferSize());
//		targetObject.transferToClosedState();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
