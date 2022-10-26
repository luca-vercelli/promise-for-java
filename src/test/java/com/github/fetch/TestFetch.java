package com.github.fetch;

import static com.github.fetch.Fetch.fetch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestFetch {

	@Test
	public void testGet() throws InterruptedException {
		String[] text = new String[] { ""};
		fetch("https://www.google.com").then(response -> { text[0] = response.text(); });
		/*assertTrue(text[0].isEmpty());
		
		// ok, this test relies on Google's servers speed...
		Thread.sleep(3000);
		assertFalse(text[0].isEmpty());*/
		Thread.sleep(10000);
	}
}
