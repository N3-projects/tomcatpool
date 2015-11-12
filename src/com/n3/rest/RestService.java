package com.n3.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.ClosedState;


public class RestService {

	public Response service(int request) {
		
		// some business logic
		
		// get CircuitBreaker from spring context
		CircuitBreaker circuitBreaker = new CircuitBreaker("oppc");
		circuitBreaker.setState(new ClosedState(circuitBreaker));
		circuitBreaker.handleInCurrentState(request, null);
		circuitBreaker.getState().destroy();
		return Response.ok(null, MediaType.APPLICATION_XML/* + RsConstants.CHARSET_XML*/).build();
	}
	public static void main(String[] args) {
		RestService rs = new RestService();
		rs.service(3);
	}
}
