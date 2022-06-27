package models
import akka.http.javadsl.model.DateTime
import play.api.libs.json.Json.{fromJson, toJson}

import java.text.SimpleDateFormat

//import models.Role.Role
import models.enums.OfferType.OfferType
import models.enums.{OfferType, Roles}
import models.enums.Roles.Roles
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.text.SimpleDateFormat
//import slick.driver.MySQLDriver.MappedColumnType.MySQLDriver.MappedColumnType
import slick.jdbc.MySQLProfile.MappedColumnType
//role:Option[Role]
case class Offer (id:OfferId.OfferId, title:Option[String], description:Option[String],mission:Option[String],KeyRequirement:Option[String], offerRef:String, Offer_Type:Option[OfferType],domain:Option[String], publishDate:Option[String] , expiredDate:Option[String],userRef:Option[Long],closed:Option[Boolean]) //userRef:Option[Long]
  extends  BaseEntity[OfferId.OfferId]

object Offer {
  implicit val OfferMapper = MappedColumnType.base[OfferType, String](
    e => e.toString,
    s => OfferType.withName(s)
  )

  implicit val OfferFormat = Json.format[Offer]

}


object OfferId {
  type OfferId = Long
}


