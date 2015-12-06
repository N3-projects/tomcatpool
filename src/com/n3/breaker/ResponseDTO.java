package com.n3.breaker;

public interface ResponseDTO {

	public Object getResponseEntity();
	
	public boolean isExceptionOccured();
	
	public Exception getExcetion();
}
