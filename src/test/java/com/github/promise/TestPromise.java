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
		Promise<Integer> p = new Promise<Integer>((resolve, reject) -> {
			resolve.accept(42);
		}).then((response) -> (response + 1)).then((response) -> (response + 1));

		Thread.sleep(50);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals(Integer.valueOf(44), p.getValue());
	}

	@Test
	public void testAll() throws InterruptedException {
		Promise<Integer> p1 = new ExamplePromise(42, 0);
		Promise<Integer> p2 = new ExamplePromise(43, 0);
		List<Integer> result = new ArrayList<>();

		Promise.all(p1, p2).then(l -> {
			result.addAll(l);
		});

		Thread.sleep(500);

		assertEquals(2, result.size());
		assertTrue(result.contains(42));
		assertTrue(result.contains(43));
	}

	@Test
	public void testAny() throws InterruptedException {
		Promise<Integer> p1 = new ExamplePromise(42, 0);
		Promise<Integer> p2 = new ExamplePromise(43, 0);
		List<Integer> result = new ArrayList<>();

		Promise.any(p1, p2).then(i -> {
			Promise.LOGGER.info("XXX");
			result.add(i);
		});

		Thread.sleep(500);

		assertEquals(1, result.size());
		assertTrue(result.contains(42) || result.contains(43));
	}
}
