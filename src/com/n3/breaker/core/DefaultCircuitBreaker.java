package com.n3.breaker.core;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n3.breaker.CircuitBreaker;
import com.n3.breaker.RequestHandler;
import com.n3.breaker.ResponseDTO;

public class DefaultCircuitBreaker implements CircuitBreaker {

	private static final Logger logger = LoggerFactory.getLogger(DefaultCircuitBreaker.class);
	private final String name;
	private final StateManager stateManager;
	private volatile int concurrency;
	private volatile int bufferSize;
	private volatile CircuitBreakerState state;
	
	public DefaultCircuitBreaker(String name, int concurrency, int bufferSize, StateManager stateManager) {
		this.name = name;
		this.concurrency = concurrency;
		this.bufferSize = bufferSize;
		this.stateManager = stateManager;
	}
	
	public ResponseDTO syncHandle(RequestHandler handler, long timeoutSeconds)
			throws RejectedExecutionException, TimeoutException, InterruptedException, ExecutionException {
		final CircuitBreakerState currentState = state;
		if(handler==null) {
			throw new IllegalArgumentException("requestHandler can not be null");
		}
		Future<ResponseDTO> future = null;
		try {
			future = currentState.handle(handler);
		} catch(RejectedExecutionException e) {
			logger.error(currentState.getClass().getSimpleName()+"拒绝请求:" + handler.getRequestEntity()+" RejectedExecutionException:"+e.getMessage());
			throw e;
		} 
		ResponseDTO result = null;
		try {
			result = future.get(timeoutSeconds, TimeUnit.SECONDS);
		} catch (CancellationException e) {
			logger.error("请求取消:" + handler.getRequestEntity());
			result = new ErrorResponseDTO(e);
			throw e;
		} catch (TimeoutException e) {
			logger.error("请求超时:" + handler.getRequestEntity());
			result = new ErrorResponseDTO(e);
			throw e;
		} catch (InterruptedException e) {
			logger.error("请求中断:" + handler.getRequestEntity(), e);
			result = new ErrorResponseDTO(e);
			throw e;
		} catch (ExecutionException e) {
			logger.error("处理失败:" + handler.getRequestEntity(), e);
			result = new ErrorResponseDTO(e);
			throw e;
		} finally {
			future.cancel(true);
			currentState.writeback(handler.getRequestEntity(), result);
		}
		return result;
	}
	
//	TODO
//	/**
//	 * required jersey2.2 tomcat7
//	 * @param task
//	 * @param asyncResponse
//	 * @return
//	 */
//	public void asyncHandle(Callable<?> task, AsyncResponse asyncResponse) throws Exception {
//		return state.handle(task);
//	}

	protected synchronized boolean transferToClosedState() {
		if(state!=null && state instanceof ClosedState) {
			logger.info("circuit breaker named "+name+" is already in closed state, ignore transfer command");
			return false;
		}
		CircuitBreakerState newState = null;
		try {
			newState = stateManager.newClosedState(this);
		} catch (Exception e) {
			logger.error("读取ClosedState配置失败", e);
		}
		if(newState == null) {
			newState = new ClosedState(this);
		}
		if (state != null) {
			state.destroy();
		}
		state = newState;
		logger.info("CircuitBreaker "+name+" 已切换至ClosedState");
		return true;
	}
	
	protected synchronized boolean transferToOpenState() {
		if(state!=null && state instanceof OpenState) {
			logger.info("circuit breaker named "+name+" is already in open state, ignore transfer command");
			return false;
		}
		CircuitBreakerState newState = null;
		try {
			newState = stateManager.newOpenState(this);
		} catch (Exception e) {
			logger.error("读取OpenState配置失败", e);
		}
		if(newState == null) {
			newState = new OpenState(this);
		}
		if(state != null) {
			state.destroy();
		}
		state = newState;
		logger.info("CircuitBreaker "+name+" 已切换至OpenState");
		return true;
	}
	
	protected synchronized boolean transferToHalfOpenState() {
		if(state!=null && state instanceof HalfOpenState) {
			logger.info("circuit breaker named "+name+" is already in open state, ignore transfer command");
			return false;
		}
		CircuitBreakerState newState = null;
		try {
			newState = stateManager.newHalfOpenState(this);
		} catch (Exception e) {
			logger.error("读取HalfOpenState配置失败", e);
		}
		if(newState == null) {
			newState = new HalfOpenState(this);
		}
		if (state != null) {
			state.destroy();
		}
		state = newState;
		logger.info("CircuitBreaker "+name+" 已切换至HalfOpenState");
		return true;
	}
	
	public void destroy() {
		if(state != null) {
			state.destroy();
		}
	}

	public int getConcurrency() {
		return concurrency;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void init() {
		if(state == null) {
			transferToClosedState();
		}
	}

}
