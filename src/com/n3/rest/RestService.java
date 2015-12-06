package com.n3.rest;

import java.util.Random;

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
				
		Thread.sleep(1500L);
		boolean result = new Random().nextBoolean();
		logger.debug("处理完成：requestEntity="+i+" result="+result);
		return String.valueOf(result);
	}
	
}
