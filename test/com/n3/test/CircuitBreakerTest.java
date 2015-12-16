package com.n3.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
		int count = 10;
		final AtomicInteger requestEntity = new AtomicInteger();
		final CountDownLatch latch = new CountDownLatch(count);
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<?>> list = new ArrayList<Future<?>>(count);
		for(int i=0; i<count; i++) {
			Future<?> future = executor.submit(new Callable<HttpResponse>() {
				@Override
				public HttpResponse call() throws Exception {
					try {
						HttpGet request = new HttpGet("http://localhost:8080/tomcatpool/rest/service?test="+requestEntity.incrementAndGet());
						HttpClient httpclient = HttpClients.createDefault();
						HttpResponse response = httpclient.execute(request);
						StringBuffer sb = new StringBuffer();
						BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						while(br.ready()) {
							sb.append(br.readLine());
						}
						System.out.println(response.getStatusLine()+"---"+sb.toString());
						return response;
					} finally {
						latch.countDown();
					}
				}
			});
			list.add(future);
		}
		latch.await();
	}
}
