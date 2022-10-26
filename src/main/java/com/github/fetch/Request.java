package com.github.fetch;

import java.util.LinkedHashMap;

public class Request {
	public String url;
	public String method = "GET";
	public String body = "";
	public String credentials = "same-origin";
	public LinkedHashMap<String, String> headers = new LinkedHashMap<>();

	public Request() {
	}

	public Request(String url) {
		this.url = url;
	}
}