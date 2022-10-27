package com.github.promise;

import java.util.LinkedList;
import java.util.List;

/**
 * An Exception aggregating other Exception's (not Error's!)
 */
public class AggregateException extends Exception {
	private static final long serialVersionUID = -6677541369658385881L;
	private List<Exception> exceptions = new LinkedList<>();

	/**
	 * Constructor
	 * 
	 * @param exceptions
	 */
	public AggregateException(List<Exception> exceptions) {
		addAllExceptions(exceptions);
	}

	/**
	 * Constructor
	 * 
	 * @param exceptions
	 */
	public AggregateException(Exception... exceptions) {
		for (Exception ex : exceptions) {
			addException(ex);
		}
	}

	protected void addAllExceptions(List<Exception> exceptions) {
		for (Exception ex : exceptions) {
			addException(ex);
		}
	}

	/**
	 * Add an Exception to internal list, provided that it is not null and it is not
	 * itself an AggregateException, in which case add all internal aggregated
	 * exceptions
	 * 
	 * @param ex
	 */
	protected void addException(Exception ex) {
		if (ex != null) {
			if (ex instanceof AggregateException) {
				List<Exception> list = ((AggregateException) ex).getExceptions();
				addAllExceptions(list);
			} else {
				this.exceptions.add(ex);
			}
		}
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