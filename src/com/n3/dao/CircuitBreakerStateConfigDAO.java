package com.n3.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.n3.model.CircuitBreakerStateConfig;

@Component
public class CircuitBreakerStateConfigDAO extends BaseHibernateDAO {

	@SuppressWarnings("unchecked")
	public List<CircuitBreakerStateConfig> getStateConfig(String stateName, String circuitBreakerName) {
		return (List<CircuitBreakerStateConfig>) super.getHibernateTemplate().findByNamedParam
				("from CircuitBreakerStateConfig where name = :name and circuitBreakerName = :circuitBreakerName" ,
						new String[] {"name","circuitBreakerName"}, new Object[] {stateName,circuitBreakerName});
	}
}
