package models.tableDefinitions
import models.Application
import models.ApplicationsId.ApplicationId
import models.enums.AppStatus
import models.enums.AppStatus.AppStatus
import slick.jdbc.MySQLProfile.MappedColumnType
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class ApplicationTableDef(tag: Tag) extends BaseTable[Application](tag,"applications") with TableId[ApplicationId]{

  override def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
  def offerRef = column[Long]("offerRef")
  def userRef = column[Long]("userRef")
  def applyDate = column[String]("applyDate")

  implicit val AppStatusMapper: BaseColumnType[AppStatus]  = MappedColumnType.base[AppStatus, String](
    e => e.toString, s => AppStatus.withName(s)
  )
  def appStatus = column[AppStatus]("appStatus")

  def offerFK = foreignKey("App_FK_1", offerRef, TableQuery[OfferTableDef])(_.id)
  def userFK = foreignKey("App_FK_2", userRef, TableQuery[UserTableDef])(_.id)

  override def * = (id,offerRef,userRef,applyDate,appStatus) <> ((Application.apply _).tupled,Application.unapply)

}
