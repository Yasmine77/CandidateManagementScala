package models

import models.enums.{Roles}
import models.enums.Roles.Roles
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.MappedColumnType
case class User (id:UsersId.UserId ,firstName:String,lastName:String,phoneNumber:String, username:String, email:String,role:Roles, var password : String  ) extends  BaseEntity[UsersId.UserId] //role:Role,role:Option[Roles]

object User {
  implicit val userFormat = Json.format[User]

    implicit val roleMapper = MappedColumnType.base[Roles, String](
      e => e.toString,
      s => Roles.withName(s)
    )

}

  object UsersId {
    type UserId = Long
  }


