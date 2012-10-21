package models

import java.util.Date

import play.api.data.Form
import play.api.data.Forms._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.dsl.{ OneToMany, ManyToOne }

case class Performance(id: Long, val tstamp: Date, val weight: Int, val notes: Option[String], val userid: Long, val exerciseid: Long) extends KeyedEntity[Long] {

    lazy val user: ManyToOne[User] = Database.performancesToUser.right(this)
    lazy val exercise: ManyToOne[Exercise] = Database.performancesToExercise.right(this)
}

object Performance {
  
  /*
   * This query returns (if any) the performances in descending timestamp order, so latest first
   */
  def findByExercise(exerciseId: Long): Seq[Performance] = {
    inTransaction {
      byExerciseQ(exerciseId).toList
    }
  }
  
  def save(performance: Performance) {
    inTransaction {
      Database.performancesTable.insert(performance)
    }
  }

  def allQ: Query[Performance] = {
    from(Database.performancesTable) {
      performance => select(performance)
    }
  }

  def byIdQ(id: Long): Query[Performance] = {
    from(allQ) {
      performance => where(performance.id === id).select(performance)
    }
  }

  def byExerciseQ(exerciseId: Long): Query[Performance] = {
    from(allQ) {
      performance => where(performance.exerciseid === exerciseId).select(performance).orderBy(performance.tstamp desc)
    }
  }

  def form(userId: Long, exerciseId: Long) = Form(
    mapping(
      "weight" -> number,
      "notes" -> optional(text))((weight, notes) => new Performance(0, new Date(System.currentTimeMillis), weight, notes, userId, exerciseId))(performance => Some(performance.weight, performance.notes)))
}