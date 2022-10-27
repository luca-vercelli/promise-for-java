package com.github.promise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

		Thread.sleep(1000);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals("43A", p.getValue());
	}

	@Test
	public void testThenParalleli() throws InterruptedException {
		Promise<Integer> p = Promise.resolve(42);
		Promise<Integer> p2 = p.then((response) -> (response + 1));
		Promise<Integer> p3 = p.then((response) -> (response + 1));

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p2.getStatus());
		assertEquals(Status.FULFILLED, p3.getStatus());
		assertEquals((Integer) 43, p2.getValue());
		assertEquals((Integer) 43, p3.getValue());
	}

	@Test
	public void testThenWithException() throws InterruptedException {
		Exception e = new RuntimeException("foo");
		Promise<Integer> p = Promise.reject(e);
		List<Exception> errors = new ArrayList<>();
		Promise<Integer> p2 = p.then((x) -> (x * 2)).then((x) -> (x + 2), (err) -> {
			errors.add(err);
		});
		Thread.sleep(500);

		assertEquals(Status.REJECTED, p2.getStatus());
		assertEquals(e, p2.getError());
		assertNull(p2.getValue());
		assertEquals(1, errors.size());
		assertTrue(errors.contains(e));
	}

	@Test
	public void testThenCatch() throws InterruptedException {
		Exception e = new RuntimeException("foo");
		Promise<Integer> p = Promise.reject(e);
		List<Exception> errors = new ArrayList<>();
		Promise<Integer> p2 = p.then((x) -> (x * 2)).thenCatch((err) -> {
			errors.add(err);
		});
		Thread.sleep(500);

		assertEquals(Status.REJECTED, p2.getStatus());
		assertEquals(e, p2.getError());
		assertNull(p2.getValue());
		assertEquals(1, errors.size());
		assertTrue(errors.contains(e));
	}

	@Test
	public void testThenFinally() throws InterruptedException {
		Exception e = new RuntimeException("foo");
		Promise<Integer> p = Promise.reject(e);
		List<Boolean> success = new ArrayList<>();
		Promise<Integer> p2 = p.then((x) -> (x * 2)).thenFinally(() -> {
			success.add(true);
		});
		Thread.sleep(500);

		assertEquals(Status.REJECTED, p2.getStatus());
		assertEquals(e, p2.getError());
		assertNull(p2.getValue());
		assertEquals(1, success.size());
		assertTrue(success.contains(true));
	}

	@Test
	public void testThenPromise() throws InterruptedException {
		Promise<Integer> p = Promise.resolve(42).thenPromise((x) -> (new Promise<>((resolve, reject) -> {
			resolve.accept(x + 1);
		})));
		Thread.sleep(500);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertEquals((Integer) 43, p.getValue());
	}
}
