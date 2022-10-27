package com.github.promise;

import static com.github.promise.SetTimeout.setTimeout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Promise represent a value that will be get in the future, or an Exception
 * that will be thrown in the future.
 * 
 * @see https://www.promisejs.org/implementing/
 * @param <T>
 */
public class Promise<T> {

	protected Status status;
	protected T value;
	protected Throwable error;
	protected List<Handler> handlers = new LinkedList<>();

	static final Logger LOGGER = Logger.getLogger("com.github.promise");

	/**
	 * Create new promise
	 * 
	 * @param fn a consumer that takes two argument (resolve, reject) as by
	 *           specification of Promise
	 */
	public Promise(BiConsumer<Consumer<T>, Consumer<Throwable>> fn) {
		status = Status.PENDING;
		LOGGER.fine("Promise constructor BEGIN " + Thread.currentThread().getName());
		Consumer<T> fulfill = (value) -> {
			Promise.this.value = value;
			Promise.this.status = Status.FULFILLED;
			LOGGER.info("Promise FULFILLED " + Thread.currentThread().getName());
		};
		Consumer<Throwable> reject = (error) -> {
			this.error = error;
			this.status = Status.REJECTED;
			LOGGER.info("Promise REJECTED " + Thread.currentThread().getName());
		};
		Consumer<T> resolve = (value) -> {
			try {
				fulfill.accept(value);
			} catch (Exception e) {
				reject.accept(e);
			}
		};
		setTimeout(() -> {
			doResolve(fn, resolve, reject);
		}, 0);
		LOGGER.fine("Promise constructor END " + Thread.currentThread().getName());
	}

	/**
	 * Create new promise from a CompletableFuture
	 * 
	 * @param future
	 */
	public Promise(CompletableFuture<T> future) {
		this((resolve, reject) -> {
			try {
				resolve(future.get());
			} catch (InterruptedException | ExecutionException e) {
				reject(e);
			}
		});
	}

	/**
	 * Return promise current status: PENDING/RESOLVED/REJECTED
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Return promise current value (if resolved)
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Return promise current value (if rejected)
	 */
	public Throwable getError() {
		return error;
	}

	private static final class BooleanHolder {
		public boolean value;
	}

	/**
	 * Take a potentially misbehaving resolver function and make sure onFulfilled
	 * and onRejected are only called once.
	 *
	 * Makes no guarantees about asynchrony.
	 *
	 * @param fn          A resolver function that may not be trusted
	 * @param onFulfilled
	 * @param onRejected
	 */
	protected static <W> void doResolve(BiConsumer<Consumer<W>, Consumer<Throwable>> fn, Consumer<W> onFulfilled,
			Consumer<Throwable> onRejected) {
		// we use boolean[] instead of boolean to avoid error "Local variable defined in
		// an enclosing scope must be final or effectively final"
		BooleanHolder done = new BooleanHolder();
		done.value = false;
		try {
			fn.accept((value) -> {
				if (done.value)
					return;
				done.value = true;
				onFulfilled.accept(value);
			}, (error) -> {
				if (done.value)
					return;
				done.value = true;
				onRejected.accept(error);
			});
		} catch (RuntimeException ex) {
			if (done.value)
				return;
			done.value = true;
			onRejected.accept(ex);
		}
	}

	public class Handler {
		public Handler() {
		}

		public Handler(Consumer<T> onFulfilled, Consumer<Throwable> onRejected) {
			this.onFulfilled = onFulfilled;
			this.onRejected = onRejected;
		}

		public Consumer<T> onFulfilled;
		public Consumer<Throwable> onRejected;
	}

	protected void handle(Handler handler) {
		if (status == Status.PENDING) {
			handlers.add(handler);
		} else {
			if (status == Status.FULFILLED && handler.onFulfilled != null) {
				handler.onFulfilled.accept(value);
			}
			if (status == Status.REJECTED && handler.onRejected != null) {
				handler.onRejected.accept(error);
			}
		}
	}

	public <W> Promise<W> then(Function<T, W> onFulfilled, Consumer<Throwable> onRejected) {
		Promise<T> self = this;
		return new Promise<>((resolve, reject) -> {
			self.done((result) -> {
				if (onFulfilled != null) {
					try {
						resolve.accept(onFulfilled.apply(result));
					} catch (RuntimeException ex) {
						reject.accept(ex);
					}
				} else {
					resolve(result);
				}
			}, (error) -> {
				if (onRejected != null) {
					try {
						onRejected.accept(error);
					} catch (RuntimeException ex) {
						reject.accept(ex);
					}
				} else {
					reject.accept(error);
				}
			});
		});
	}

	/**
	 * Shortcut for <code>then(onFulfilled, null)</code>
	 * 
	 * @param onFulfilled
	 * @return
	 */
	public <W> Promise<W> then(Function<T, W> onFulfilled) {
		return then(onFulfilled, null);
	}

	public Promise<T> then(Consumer<T> onFulfilled, Consumer<Throwable> onRejected) {
		Promise<T> that = this;
		return then((result) -> {
			if (onFulfilled != null) {
				onFulfilled.accept(result);
			}
			return that.getValue();
		}, onRejected);
	}

	/**
	 * Shortcut for <code>then(onFulfilled, null)</code>
	 * 
	 * @param onFulfilled
	 * @return
	 */
	public Promise<T> then(Consumer<T> onFulfilled) {
		return then(onFulfilled, null);
	}

	/**
	 * Special case of <code>then</code> where the return type of
	 * <code>onFulfilled</code> is another <code>onPromise</code>
	 * 
	 * @param <W>
	 * @param onFulfilled
	 * @param onRejected
	 * @return
	 */
	public <W> Promise<W> thenPromise(Function<T, Promise<W>> onFulfilled, Consumer<Throwable> onRejected) {
		Promise<T> orig = this;
		return new Promise<>((resolve, reject) -> {
			orig.then(onFulfilled, onRejected).then((promise) -> {
				promise.then((w) -> {
					resolve.accept(w);
				}, (err) -> {
					reject.accept(err);
				});
			});
		});
	}

	/**
	 * Special case of <code>then</code> where the return type of
	 * <code>onFulfilled</code> is another <code>onPromise</code>
	 * 
	 * @param <W>
	 * @param onFulfilled
	 * @return
	 */
	public <W> Promise<W> thenPromise(Function<T, Promise<W>> onFulfilled) {
		return thenPromise(onFulfilled, null);
	}

	/**
	 * A shortcut for <code>then(null, onRejected)</code>
	 * 
	 * @param onRejected
	 * @return
	 */
	public Promise<T> thenCatch(Consumer<Throwable> onRejected) {
		return then((Function<T, T>) null, onRejected);
	}

	/**
	 * Something like <code>then(f, f)</code>
	 * 
	 * @param onRejected
	 * @return
	 */
	public Promise<T> thenFinally(BiConsumer<T, Throwable> onRejectedOrOnFulfilled) {
		return then((t) -> {
			onRejectedOrOnFulfilled.accept(t, null);
		}, (err) -> {
			onRejectedOrOnFulfilled.accept(null, err);
		});
	}

	/**
	 * Something like <code>then(f, f)</code>
	 * 
	 * @param onRejected
	 * @return
	 */
	public Promise<T> thenFinally(Runnable runnable) {
		return then((t) -> {
			runnable.run();
		}, (err) -> {
			runnable.run();
		});
	}

	/**
	 * - only one of onFulfilled or onRejected is called
	 * 
	 * - it is only called once
	 * 
	 * - it is never called until the next tick (i.e. after the .done method has
	 * returned)
	 * 
	 * - it is called regardless of whether the promise is resolved before or after
	 * we call .done
	 */
	public void done(Consumer<T> onFulfilled, Consumer<Throwable> onRejected) {
		Handler handler = new Handler(onFulfilled, onRejected);
		setTimeout(() -> {
			this.handle(handler);
		}, 0);
	}

	/**
	 * Returns a Promise object that is resolved with a given value
	 */
	public static <W> Promise<W> resolve(W value) {
		return new Promise<>((resolve, reject) -> {
			resolve.accept(value);
		});
	}

	/**
	 * Returns a Promise object that is rejected with a given error
	 */
	public static <W> Promise<W> reject(Throwable error) {
		return new Promise<>((resolve, reject) -> {
			reject.accept(error);
		});
	}

	private static final class ResultHolder<W> {
		public Throwable error;
		public boolean rejected;
		public W result;
	}

	/**
	 * Resolve when all given promises do resolve
	 */
	@SafeVarargs
	public static <W> Promise<List<W>> all(Promise<W>... promises) {

		return new Promise<>((resolve, reject) -> {

			// we expect for promises.length promises to finish
			final CountDownLatch latch = new CountDownLatch(promises.length);
			List<W> resultList = new ArrayList<>(promises.length);

			// we use a holder to avoid error "Local variable defined in
			// an enclosing scope must be final or effectively final"
			ResultHolder<?> resultHolder = new ResultHolder<>();
			resultHolder.rejected = false;

			for (Promise<W> promise : promises) {
				promise.then((result) -> {
					// one promise ended: add result to list

					resultList.add(result);
					latch.countDown();
				}, (error) -> {
					// one promise failed: "all" promise failed, too

					resultHolder.rejected = true;
					resultHolder.error = error;

					// Countdown latches to cancel to move forward even if there is something else
					// thread running
					for (int i = 0; i < promises.length; i++) {
						latch.countDown();
					}
				});
			}

			// wait that latch goes down to 0
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			if (resultHolder.rejected) {
				reject.accept(resultHolder.error);
			} else {
				resolve.accept(resultList);
			}
		});
	}

	/**
	 * Resolve when all given promises finish, either resolved or rejected
	 */
	@SafeVarargs
	public static <W> Promise<List<Object>> allSettled(Promise<W>... promises) {

		return new Promise<>((resolve, reject) -> {

			// we expect for promises.length promises to finish
			final CountDownLatch latch = new CountDownLatch(promises.length);
			List<Object> resultList = new ArrayList<>(promises.length);

			for (Promise<W> promise : promises) {
				promise.then((result) -> {
					resultList.add(result);
					latch.countDown();
				}, (error) -> {
					resultList.add(error);
					latch.countDown();
				});
			}

			// wait that latch goes down to 0
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			resolve.accept(resultList);
		});
	}

	/**
	 * 
	 * It returns a single promise that fulfills as soon as any of the promises in
	 * the iterable fulfills, with the value of the fulfilled promise.
	 */
	@SafeVarargs
	public static <W> Promise<W> any(Promise<W>... promises) {

		return new Promise<>((resolve, reject) -> {

			// we expect for promises.length promises to finish
			final CountDownLatch latch = new CountDownLatch(promises.length);
			List<Throwable> errorList = new ArrayList<>(promises.length);

			// we use a holder to avoid error "Local variable defined in
			// an enclosing scope must be final or effectively final"
			ResultHolder<W> resultHolder = new ResultHolder<>();
			resultHolder.rejected = true;

			for (Promise<W> promise : promises) {
				promise.then((result) -> {
					// one promise resolved
					LOGGER.info("SONO QUI" + result);
					resultHolder.result = result;
					resultHolder.rejected = false;

					// Countdown latches to cancel to move forward even if there is something else
					// thread running
					for (int i = 0; i < promises.length; i++) {
						latch.countDown();
					}
					;
				}, (error) -> {
					// one promise failed
					errorList.add(error);
					latch.countDown();
				});
			}

			// wait that latch goes down to 0
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			if (resultHolder.rejected) {
				reject.accept(new AggregateException(errorList));
			} else {
				LOGGER.info("SONO QUA " + resultHolder.rejected + " " + resultHolder.result);
				resolve.accept(resultHolder.result);
			}
		});
	}

	/**
	 * A promise that fulfills or rejects as soon as one of the promises in an
	 * iterable fulfills or rejects, with the value or reason from that promise.
	 */
	@SafeVarargs
	public static <W> Promise<Object> race(Promise<W>... promises) {

		return new Promise<>((resolve, reject) -> {

			// we expect for promises.length promises to finish
			final CountDownLatch latch = new CountDownLatch(promises.length);

			// we use a holder to avoid error "Local variable defined in
			// an enclosing scope must be final or effectively final"
			ResultHolder<W> resultHolder = new ResultHolder<>();
			resultHolder.rejected = false;

			for (Promise<W> promise : promises) {
				promise.then((result) -> {
					// one promise ended

					resultHolder.rejected = false;
					resultHolder.result = result;

					// Countdown latches to cancel to move forward even if there is something else
					// thread running
					for (int i = 0; i < promises.length; i++) {
						latch.countDown();
					}
					;
				}, (error) -> {
					// one promise failed

					resultHolder.rejected = true;
					resultHolder.error = error;

					// Countdown latches to cancel to move forward even if there is something else
					// thread running
					for (int i = 0; i < promises.length; i++) {
						latch.countDown();
					}
				});
			}

			// wait that latch goes down to 0
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			if (resultHolder.rejected) {
				reject.accept(resultHolder.error);
			} else {
				resolve.accept(resultHolder.result);
			}
		});
	}
}
