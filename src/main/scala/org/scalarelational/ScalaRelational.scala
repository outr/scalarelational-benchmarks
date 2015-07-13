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
      // drop(suppliers)  TODO not implemented?
    }
  }

  def insertBatch() {
    import Datastore._
    import suppliers._

    session {
      // TODO Improve DSL (see Breeze) or adopt Slick's += notation
      (2 to 500).foldLeft(
        insert(name(s"Name 0"), street("Street 0"))
          .and(name(s"Name 1"), street(s"Street 1"))
      ) { case (acc, cur) =>
        acc.and(name(s"Name $cur"), street(s"Street $cur"))
      }.result
    }
  }

  def insertSeparate() {
    import Datastore._
    import suppliers._

    session {
      (0 to 500).foreach { cur =>
        insert(name(s"Name $cur"), street(s"Street $cur")).result
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
        query.to[Supplier]
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
  object suppliers extends Table("SUPPLIERS") {
    val id = column[Option[Int]]("SUP_ID", PrimaryKey, AutoIncrement)
    val name = column[String]("SUP_NAME")
    val street = column[String]("STREET")
  }
}
