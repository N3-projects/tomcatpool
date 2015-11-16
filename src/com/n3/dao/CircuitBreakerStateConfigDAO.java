package com.n3.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.n3.model.CircuitBreakerStateConfig;

@Component
public class CircuitBreakerStateConfigDAO extends BaseHibernateDAO {

	@SuppressWarnings("unchecked")
	public List<CircuitBreakerStateConfig> getStateConfig(String stateName, String circuitBreakerName) {
		return (List<CircuitBreakerStateConfig>) super.getHibernateTemplate().findByNamedQuery(
				"from CircuitBreakerStateConfig where name =? and circuitBreakerName=?", stateName, circuitBreakerName);
	}
}
