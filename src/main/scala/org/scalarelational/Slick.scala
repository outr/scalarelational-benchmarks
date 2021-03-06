package org.scalarelational

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import slick.driver.H2Driver.api._

object Slick {
  val db = Database.forConfig("h2mem1")

  class Suppliers(tag: Tag) extends Table[(Int, String, String)](tag, "suppliers") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def street = column[String]("street")
    def * = (id, name, street)
  }

  val suppliers = TableQuery[Suppliers]

  def setUp() {
    Await.result(db.run(suppliers.schema.create), Duration.Inf)
  }

  def tearDown() {
    Await.result(db.run(suppliers.schema.drop), Duration.Inf)
  }

  def batchInsert() {
    val deleteQuery = suppliers.delete

    val insertQueries = (0 to 500).map { id =>
      suppliers += (id, s"Name $id", s"Street $id")
    }

    val queries = deleteQuery +: insertQueries
    val batchQuery = DBIO.seq(queries: _*)

    Await.result(db.run(batchQuery), Duration.Inf)
  }

  def insertSeparate() {
    val deleteQuery = suppliers.delete
    Await.result(db.run(deleteQuery), Duration.Inf)

    (0 to 500).foreach { id =>
      val query = suppliers += (id, s"Name $id", s"Street $id")
      Await.result(db.run(query), Duration.Inf)
    }
  }

  def query(): Int = {
    (0 to 500).map { i =>
      val future = db.run(suppliers.filter(_.id === i).result)
      Await.result(future, Duration.Inf)
    }.size
  }

  // TODO compare lifted queries

  def update(): Int = {
    (0 to 500).map { i =>
      val future = db.run(
        suppliers
          .filter(_.id === i)
          .map(_.name)
          .update(s"Updated $i")
      )

      Await.result(future, Duration.Inf)
    }.size
  }

  def delete(): Int = {
    (0 to 500).map { i =>
      val future = db.run(
        suppliers
          .filter(_.id === i)
          .delete
      )

      Await.result(future, Duration.Inf)
    }.size
  }
}
