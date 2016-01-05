package com.n3.breaker.core;

import com.n3.breaker.ResponseDTO;

public class ErrorResponseDTO implements ResponseDTO {

	private Exception e;
	
	public ErrorResponseDTO(Exception e) {
		this.e = e;
	}

	@Override
	public Object getResponseEntity() {
		return null;
	}

	@Override
	public boolean isExceptionOccured() {
		return true;
	}

	@Override
	public Exception getExcetion() {
		return e;
	}

}
