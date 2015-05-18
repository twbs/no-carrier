NO CARRIER
======
[![Build Status](https://travis-ci.org/twbs/no-carrier.svg?branch=master)](https://travis-ci.org/twbs/no-carrier)
[![MIT License](https://img.shields.io/github/license/twbs/no-carrier.svg)](https://github.com/twbs/no-carrier/blob/master/LICENSE.txt)

NO CARRIER is a tool to auto-close old GitHub issues that were abandoned by their reporter, which is to say that we were waiting for a response from the reporter but never got a response in a reasonable amount of time.

Named after [the message indicating that your dial-up modem has lost its connection.](http://en.wikipedia.org/wiki/NO_CARRIER#As_Internet_slang)

## Motivation
You're a member of a popular open source project. Cool.

But due to the project's popularity, you get lots of bug reports on the issue tracker, and often these reports are lacking key information that's explicitly mentioned in your bug reporting docs as being required. Or maybe you just need extra information or test results from the bug reporter. So, okay, you post nice reply comments on the issues, asking the reporters for the information or requesting that they do some investigation/tests.

And then you wait for a reply. Unfortunately, for whatever reason, a surprisingly large fraction of folks never end up replying to your questions/requests. So unactionable issues start to pile up. You occasionally review the oldest batch of such issues and close them manually.

By automating the process of closing abandoned issues, the issue tracker is kept clean with less tedium on the part of issue triagers. The abandoned issue expiration policy is also applied more uniformly/fairly, and it's easier to use a canonical template for the closing issue comment that clearly and politely explains the reason for the closure.

## Used by
* (Hopefully [Bootstrap](https://github.com/twbs/bootstrap) real soon)

## Usage
Java 8+ is required to run NO CARRIER. For instructions on building NO CARRIER yourself, see [the Contributing docs](https://github.com/twbs/no-carrier/blob/master/CONTRIBUTING.md).

NO CARRIER accepts exactly 5 command line arguments. Once you've built the JAR, run e.g. `java -jar no-carrier-assembly-1.0.jar <username> <password> <owner/repo> <label> <days>`. Here's what each of the arguments is:
* `username`: Username of GitHub user to login as
* `password`: Password of GitHub user to login as
* `owner/repo`: GitHub repo whose issues will be operated upon (example: `twbs/bootstrap` for [Bootstrap](https://github.com/twbs/bootstrap))
* `label`: Name of label used on the repo's GitHub issue tracker to indicate that the issue is blocked waiting for a reply from a user (typically the issue's original poster).
* `days`: Integer number of days. If at least this number of days elapses after an issue has been labeled without any new comment being posted, NO CARRIER will close the issue and post an explanatory comment.

## License
NO CARRIER is licensed under the [MIT License](https://github.com/twbs/no-carrier/blob/master/LICENSE.txt).

## Acknowledgments
We all stand on the shoulders of giants and get by with a little help from our friends. NO CARRIER is written in [Scala](http://www.scala-lang.org) and built on top of:
* [jcabi-github](https://github.com/jcabi/jcabi-github), for working with [the GitHub API](https://developer.github.com/v3/)
* [Logback](http://logback.qos.ch/) and [Scala Logging](https://github.com/typesafehub/scala-logging), for logging (duh)
* [specs2](http://etorreborre.github.io/specs2/) for unit tests

## See also
* [LMVTFY](https://github.com/cvrebert/lmvtfy), NO CARRIER's sister bot who does HTML validation
* [Savage](https://github.com/twbs/savage), NO CARRIER's sister bot who runs cross-browser JS tests on Sauce Labs
* [Rorschach](https://github.com/twbs/rorschach), NO CARRIER's sister bot who sanity-checks Bootstrap pull requests
