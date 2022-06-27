package models.tableDefinitions
import models.{User, UsersId}
import models.UsersId.UserId
import models.enums.{Roles}
import models.enums.Roles.Roles
import slick.jdbc.MySQLProfile.MappedColumnType
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
class UserTableDef(tag: Tag) extends BaseTable[User](tag, "users")  with TableId[UserId] {


  override  def id = column[Long]("id", O.PrimaryKey, O.Unique, O.AutoInc)
  def firstName= column[String]("firstName")
  def lastName= column[String]("lastName")
  def phoneNumber=column[String]("phoneNumber")
  def username = column[String]("username",O.Unique)
  def email=column[String]("email",O.Unique)
  implicit val roleMapper: BaseColumnType[Roles] = MappedColumnType.base[Roles, String](
    e => e.toString,
    s => Roles.withName(s)
  )
  def role =column[Roles]("role",O.Default(Roles.candidate))
  def password = column[String]("password")



  override def * =
    (id,firstName,lastName,phoneNumber,username,email,role,password) <> ((User.apply _).tupled, User.unapply)
//role.?

}
