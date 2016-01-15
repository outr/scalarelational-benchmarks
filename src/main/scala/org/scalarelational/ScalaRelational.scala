package org.scalarelational

import org.scalarelational.column.property.{AutoIncrement, PrimaryKey}
import org.scalarelational.h2.{H2Datastore, H2Memory}
import org.scalarelational.instruction.Query
import org.scalarelational.mapper._

object ScalaRelational {
  import Datastore._
  import suppliers._

  def setUp() = session {
    create(suppliers)
  }

  def tearDown() = session {
    dropTable(suppliers, cascade = true).result
  }

  def batchInsert() = session {
    Datastore.delete(suppliers).result

    val batch = (0 to 500).map { i =>
      List(id(Some(i)), name(s"Name $i"), street(s"Street $i"))
    }
    insertBatch(batch).result
  }

  def insertSeparate() = session {
    Datastore.delete(suppliers).result

    (0 to 500).foreach { cur =>
      insert(id(Some(cur)), name(s"Name $cur"), street(s"Street $cur")).result
    }
  }

  def insertMapper() = session {
    (0 to 500).foreach { cur =>
      Supplier(s"Name $cur", s"Street $cur").insert.result
    }
  }

  def queryConvert(): Int = session {
    (0 to 500).map { i =>
      val query = suppliers.q from suppliers where id === Some(i)
      query.convert[Supplier] { qr =>
        Supplier(qr(name), qr(street), qr(id))
      }.result.one()
    }.size
  }

  def queryMap(): Int = session {
    (0 to 500).map { i =>
      // val query = suppliers.q from suppliers where id === Some(i)  TODO should work too
      val query = select(name, street, id) from suppliers where id === Some(i)
      query.map(Supplier.tupled).result.one()
    }.size
  }

  def queryTo(): Int = session {
    (0 to 500).map { i =>
      val query = suppliers.q from suppliers where id === Some(i)
      query.to[Supplier].result.one()
    }.size
  }

  def update(): Int = session {
    (0 to 500).map { i =>
      (Datastore.update(name(s"Updated $i")) where id === Some(i)).result
    }.size
  }

  def delete(): Int = session {
    (0 to 500).map { i =>
      (Datastore.delete(suppliers) where id === Some(i)).result
    }.size
  }
}

case class Supplier(name: String, street: String, id: Option[Int] = None) extends Entity[Supplier] {
  def columns = mapTo[Supplier](Datastore.suppliers)
}

object Datastore extends H2Datastore(mode = H2Memory("scalarelational")) {
  object suppliers extends MappedTable[Supplier]("suppliers") {
    val id = column[Option[Int], Int]("id", PrimaryKey, AutoIncrement)
    val name = column[String]("name")
    val street = column[String]("street")

    override def query = q.to[Supplier]
  }
}