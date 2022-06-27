package models.enums

import models.enums.Roles.{Roles, candidate}
import slick.jdbc.MySQLProfile.MappedColumnType
import slick.jdbc.MySQLProfile.api._

object userPermission extends Enumeration {
  type userPermission = Value

  val AddUser, ReadUser, ReadRoles, AffectRole, DeleteAffectedRole, CreateOffer, EditOffer, MakeDesscion = Value // if you want to use a,b,c instead, feel free to do it
  implicit val userPermissionMapper: BaseColumnType[userPermission] = MappedColumnType.base[userPermission, String](
    e => e.toString,
    s => userPermission.withName(s)
  )

  def roles = Roles match {
    case candidate => ()
    case admin => userPermission.values
    case manager => (userPermission.CreateOffer, EditOffer, MakeDesscion)
    case supervisor => (userPermission.CreateOffer, EditOffer)

  }

  def affectPermissionToRoles(R: Roles, P: userPermission) = {
    R match {
      case candidate => ()
      case admin => userPermission.values
      case manager => (userPermission.CreateOffer, EditOffer, MakeDesscion)
      case supervisor => (userPermission.CreateOffer, EditOffer)

    }


  }
}
