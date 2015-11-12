package com.n3.breaker;

import java.util.Random;

public class TestClient {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		CircuitBreaker circuitBreaker = new CircuitBreaker(null);
		ClosedState state = new ClosedState(circuitBreaker);
		circuitBreaker.setState(state);
		for(int i=0; i<1000; i++) {
//			Thread.sleep(new Random().nextInt(30));
//			new ClintThread(circuitBreaker, i).start();
		}
		System.out.println(state.getName());
		state.destroy();
	}

	static class ClintThread extends Thread {
		private CircuitBreaker circuitBreaker;
		private int obj;
		public ClintThread(CircuitBreaker circuitBreaker, int obj) {
			this.circuitBreaker = circuitBreaker;
			this.obj = obj;
		}
		@Override
		public void run() {
			circuitBreaker.handleInCurrentState(obj, null);
		}
	}
}
