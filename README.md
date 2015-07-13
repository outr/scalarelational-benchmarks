# ScalaRelational benchmarks
This benchmark suite compares the performance of [ScalaRelational](https://github.com/outr/scalarelational/)
to Slick.

The suite is based upon [jmh](http://openjdk.java.net/projects/code-tools/jmh/).

## Running
Run the benchmarks with ``sbt run``.

This may take up to several hours. To speed up the process, you can configure
the number of iterations. For example, ``run -i 2 -wi 2 -f 0`` will run
only two iterations, two warmup iterations and disable forking.

## License
This project is licensed under the terms of the Apache v2.0 license.

## Authors
* Tim Nieradzik
