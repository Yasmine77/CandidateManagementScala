
package repositories

import models.enums.Roles.Roles
import javax.inject.Inject
import scala.concurrent.Future
import models.User
import models.UsersId.UserId
import models.tableDefinitions.{UserTableDef}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext


 class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext:ExecutionContext) extends  BaseRepository[User, UserTableDef,UserId](dbConfigProvider, executionContext, TableQuery[UserTableDef]) {


  implicit val users = TableQuery[UserTableDef]

  def searchByUsername  (name : String) : DBIO[Option[User]] = users.filter(e=> e.username === name).result.headOption
  def searchByEmail  (email : String) : DBIO[Option[User]] = users.filter(e=> e.email === email).result.headOption

  def getById  (id: UserId  ) : DBIO[Option[User]] = users.filter(e=> e.id === id).result.headOption

  def updateUser(user : User) = update(user.id,user)

  def ChangePassword(id: UserId , password: String) = {
   val query = for (user <- users.filter(_.id === id))
    yield (user.password)
   db.run(query.update(password)) map { _ > 0 }
  }
  def updateRole(user : User): Future[Int] = {
   db.run(users.filter(_.id === user.id)
     .map(x => (x.role))
     .update(user.role)
   )
  }
  def ChangeRole(id: UserId , role: Roles) = {
   val query = for (user <- users.filter(_.id === id))
    yield (user.role)
   db.run(query.update(role)) map { _ > 0 }
  }
  def userUpdate(id: UserId , firstName: String,lastName:String,phoneNumber:String,username:String,email:String) = {
   val query = for (user <- users.filter(_.id === id))
    yield (user.firstName,user.lastName,user.phoneNumber,user.username,user.email)
   db.run(query.update(firstName,lastName,phoneNumber,username,email)) map { _ > 0 }
  }
  def searchByRole  (role : Roles   ) : DBIO[Option[User]] = users.filter(e=> e.role === role).result.headOption





}
