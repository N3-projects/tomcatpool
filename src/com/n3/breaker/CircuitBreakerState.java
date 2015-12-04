package com.n3.breaker;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface CircuitBreakerState {

	public Future<ResponseDTO> handle(Callable<ResponseDTO> task);
	
	public void destroy();
	
	public void writeback(Object requestEntity, ResponseDTO responseDTO);
}
