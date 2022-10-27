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
	public void testGetText() throws InterruptedException {
		final String URL = "https://www.google.com";
		Promise<String> p = fetch(URL).thenPromise((response) -> (response.text()));

		Thread.sleep(2000);
		assertEquals(Status.FULFILLED, p.getStatus());
		assertNotNull(p.getValue());
		assertFalse(p.getValue().isEmpty());
	}

	@Test
	public void testGetJson() throws InterruptedException {
		final String URL = "https://api.publicapis.org/entries";

		Promise<PublicApiListBean> p = fetch(URL).thenPromise((response) -> (response.json(PublicApiListBean.class)));

		Thread.sleep(2000);
		assertEquals(Status.FULFILLED, p.getStatus());
		assertNotNull(p.getValue());
		assertFalse(p.getValue().getEntries().isEmpty());
	}
}
