package com.github.fetch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

import com.github.promise.Promise;

public class Fetch {

	private Fetch() {
	}

	public static Promise<Response> fetch(String url) {
		Request request = new Request();
		request.url = url;
		return fetch(request);
	}

	public static Promise<Response> fetch(String url, String body) {
		Request request = new Request();
		request.url = url;
		request.body = body;
		return fetch(request);
	}

	public static Promise<Response> fetch(Request req) {
		return new Promise<>((resolve, reject) -> {
			try {
				DataOutputStream printout;

				URL url = new URL(req.url);

				// URL connection channel.
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(req.method);

				// Let the run-time system (RTS) know that we want input.
				conn.setDoInput(true);

				// No caching, we want the real thing.
				conn.setUseCaches(false);

				// Specify the content type.
				for (Entry<String, String> entry : req.headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}

				if (!"GET".equals(req.method) && !req.body.isEmpty()) {

					// Let the RTS know that we want to do output.
					conn.setDoOutput(true);

					// Send POST output.
					printout = new DataOutputStream(conn.getOutputStream());

					printout.writeBytes(req.body);
					printout.flush();
					printout.close();
				}

				Response resp = new Response();
				resp.status = conn.getResponseCode();
				resp.headers = conn.getHeaderFields();

				// Get response data.
				if (resp.status <= 399) {
					resp.body = new DataInputStream(conn.getInputStream());
				} else {
					resp.body = new DataInputStream(conn.getErrorStream());
				}

				resolve.accept(resp);
			} catch (MalformedURLException me) {
				reject.accept(me);
			} catch (IOException ioe) {
				reject.accept(ioe);
			}
		});
	}
}
