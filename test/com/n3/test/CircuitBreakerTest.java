package com.n3.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;

public class CircuitBreakerTest {

	@Test
	public void testJunit() {
		Assert.assertNotNull(new Object());
	}
	
	@Test
	public void testClosedState() throws Exception {
		int count = 20;
		final CountDownLatch latch = new CountDownLatch(count);
		ExecutorService executor = Executors.newFixedThreadPool(20);
		List<Future<?>> list = new ArrayList<Future<?>>(count);
		for(int i=0; i<count; i++) {
			Future<?> future = executor.submit(new Callable<HttpResponse>() {
				@Override
				public HttpResponse call() throws Exception {
					try {
						HttpGet request = new HttpGet("http://localhost:8080/tomcatpool/rest/service?test=1");
						HttpClient httpclient = HttpClients.createDefault();
						HttpResponse response = httpclient.execute(request);
						System.out.println(response.getStatusLine());
						return response;
					} finally {
						latch.countDown();
					}
				}
			});
			list.add(future);
		}
		latch.await();
//		for(Future<?> future : list) {
//			HttpResponse response = (HttpResponse)future.get();
//			System.out.println(response.getStatusLine());
//		}
		
		
		/*CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
			httpclient.start();
			final HttpGet request = new HttpGet("http://www.apache.org/");
			FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
				
				public void completed(final HttpResponse response) {
					latch.countDown();
					System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
				}
				
				public void failed(final Exception ex) {
					latch.countDown();
					System.out.println(request.getRequestLine() + "->" + ex);
				}
				
				public void cancelled() {
					latch.countDown();
					System.out.println(request.getRequestLine() + " cancelled");
				}
				
			};
			for(int i=0; i<count; i++) {
				httpclient.execute(request, callback);
			}
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
}
