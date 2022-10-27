package com.github.promise;

/**
 * Imlements a single static method <code>setTimeout</code>
 */
public class SetTimeout {

	private SetTimeout() {
	}

	/**
	 * Execute a given <code>Runnable</code> in a new Thread, after a given amount
	 * of time.
	 * 
	 * @param runnable
	 * @param ms
	 */
	public static Thread setTimeout(Runnable runnable, int ms) {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(ms);
				} catch (InterruptedException e) {
					// clearInterval was called
					return;
				}
				runnable.run();
			}
		};
		t.start();
		return t;
	}

	/**
	 * Ask a thread to stop. Just an alias for <code>Thread.interrupt()</code>, with
	 * a name remembering JavaScript equivalent.
	 * 
	 * @param t
	 */
	public static void clearTimeout(Thread t) {
		t.interrupt();
	}
}
