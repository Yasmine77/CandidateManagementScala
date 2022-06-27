package models.tableDefinitions

import models.OfferId.OfferId
import models.{Offer, User}
import models.enums.OfferType.OfferType
import models.enums.{OfferType, Roles}
import slick.jdbc.MySQLProfile.MappedColumnType
import slick.lifted.{TableQuery, Tag}
import slick.jdbc.MySQLProfile.api._

class OfferTableDef(tag: Tag) extends BaseTable[Offer](tag, "offers")  with TableId[OfferId] {

  override  def id = column[Long]("id", O.PrimaryKey, O.Unique, O.AutoInc)
  def title= column[String]("title")
  def description= column[String]("description")
  def keyRequirement= column[String]("keyRequirement")
  def mission= column[String]("mission")

  implicit val OfferMapper: BaseColumnType[OfferType] = MappedColumnType.base[OfferType, String](
    e => e.toString,
    s => OfferType.withName(s)
  )
  def offerRef = column[String]("offerRef")
  def Offer_Type=column[OfferType]("Offer_Type")
  def domain = column[String]("domain")
def publishDate =column[String]("publishDate")
  def expiredDate =column[String]("expiredDate")
  def userRef = column[Long]("UserRef")
  def userFk =  foreignKey("OFFER_FK", userRef,  TableQuery[UserTableDef]) (_.id)  // , ForeignKeyAction.NoAction,ForeignKeyAction.Cascade
  def closed =column[Boolean]("closed")

  override def * =
    (id,title.?,description.?,mission.?,keyRequirement.?,offerRef,Offer_Type.?,domain.?,publishDate.?,expiredDate.?,userRef.?,closed.?) <> ((Offer.apply _).tupled, Offer.unapply)
//userRef.?
}
