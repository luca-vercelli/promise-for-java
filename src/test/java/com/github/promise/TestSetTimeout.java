package com.github.promise;

import static com.github.promise.SetTimeout.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSetTimeout {

	@Test
	public void testSetTimeout() throws InterruptedException {
		boolean[] target = new boolean[] { false };
		setTimeout(() -> {
			target[0] = true;
		}, 1000);
		assertFalse(target[0]);
		Thread.sleep(1100);
		assertTrue(target[0]);
	}

	@Test
	public void testClearTimeout() throws InterruptedException {
		boolean[] target = new boolean[] { false };
		Thread t = setTimeout(() -> {
			target[0] = true;
		}, 1000);
		clearTimeout(t);

		Thread.sleep(50);
		assertFalse(t.isAlive());

		Thread.sleep(1100);
		assertFalse(target[0]);
	}
}
