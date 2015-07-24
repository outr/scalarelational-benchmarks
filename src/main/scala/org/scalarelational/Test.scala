package org.scalarelational

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.All))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Test {
  @Setup(Level.Iteration)
  def setUp() {
    // Create table
    Slick.setUp()
    ScalaRelational.setUp()

    // Some benchmarks (query, update, delete) require a non-empty table, do it
    // outside of the benchmark as it may skew the results otherwise
    Slick.batchInsert()
    ScalaRelational.batchInsert()
  }

  @TearDown(Level.Iteration)
  def tearDown() {
    // Drop table
    ScalaRelational.tearDown()
    Slick.tearDown()
  }

  @Benchmark
  def slickInsertBatch() { Slick.batchInsert() }

  @Benchmark
  def slickInsertSeparate() { Slick.insertSeparate() }

  @Benchmark
  def slickQuery() { Slick.query() }

  @Benchmark
  def slickUpdate() { Slick.update() }

  @Benchmark
  def slickDelete() { Slick.delete() }

  @Benchmark
  def srInsertBatch() { ScalaRelational.batchInsert() }

  @Benchmark
  def srInsertSeparate() { ScalaRelational.insertSeparate() }

  @Benchmark
  def srInsertMapper() { ScalaRelational.insertMapper() }

  @Benchmark
  def srInsertMapperMacros() { ScalaRelational.insertMapperMacros() }

  @Benchmark
  def srQueryConvert() { ScalaRelational.queryConvert() }

  @Benchmark
  def srQueryMap() { ScalaRelational.queryMap() }

  @Benchmark
  def srQueryTo() { ScalaRelational.queryTo() }

  @Benchmark
  def srUpdate() { ScalaRelational.update() }

  @Benchmark
  def srDelete() { ScalaRelational.delete() }
}
