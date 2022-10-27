package com.github.fetch;

import static com.github.fetch.Fetch.fetch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.promise.Promise;
import com.github.promise.Status;

public class TestFetch {

	@Test
	public void testGet() throws InterruptedException {
		Promise<String> p = fetch("https://www.google.com").thenPromise((response) -> (response.text()));

		// ok, this test relies on Google's servers speed...
		Thread.sleep(10000);
		assertEquals(Status.FULFILLED, p.getStatus());
		assertNotNull(p.getValue());
		assertFalse(p.getValue().isEmpty());
	}
}
