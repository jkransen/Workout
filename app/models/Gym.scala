package models

import play.api.data.Form
import play.api.data.Forms._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.KeyedEntity
import org.squeryl.dsl.{ OneToMany, ManyToOne }

case class Gym(id: Long, name: Option[String], adminid: Long) extends KeyedEntity[Long] {

  lazy val admin: ManyToOne[User] = Database.gymsToAdmin.right(this)

  def getAdmin: User = {
    inTransaction {
      this.admin.head
    }
  }
}

object Gym {

  def apply(name: String, admin: User): Gym = {
    val gym = Gym(0, Some(name), admin.id)
    gym.admin.assign(admin)
    gym
  }

  def form(admin: User): Form[Gym] = {
    Form[Gym](
      mapping(
        "name" -> text)((name) => Gym(name, admin))(gym => gym.name))
  }

  //  val form = Form(
  //    mapping(
  //      "name" -> optional(text))((name) => new Gym(0, name, 0))(gym => Some(gym.name)))

  def findAll(): List[Gym] = {
    inTransaction {
      allQ.toList
    }
  }

  def findById(id: Long): Gym = {
    inTransaction {
      byIdQ(id).headOption.get
    }
  }

  def findByUser(user: User): List[Gym] = {
    inTransaction {
      byUserQ(user).toList
    }
  }

  def save(gym: Gym) {
    inTransaction {
      Database.gymsTable.insert(gym)
    }
  }

  // queries
  def allQ: Query[Gym] = {
    from(Database.gymsTable) {
      gym => select(gym)
    }
  }

  def byIdQ(id: Long): Query[Gym] = {
    from(allQ) {
      gym => where(gym.id === id).select(gym)
    }
  }

  def byUserQ(user: User): Query[Gym] = {
    from(allQ) {
      gym => where(gym.adminid === user.id).select(gym)
    }
  }
}