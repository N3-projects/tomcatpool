package com.n3.breaker;

import java.util.concurrent.Callable;

public interface RequestHandler extends Callable<ResponseDTO> {

	public Object getRequestEntity();
}
