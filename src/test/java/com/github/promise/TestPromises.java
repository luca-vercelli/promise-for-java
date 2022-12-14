package com.github.promise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TestPromises {

	@Test
	public void testAll() throws InterruptedException {
		Promise<Integer> p1 = Promise.resolve(42);
		Promise<Integer> p2 = Promise.resolve(43);
		Promise<List<Integer>> p = Promise.all(p1, p2);

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p.getStatus());
		List<Integer> l = p.getValue();
		assertNotNull(l);
		assertEquals(2, l.size());
		assertTrue(l.contains(42));
		assertTrue(l.contains(43));
	}

	@Test
	public void testAny() throws InterruptedException {
		Promise<Integer> p1 = Promise.resolve(42);
		Promise<Integer> p2 = Promise.resolve(43);
		Promise<Integer> p = Promise.any(p1, p2);

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertTrue(p.getValue().equals(42) || p.getValue().equals(43));
	}

	@Test
	public void testAllSettled() throws InterruptedException {
		Exception e = new Exception("foo");
		Promise<Integer> p1 = Promise.resolve(42);
		Promise<Integer> p2 = Promise.reject(e);
		Promise<List<Object>> p = Promise.allSettled(p1, p2);

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p.getStatus());
		List<Object> l = p.getValue();
		assertNotNull(l);
		assertEquals(2, l.size());
		assertTrue(l.contains(42));
		assertTrue(l.contains(e));
	}

	@Test
	public void testRace() throws InterruptedException {
		Exception e = new Exception("foo");
		Promise<Integer> p1 = Promise.resolve(42);
		Promise<Integer> p2 = Promise.reject(e);
		Promise<Object> p = Promise.race(p1, p2);

		Thread.sleep(100);

		assertEquals(Status.FULFILLED, p.getStatus());
		assertTrue(p.getValue().equals(42) || p.getValue().equals(e));
	}
}
