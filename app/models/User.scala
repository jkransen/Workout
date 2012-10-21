package models

import play.api.db._
import play.api.Play.current

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._

case class User(id: Long, email: String, fullname: String, facebookusername: String = null, facebookid: Option[Long] = None) extends KeyedEntity[Long]

object User {

  // returns existing user by email address, or null if not exists
  def apply(email: String): User = {
    inTransaction {
      findByEmailQ(email).headOption.getOrElse(null)
    }
  }

  def allQ: Query[User] = from(Database.usersTable) {
    user => select(user)
  }

  def findByEmailQ(email: String): Query[User] = from(allQ) {
    user => where(user.email === email).select(user)
  }

  def findByFacebookIdQ(facebookId: Long): Query[User] = from(allQ) {
    user => where(user.facebookid === facebookId).select(user)
  }

  def findByFacebookId(facebookId: Long): Option[User] = {
    inTransaction {
      findByFacebookIdQ(facebookId).headOption
    }
  }

  def existsByFacebookId(facebookId: Long): Boolean = {
    findByFacebookId(facebookId).isDefined
  }

  def findAll(): List[User] = {
    inTransaction {
      allQ.toList
    }
  }

  def create(user: User) {
    inTransaction {
      Database.usersTable.insert(user)
    }
  }
}