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
