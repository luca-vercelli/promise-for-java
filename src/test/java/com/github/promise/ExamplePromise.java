package com.github.promise;

public class ExamplePromise extends Promise<Integer> {

	/**
	 * Return a given integer constant after a specified amount of time
	 * 
	 * @param value
	 * @param ms
	 */
	public ExamplePromise(int value, int ms) {
		super((resolve, reject) -> {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			resolve.accept(value);
		});
	}

}
