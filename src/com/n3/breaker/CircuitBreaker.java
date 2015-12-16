package com.n3.breaker;

public interface CircuitBreaker {

	public int getConcurrency();
	
	public int getBufferSize();
	
	public void init();
	
	public void destroy();
	
	public ResponseDTO syncHandle(RequestHandler handler, long timeoutSeconds) throws Exception;
	
	public String getName();
	
//	public void asyncHandle(Callable<?> task, AsyncResponse asyncResponse) throws Exception ;
	
}
