package com.github.fetch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.github.promise.Promise;

public class Response {
	public String url;
	public int status = 0;
	public InputStream body;
	public Map<String, List<String>> headers = new LinkedHashMap<>();

	/**
	 * True if status is between 200 and 299.
	 */
	public boolean ok() {
		return status >= 200 && status < 300;
	}

	/**
	 * Returns a promise that resolves with a text representation of the response
	 * body
	 * 
	 * @throws IOException
	 */
	public String text() {
		try {
			return is2string(this.body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a promise that resolves with the result of parsing the response body
	 * text as JSON.
	 * 
	 * @throws IOException
	 * @throws JsonSyntaxException
	 */
	public <W> W json(Class<W> type) {
		return new Gson().fromJson(text(), type);
	}

	/**
	 * Load whole <code>InputStream</code> content into a UTF8 String.
	 * 
	 * @param body
	 * @return
	 * @throws IOException
	 */
	private static String is2string(InputStream body) throws IOException {
		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(
				new InputStreamReader(body, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}
		return textBuilder.toString();
	}
}