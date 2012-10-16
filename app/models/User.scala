package models
 
import play.api.db._
import play.api.Play.current
 
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._

case class User(id: Long, email: String, fullname: String, facebookusername: String = null)  extends KeyedEntity[Long]

object User {
  
  def allQ: Query[User] = from(Database.usersTable) {
    user => select(user)
  }

  def findByEmailQ(email: String): Query[User] = from(allQ) {
    user => where(user.email === email).select(user)
  }
  
  // returns existing user by email address, or null if not exists
  def apply(email: String): User = {
    inTransaction {
      findByEmailQ(email).headOption.getOrElse(null)
    }
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