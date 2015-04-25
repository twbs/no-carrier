NO CARRIER
======
[![Build Status](https://travis-ci.org/cvrebert/no-carrier.svg?branch=master)](https://travis-ci.org/cvrebert/no-carrier)

NO CARRIER is a tool to auto-close old GitHub issues that were waiting for a response from the reporter but never got a response in a reasonable amount of time.

Named after [the message indicating that your dial-up modem has lost its connection.](http://en.wikipedia.org/wiki/NO_CARRIER#As_Internet_slang)

## Motivation


## Used by
* (Hopefully Bootstrap real soon)

## Usage
Java 8+ is required to run NO CARRIER. For instructions on building NO CARRIER yourself, see [the Contributing docs](https://github.com/cvrebert/no-carrier/blob/master/CONTRIBUTING.md).

NO CARRIER accepts command line arguments (XXX). Once you've built the JAR, run e.g. `java -jar no-carrier-assembly-1.0.jar XXX`.

## Acknowledgments
We all stand on the shoulders of giants and get by with a little help from our friends. NO CARRIER is written in [Scala](http://www.scala-lang.org) and built on top of:
* [jcabi-github](https://github.com/jcabi/jcabi-github), for working with [the GitHub API](https://developer.github.com/v3/)
* [Logback](http://logback.qos.ch/) and [Scala Logging](https://github.com/typesafehub/scala-logging), for logging (duh)
* [specs2](http://etorreborre.github.io/specs2/) for unit tests

## See also
* [LMVTFY](https://github.com/cvrebert/lmvtfy), NO CARRIER's sister bot who does HTML validation
* [Savage](https://github.com/cvrebert/savage), NO CARRIER's sister bot who runs cross-browser JS tests on Sauce Labs
* [Rorschach](https://github.com/twbs/rorschach), NO CARRIER's sister bot who sanity-checks Bootstrap pull requests
