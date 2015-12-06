package com.n3.breaker;

import java.io.Serializable;

import javax.ws.rs.core.UriInfo;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n3.util.ApplicationContextHolder;

public class CircuitBreakerInterceptor implements MethodInterceptor, Serializable{

	private static final long serialVersionUID = -6594338383457482623L;
	private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerInterceptor.class);
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("just a test");
//		String str = (String) RequestContextHolder.currentRequestAttributes().getAttribute("circuitBreakerName", 0);
		Object str = invocation.getArguments()[0];
		UriInfo uriInfo = (UriInfo)str;
		int i = Integer.parseInt(uriInfo.getQueryParameters().getFirst("test"));
		if(i>0) {
			// get CircuitBreaker from spring context
			CircuitBreaker circuitBreaker = (CircuitBreaker) ApplicationContextHolder
					.getApplicationContext().getBean("oppcCircuitBreaker");
			
			ResponseDTO dto = circuitBreaker.syncHandle(new InternalRequest(invocation), i, 10L);
			return dto.isExceptionOccured() ? dto.getExcetion().getMessage() : dto
					.getResponseEntity().toString();
		}
		System.out.println();
		return invocation.proceed();
	}

	private class InternalRequest implements RequestTask {

		MethodInvocation invocation;
		
		InternalRequest(MethodInvocation invocation) {
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
	
}
