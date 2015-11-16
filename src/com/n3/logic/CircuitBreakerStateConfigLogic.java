package com.n3.logic;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.n3.breaker.CircuitBreakerState;
import com.n3.dao.CircuitBreakerStateConfigDAO;
import com.n3.model.CircuitBreakerStateConfig;

@Component
public class CircuitBreakerStateConfigLogic {

	private CircuitBreakerStateConfigDAO CircuitBreakerStateDao;
	
	public CircuitBreakerStateConfig getStateConfig(Class<? extends CircuitBreakerState> state, String circuitBreakerName) {
		String webServerName = System.getProperty("catalina.home");
		//配置量不大的情况下，一次性从数据库全部load出来再匹配
		List<CircuitBreakerStateConfig> list = CircuitBreakerStateDao.getStateConfig(state.getSimpleName(), circuitBreakerName);
		if(list == null) {
			return null;
		}
		CircuitBreakerStateConfig generalCfg = null;
		for(CircuitBreakerStateConfig cfg : list) {
			if(StringUtils.equals(webServerName, cfg.getWebServerName())) {
				return cfg;
			}
			if("*".equals(cfg.getWebServerName())) {
				generalCfg = cfg;
			}
		}
		return generalCfg;
	}

	@Autowired
	public void setCircuitBreakerStateDAO(CircuitBreakerStateConfigDAO CircuitBreakerStateDao) {
		this.CircuitBreakerStateDao = CircuitBreakerStateDao;
	}
}
