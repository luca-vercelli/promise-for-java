package com.github.fetch;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

public class Request {
	private String url;
	private String method = "GET";
	private String body = "";
	private String credentials = "same-origin";
	private LinkedHashMap<String, String> headers = new LinkedHashMap<>();

	public Request() {
	}

	public Request(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public LinkedHashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(LinkedHashMap<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * Create JSON body from given object
	 * 
	 * @param data
	 */
	public void setJsonBody(Object data) {
		headers.put("Content-Type", "application/json");
		this.body = new Gson().toJson(data);
	}

	/**
	 * Create traditional POST body with given attributes
	 * 
	 * @param data
	 */
	public void setFormDataBody(Map<String, String> data) {
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		StringBuffer body = new StringBuffer();
		for (Entry<String, String> entry : data.entrySet()) {
			body.append(formEncode(entry.getKey())).append('=').append(formEncode(entry.getValue()));
		}
		this.body = body.toString();
	}

	private String formEncode(String s) {
		return s == null ? "" : s.replace("&", "%%");
	}
}