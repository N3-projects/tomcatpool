package com.n3.breaker.core;

import java.util.concurrent.Future;

import com.n3.breaker.RequestHandler;
import com.n3.breaker.ResponseDTO;

public interface CircuitBreakerState {
	
	public Future<ResponseDTO> handle(RequestHandler task);
	
	public void destroy();
	
	public void writeback(Object requestEntity, ResponseDTO responseDTO);
	
}
