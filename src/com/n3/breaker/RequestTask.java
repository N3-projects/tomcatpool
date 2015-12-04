package com.n3.breaker;

import java.util.concurrent.Callable;

public interface RequestTask extends Callable<ResponseDTO> {

	public Object getRequestEntity();
}
