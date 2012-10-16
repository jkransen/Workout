package models

import play.api.data.Form
import play.api.data.Forms._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.dsl.{ OneToMany, ManyToOne }

case class Machine(id: Long, name: String, location: Option[String], gymid: Long) extends KeyedEntity[Long] {

  lazy val gym: ManyToOne[Gym] = Database.machinesToGym.right(this)
}

object Machine {

  def apply(name: String, location: String) {
    new Machine(0, name, Some(location), 0)
  }

  def findByGym(gymId: Long): Seq[Machine] = {
    inTransaction {
      byGymQ(gymId).toList
    }
  }

  def findById(id: Long): Machine = {
    inTransaction {
      byIdQ(id).headOption.get
    }
  }

  def save(machine: Machine) {
    inTransaction {
      Database.machinesTable.insert(machine)
    }
  }

  def allQ: Query[Machine] = {
    from(Database.machinesTable) {
      machine => select(machine)
    }
  }

  def byIdQ(id: Long): Query[Machine] = {
    from(allQ) {
      machine => where(machine.id === id).select(machine)
    }
  }

  def byGymQ(gymId: Long): Query[Machine] = {
    from(allQ) {
      machine => where(machine.gymid === gymId).select(machine)
    }
  }

  def form(gymId: Long) = Form(
    mapping(
      "name" -> text,
      "location" -> optional(text))((name, location) => new Machine(0, name, location, gymId))(machine => Some(machine.name, machine.location)))
}
