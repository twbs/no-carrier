Hacking on NO CARRIER
=================
## How do I build NO CARRIER?
1. [Install sbt](http://www.scala-sbt.org/download.html)
2. Go to your `no-carrier` directory.
3. Run `sbt compile`

## How do I run NO CARRIER's unit test suite?
0. Ensure that sbt is installed (see above).
1. Go to your `no-carrier` directory.
2. Run `sbt test`

## How do I generate a single self-sufficient JAR that includes all of the necessary dependencies?
0. Ensure that sbt is installed (see above).
1. Go to your `no-carrier` directory.
2. Run `sbt assembly`
3. If the build is successful, the desired JAR will be generated as `target/scala-2.11/no-carrier-assembly-1.0.jar`.

## Licensing
NO CARRIER is licensed under The MIT License. By contributing to NO CARRIER, you agree to license your contribution under [The MIT License](https://github.com/cvrebert/no-carrier/blob/master/LICENSE.txt).
