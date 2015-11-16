package com.n3.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.n3.dao.CircuitBreakerConfigDAO;
import com.n3.model.CircuitBreakerConfig;
import com.n3.model.CircuitBreakerConfigPK;

@Component
public class CircuitBreakerConfigLogic {

	private CircuitBreakerConfigDAO circuitBreakerConfigDao;
	
	public CircuitBreakerConfig getCircuitBreakerConfig(String name) {
		CircuitBreakerConfigPK pk = new CircuitBreakerConfigPK(name, System.getProperty("catalina.home"));
		return circuitBreakerConfigDao.getById(pk);
	}

	@Autowired
	public void setCircuitBreakerConfigDao(CircuitBreakerConfigDAO circuitBreakerConfigDao) {
		this.circuitBreakerConfigDao = circuitBreakerConfigDao;
	}
}
