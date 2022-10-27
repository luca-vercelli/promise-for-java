package com.github.fetch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.github.promise.Promise;

/**
 * Fetch an URL resource returning a Promise.
 */
public class Fetch {

	static final Logger LOGGER = Logger.getLogger("com.github.fetch");

	private Fetch() {
	}

	/**
	 * Fetch an URL resource returning a Promise.
	 */
	public static Promise<Response> fetch(String url) {
		Request request = new Request(url);
		return fetch(request);
	}

	/**
	 * Fetch an URL resource returning a Promise.
	 */
	public static Promise<Response> fetch(String url, String method, String body) {
		Request request = new Request(url);
		request.setMethod(method);
		request.setBody(body);
		return fetch(request);
	}

	/**
	 * Fetch an URL resource returning a Promise.
	 */
	public static Promise<Response> fetch(String url, String method, Map<String, String> body) {
		Request request = new Request(url);
		request.setMethod(method);
		request.setFormDataBody(body);
		return fetch(request);
	}

	/**
	 * Fetch an URL resource returning a Promise.
	 */
	public static Promise<Response> fetch(String url, String method, Object jsonBody) {
		Request request = new Request(url);
		request.setMethod(method);
		request.setJsonBody(jsonBody);
		return fetch(request);
	}

	/**
	 * Fetch an URL resource returning a Promise.
	 */
	public static Promise<Response> fetch(Request req) {
		return new Promise<>((resolve, reject) -> {
			try {
System.err.println("1");
				DataOutputStream printout;

				URL url = new URL(req.getUrl());

System.err.println("2");
				// URL connection channel.
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(req.getMethod());

System.err.println("3");
				// Let the run-time system (RTS) know that we want input.
				conn.setDoInput(true);

				// No caching, we want the real thing.
				conn.setUseCaches(false);

				// Specify the content type.
				for (Entry<String, String> entry : req.getHeaders().entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}

System.err.println("4");
				if (!"GET".equals(req.getMethod()) && !req.getBody().isEmpty()) {

					// Let the RTS know that we want to do output.
					conn.setDoOutput(true);

					// Send POST output.
					printout = new DataOutputStream(conn.getOutputStream());

					printout.writeBytes(req.getBody());
					printout.flush();
					printout.close();
				}

System.err.println("5");
				Response resp = new Response();
				resp.setStatus(conn.getResponseCode());
				resp.setHeaders(conn.getHeaderFields());

				// Get response data.
				if (resp.getStatus() <= 399) {
					resp.setBody(new DataInputStream(conn.getInputStream()));
				} else {
					resp.setBody(new DataInputStream(conn.getErrorStream()));
				}

System.err.println("6");
				resolve.accept(resp);
			} catch (MalformedURLException me) {
				reject.accept(me);
			} catch (IOException ioe) {
				reject.accept(ioe);
			}
		});
	}
}
