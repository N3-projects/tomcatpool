package com.n3.breaker;

import javax.ws.rs.core.UriInfo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CircuitBreakerAspect implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerAspect.class);
	private ApplicationContext applicationContext;
	
	public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("just a test");
		Object str = pjp.getArgs()[0];
		UriInfo uriInfo = (UriInfo)str;
		int i = Integer.parseInt(uriInfo.getQueryParameters().getFirst("test"));
		if(i>0) {
			// get CircuitBreaker from spring context
			CircuitBreaker circuitBreaker = (CircuitBreaker)applicationContext.getBean("oppcCircuitBreaker");
			
			ResponseDTO dto = circuitBreaker.syncHandle(new InternalRequest(pjp), i, 10L);
			return dto.isExceptionOccured() ? dto.getExcetion().getMessage() : dto
					.getResponseEntity().toString();
		}
		System.out.println();
		return pjp.proceed();
	}

	private class InternalRequest implements RequestTask {

		ProceedingJoinPoint invocation;
		
		InternalRequest(ProceedingJoinPoint invocation) {
			this.invocation = invocation;
		}
		
		@Override
		public ResponseDTO call() throws Exception {
			try {
				Object result = invocation.proceed();
				return new InternalResponse(result,"true".equalsIgnoreCase(result.toString()));
			} catch (Throwable e) {
				logger.error("交易失败", e);
				throw new Exception(e);
			}
		}

		@Override
		public Object getRequestEntity() {
			return invocation;
		}
	
	}
	
	class InternalResponse implements ResponseDTO {

		final Object entity;
		final boolean isException;
		
		public InternalResponse(Object result, boolean isException) {
			this.entity = result;
			this.isException = isException;
		}

		@Override
		public Object getResponseEntity() {
			return entity;
		}

		@Override
		public boolean isExceptionOccured() {
			return !isException;
		}

		@Override
		public Exception getExcetion() {
			return isException ? null : new Exception("some exception occured");
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
