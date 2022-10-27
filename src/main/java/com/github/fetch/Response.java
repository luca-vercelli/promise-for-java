package com.github.fetch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.github.promise.Promise;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Response {
	private String url;
	private int status = -1;
	private InputStream body;
	private Map<String, List<String>> headers = new LinkedHashMap<>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public InputStream getBody() {
		return body;
	}

	public void setBody(InputStream body) {
		this.body = body;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	/**
	 * True if status is between 200 and 299.
	 */
	public boolean ok() {
		return status >= 200 && status < 300;
	}

	/**
	 * Returns a promise that resolves with a text representation of the response
	 * body
	 */
	public Promise<String> text() {
		return text(StandardCharsets.UTF_8.name());
	}

	/**
	 * Returns a promise that resolves with a text representation of the response
	 * body
	 */
	public Promise<String> text(String charset) {
		return new Promise<>((resolve, reject) -> {
			try {
				resolve.accept(IOUtils.toString(this.body, charset));
			} catch (IOException e) {
				reject.accept(e);
			}
		});
	}

	/**
	 * Returns a promise that resolves with a text representation of the response
	 * body
	 */
	public Promise<byte[]> arrayBuffer() {
		return new Promise<>((resolve, reject) -> {
			try {
				resolve.accept(IOUtils.toByteArray(this.body));
			} catch (IOException e) {
				reject.accept(e);
			}
		});
	}

	/**
	 * Returns a promise that resolves with the result of parsing the response body
	 * text as JSON.
	 * 
	 * @throws IOException
	 * @throws JsonSyntaxException
	 */
	public <W> Promise<W> json(Class<W> type) {
		return text().then((text) -> (new Gson().fromJson(text, type)));
	}
}