package com.n3.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.n3.dao.CircuitBreakerStateConfigDAO;

public class SpringComponentTest {

	private static ApplicationContext context;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetStateConfig() {
		CircuitBreakerStateConfigDAO dao = context.getBean(CircuitBreakerStateConfigDAO.class);
		dao.getStateConfig("ClosedState", "oppc");
	}

}
