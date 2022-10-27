package com.github.promise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

public class TestPromise {

	@Before
	public void setUp() {

		// FIXME does not work ?!?
		Promise.LOGGER.setLevel(Level.ALL);
	}

	@Test
	public void testResolve() throws InterruptedException {
		Promise<Integer> p = new Promise<>((resolve, reject) -> {
			resolve.accept(42);
		});

		Thread.sleep(10);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals(Integer.valueOf(42), p.getValue());
	}

	@Test
	public void testReject() throws InterruptedException {
		Promise<Object> p = new Promise<>((resolve, reject) -> {
			throw new RuntimeException("foo");
		});

		Thread.sleep(10);

		assertEquals(Status.REJECTED, p.getStatus());
		assertNotNull(p.getError());
		assertEquals("foo", p.getError().getMessage());
	}

	@Test
	public void testTimeout() throws InterruptedException {
		Promise<Integer> p = new ExamplePromise(42, 1000);
		assertEquals(Status.PENDING, p.getStatus());

		Thread.sleep(1200);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals(Integer.valueOf(42), p.getValue());
	}

	@Test
	public void testThen() throws InterruptedException {
		Promise<String> p = new Promise<Integer>((resolve, reject) -> {
			resolve.accept(42);
		}) //
				.then((response) -> (response + 1)) //
				.then((response) -> (response.toString())) //
				.then((response) -> (response + "A"));

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals("43A", p.getValue());
	}
}
