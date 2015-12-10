package com.n3.breaker.spring;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;


public class CircuitBreakerAdvisor extends DefaultPointcutAdvisor implements InitializingBean {

	private static final long serialVersionUID = -2779022013435827842L;
	
	public CircuitBreakerAdvisor() {
		super();
		setOrder(0);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(super.getPointcut()==null) {
			throw new BeanInitializationException("the pointcut of CircuitBreakerAdvisor must be specifically");
		}
		if(super.getAdvice()==null) {
			setAdvice(new CircuitBreakerInterceptor());
		}
	}
}
