package com.n3.breaker;

import java.util.concurrent.Callable;

public interface CircuitBreaker {

	public int getConcurrency();
	
	public int getBufferSize();
	
	public void destroy();
	
	public ResponseDTO syncHandle(Callable<ResponseDTO> task, Object requestEntity, long timeoutSeconds) throws Exception;
	
//	public void asyncHandle(Callable<?> task, AsyncResponse asyncResponse) throws Exception ;
	
//	public boolean transferToState(CircuitBreakerState newState);
	
}
