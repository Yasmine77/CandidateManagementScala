package services

import com.google.inject.Inject
import models.User
import models.UsersId.UserId
import models.enums.Roles.Roles
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.UserRepository
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
class UserService @Inject()(val dbConfigProvider:DatabaseConfigProvider,executionContext:ExecutionContext ,users:UserRepository) extends HasDatabaseConfigProvider[JdbcProfile]  {

def delete(id: UserId)  = db.run(users.deleteById(id))

def createUser(user : User) = db.run(users.save(user)(executionContext))

def getAllUsers = db.run(users.all())

def getUserById(id: UserId) = db.run(users.find(id))
  def searchByRole(role: Roles ) =  db.run(users.searchByRole(role ))

  def authenticateUser (username : String   ) =  db.run(users.searchByUsername(username ))

def checkEmail (email : String   ) =  db.run(users.searchByEmail(email ))

def updateUser(user:User) = db.run(users.updateUser(user))

def ChangePassword(id: UserId , password: String) = users.ChangePassword(id, password)

def ChangeRole(id: UserId , role: Roles) = users.ChangeRole(id, role)

def updateRole(user: User) = users.updateRole(user)

  def userUpdate(id: UserId , firstName: String,lastName:String,phoneNumber:String,username:String,email:String) = users.userUpdate(id,firstName,lastName,phoneNumber,username,email)


}