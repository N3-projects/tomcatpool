package com.n3.rest;

import java.util.Random;
import java.util.concurrent.Callable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.RequestTask;
import com.n3.breaker.ResponseDTO;
import com.n3.util.ApplicationContextHolder;

@Component
@Path("/service")
public class RestService {

	private static final Logger logger = LoggerFactory.getLogger(RestService.class);
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String service(@Context UriInfo uriInfo) throws Exception {
		
		// some business logic
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Integer i = Integer.valueOf(queryParams.getFirst("test"));
				
		// get CircuitBreaker from spring context
		CircuitBreaker circuitBreaker = (CircuitBreaker) ApplicationContextHolder
				.getApplicationContext().getBean("oppcCircuitBreaker");
		
		
		return circuitBreaker.syncHandle(new InternalRequest(i), i, 10L).toString();
	}
	
	private class InternalRequest implements RequestTask {

		Object requestEntity;
		
		InternalRequest(Object requestEntity) {
			this.requestEntity = requestEntity;
		}
		
		@Override
		public ResponseDTO call() throws Exception {
			Boolean result = null;
			try {
				Thread.sleep(2000L);
				result = new Random().nextBoolean();
				logger.debug("ClosedState 处理完成：requestEntity="+requestEntity+" result="+result);
//				ClosedState.this.writeback(requestEntity,result);
				return result;
			} catch (InterruptedException e) {
				logger.error("交易中断：requestEntity="+requestEntity);
				throw e;
			} catch (Exception e) {
				logger.error("交易失败：requestEntity="+requestEntity, e);
				throw e;
			}
		}

		@Override
		public Object getRequestEntity() {
			return requestEntity;
		}
	
	}
}
