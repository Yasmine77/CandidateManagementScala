package models.tableDefinitions

import models.Profile
import models.ProfilesId.ProfileId
import slick.lifted.{TableQuery, Tag}
import slick.jdbc.MySQLProfile.api._


class ProfileTableDef(tag: Tag) extends BaseTable[Profile](tag, "profiles") with TableId[ProfileId]{

  override def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
  def profileType = column[String]("profileType")
  def userRef = column[Long]("userRef")

  override def * = (id, profileType, userRef) <> ((Profile.apply _).tupled, Profile.unapply)

  def  userFk =  foreignKey("profiles_ibfk_1", userRef,  TableQuery[UserTableDef]) (_.id)  // , ForeignKeyAction.NoAction,ForeignKeyAction.Cascade

}