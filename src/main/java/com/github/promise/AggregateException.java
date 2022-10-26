package com.github.promise;

import java.util.LinkedList;
import java.util.List;

/**
 * An exception aggregating other exceptions
 */
public class AggregateException extends Throwable {
	private static final long serialVersionUID = -6677541369658385881L;
	private List<Throwable> exceptions = new LinkedList<>();

	public AggregateException(List<Throwable> exceptions) {
		this.exceptions.addAll(exceptions);
	}

	public List<Throwable> getExceptions() {
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