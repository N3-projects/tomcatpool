package com.n3.breaker;

public class CommonExceptionResponse implements ResponseDTO {

	private Exception ex;
	private Object responseEntity;
	
	public CommonExceptionResponse(Exception ex) {
		super();
		this.ex = ex;
	}

	@Override
	public Object getResponseEntity() {
		return responseEntity;
	}

	@Override
	public boolean isExceptionOccured() {
		return true;
	}

	@Override
	public Exception getExcetion() {
		return ex;
	}

	public void setResponseEntity(Object responseEntity) {
		this.responseEntity = responseEntity;
	}

}
