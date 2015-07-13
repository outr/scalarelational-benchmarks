package org.scalarelational

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import slick.driver.H2Driver.api._

object Slick {
  val _db = Database.forConfig("h2mem1")

  // TODO db.close() destroys database?
  def session[T](f: Database => T): T = {
    // val db = Database.forConfig("h2mem1")
    // try f(db) finally db.close()
    f(_db)
  }

  class Suppliers(tag: Tag) extends Table[(Int, String, String)](tag, "suppliers") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def street = column[String]("street")
    def * = (id, name, street)
  }

  val suppliers = TableQuery[Suppliers]

  def setUp() {
    session { db =>
      Await.result(db.run(suppliers.schema.create), Duration.Inf)
    }
  }

  def tearDown() {
    session { db =>
      Await.result(db.run(suppliers.schema.drop), Duration.Inf)
    }
  }

  def insertBatch() {
    val deleteQuery = suppliers.delete

    val insertQueries = (0 to 500).map { id =>
      suppliers += (id, s"Name $id", s"Street $id")
    }

    val queries = deleteQuery +: insertQueries

    val batchQuery = DBIO.seq(queries: _*)

    session { db =>
      Await.result(db.run(batchQuery), Duration.Inf)
    }
  }

  def insertSeparate() {
    session { db =>
      (0 to 500).foreach { id =>
        val query = suppliers += (-1, s"Name $id", s"Street $id")
        Await.result(db.run(query), Duration.Inf)
      }
    }
  }

  def query(): Int = {
    session { db =>
      (0 to 500).map { i =>
        val future = db.run(suppliers.filter(_.id === i).result)
        Await.result(future, Duration.Inf)
      }.size
    }
  }

  // TODO compare lifted queries

  def update(): Int = {
    session { db =>
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
  }

  def delete(): Int = {
    session { db =>
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
}
