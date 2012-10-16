package models

import play.api.data.Form
import play.api.data.Forms._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.dsl.{ OneToMany, ManyToOne }

case class Exercise(id: Long, val variation: Option[String], val musclezone: Option[String], val machineid: Long) extends KeyedEntity[Long] {

  lazy val machine: ManyToOne[Machine] = Database.exercisesToMachine.right(this)
}

object Exercise {

  def apply(variation: String, zone: String) {
    new Exercise(0, Some(variation), Some(zone), 0)
  }

  def findByMachine(machineId: Long): Seq[Exercise] = {
    inTransaction {
      byMachineQ(machineId).toList
    }
  }

  def findById(id: Long): Exercise = {
    inTransaction {
      byIdQ(id).headOption.get
    }
  }

  def save(exercise: Exercise) {
    inTransaction {
      Database.exercisesTable.insert(exercise)
    }
  }

  def allQ: Query[Exercise] = {
    from(Database.exercisesTable) {
      exercise => select(exercise)
    }
  }

  def byIdQ(id: Long): Query[Exercise] = {
    from(allQ) {
      exercise => where(exercise.id === id).select(exercise)
    }
  }

  def byMachineQ(machineId: Long): Query[Exercise] = {
    from(allQ) {
      exercise => where(exercise.machineid === machineId).select(exercise)
    }
  }

  def form(machineId: Long) = Form(
    mapping(
      "variation" -> optional(text),
      "musclezone" -> optional(text))((variation, musclezone) => new Exercise(0, variation, musclezone, machineId))(exercise => Some(exercise.variation, exercise.musclezone)))

}