package com.n3.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;

import com.n3.breaker.CircuitBreaker;
import com.n3.util.ApplicationContextHolder;

@Component
@Path("/service")
public class RestService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String service(@Context UriInfo uriInfo) {
		
		// some business logic
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Integer i = Integer.valueOf(queryParams.getFirst("test"));
				
		// get CircuitBreaker from spring context
		CircuitBreaker circuitBreaker = (CircuitBreaker) ApplicationContextHolder
				.getApplicationContext().getBean("oppcCircuitBreaker");
		circuitBreaker.handleInCurrentState(i, null);
		
		return "ok";
	}
	
}
