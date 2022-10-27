package com.github.promise;

import java.util.LinkedList;
import java.util.List;

/**
 * An Exception aggregating other Exception's (not Error's!)
 */
public class AggregateException extends Exception {
	private static final long serialVersionUID = -6677541369658385881L;
	private List<Exception> exceptions = new LinkedList<>();

	public AggregateException(List<Exception> exceptions) {
		this.exceptions.addAll(exceptions);
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}

	@Override
	public String getMessage() {
		StringBuffer msg = new StringBuffer();
		String comma = "";
		for (Throwable t : exceptions) {
			msg.append(comma).append(t.getMessage());
			comma = ";";
		}
		return msg.toString();
	}
}