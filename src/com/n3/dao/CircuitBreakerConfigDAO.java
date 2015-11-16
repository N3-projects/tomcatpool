package com.n3.dao;

import org.springframework.stereotype.Component;

import com.n3.model.CircuitBreakerConfig;
import com.n3.model.CircuitBreakerConfigPK;

@Component
public class CircuitBreakerConfigDAO extends BaseHibernateDAO {

	public CircuitBreakerConfig getById(CircuitBreakerConfigPK pk) {
//		super.getHibernateTemplate().findByNamedQuery(
//				"from CircuitBreakerConfig where name =? and webServerName=?", pk.getName(),pk.getWebServerName());
		return getHibernateTemplate().get(CircuitBreakerConfig.class, pk);
	}
}
