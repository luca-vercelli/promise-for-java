# promise-for-java
JavaScript-like Promise for Java

This project is inspired by https://github.com/riversun/java-promise and https://www.promisejs.org/implementing/.

Differently from `java-promise`, we

1. use Java generics
2. use Java functional interfaces
3. always run `Promise`'s in a new tread (please note that Javascript is single-threaded, while Java is not, it's difficult for use to use a single thread)
4. avoid any kind of `start()` method to trigger promise


We also add some (naive) implementation of the `fetch` Javascript API for accessing web resources.

JavaScript API are defined here:
[Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise),
[Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).


## Usage

Creating promises:

    Promise<Integer> p1 = Promise.resolve(42);
    Promise<Integer> p2 = Promise.resolve((resolve,reject) -> {
        SetTimeout.setTimeout(() -> { resolve(43); }, 1000);
    });

Chaining promises:

    p1.then((x) -> (x*2)).then((x) -> (x+1))

## Main differences from JavaScript Promise API

* `resolve` and `reject` functional interfaces must be called as `resolve.accept(x)` and `reject.accept(x)`. This is the equivalent of JavaScript's `resolve(x)` and `reject(x)`.
* `catch` and `finally` are reserved words and cannot be used as method names. We decided to call these methods `thenCatch()` and `thenFinally()`.
* It's not possible to declare two different methods `<W> Promise<W> then(Function<T, W> onFulfilled)` and `<W> Promise<W> then(Function<T, Promise<W>> onFulfilled)` because they have same erasure. We decided to call the latter method `thenPromise()`.
* In order to respects generics, we keep separated promise value and promise error.
