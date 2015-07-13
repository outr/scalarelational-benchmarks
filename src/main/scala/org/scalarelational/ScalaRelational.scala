package org.scalarelational

import org.scalarelational.column.property.{AutoIncrement, PrimaryKey}
import org.scalarelational.h2.{H2Datastore, H2Memory}
import org.scalarelational.model.Table
import org.scalarelational.mapper._

object ScalaRelational {
  def setUp() {
    import Datastore._

    session {
      create(suppliers)
    }
  }

  def tearDown() {
    import Datastore._

    session {
      // TODO Use DDL DSL
      session.execute("DROP TABLE `suppliers`")
    }
  }

  def insertBatch() {
    import Datastore._
    import suppliers._

    session {
      Datastore.delete(suppliers).result

      // TODO Improve DSL (see Breeze) or adopt Slick's += notation
      (2 to 500).foldLeft(
        insert(id(Some(0)), name(s"Name 0"), street("Street 0"))
          .and(id(Some(1)), name(s"Name 1"), street(s"Street 1"))
      ) { case (acc, cur) =>
        acc.and(id(Some(cur)), name(s"Name $cur"), street(s"Street $cur"))
      }.result
    }
  }

  def insertSeparate() {
    import Datastore._
    import suppliers._

    session {
      Datastore.delete(suppliers).result

      (0 to 500).foreach { cur =>
        insert(id(Some(cur)), name(s"Name $cur"), street(s"Street $cur")).result
      }
    }
  }

  def insertMapper() {
    import Datastore._

    session {
      (0 to 500).foreach { cur =>
        suppliers.persist(Supplier(s"Name $cur", s"Street $cur")).result
      }
    }
  }

  def queryConvert(): Int = {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).map { i =>
        val query = suppliers.q from suppliers where id === Some(i)
        query.convert[Supplier] { qr =>
          Supplier(qr(name), qr(street), qr(id))
        }.result.one()
      }.size
    }
  }

  def queryMap(): Int = {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).map { i =>
        // val query = suppliers.q from suppliers where id === Some(i)  TODO should work too
        val query = select(name, street, id) from suppliers where id === Some(i)
        query.map(Supplier.tupled).result.one()
      }.size
    }
  }

  def queryTo(): Int = {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).map { i =>
        val query = suppliers.q from suppliers where id === Some(i)
        query.to[Supplier].result.one()
      }.size
    }
  }

  def update(): Int = {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).map { i =>
        (Datastore.update(name(s"Updated $i")) where id === Some(i)).result
      }.size
    }
  }

  def delete(): Int = {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).map { i =>
        (Datastore.delete(suppliers) where id === Some(i)).result
      }.size
    }
  }
}

case class Supplier(name: String, street: String, id: Option[Int] = None)

object Datastore extends H2Datastore(mode = H2Memory("scalarelational")) {
  object suppliers extends Table("suppliers") {
    val id = column[Option[Int]]("id", PrimaryKey, AutoIncrement)
    val name = column[String]("name")
    val street = column[String]("street")
  }
}
