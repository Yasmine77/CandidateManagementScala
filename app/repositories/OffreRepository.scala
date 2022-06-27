package repositories


import models.{Offer, User}
import models.UsersId.UserId
import models.tableDefinitions.{ApplicationTableDef, OfferTableDef, UserTableDef}
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import ch.qos.logback.core.util.InterruptUtil
import models.enums.OfferType.{OfferType, internshipOffer, jobOffer}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import com.github.t3hnar.bcrypt._
import models.OfferId.OfferId
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext


class OfferRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends BaseRepository[Offer, OfferTableDef,OfferId](dbConfigProvider, executionContext, TableQuery[OfferTableDef]) {

  implicit val Offers = TableQuery[OfferTableDef]

  def searchByTitle  (title : String   ) : DBIO[Option[Offer]] = Offers.filter(e=> e.title === title).result.headOption

  def searchByType (OfferType : OfferType ) : DBIO[Seq[Offer]] = Offers.filter(e=> e.Offer_Type === OfferType).result

  def searchByRef (offerRef : String   ) : DBIO[Option[Offer]] = Offers.filter(e=> e.offerRef === offerRef).result.headOption

  def updateOffer(offer: Offer) = update(offer.id,offer)

  def getOfferById  (id: OfferId  ) : DBIO[Option[Offer]] = Offers.filter(e=> e.id === id).result.headOption

  def closed (id: OfferId  , closed: Boolean) = {
    val query = for (offer <- Offers.filter(_.id === id))
      yield (offer.closed)
    db.run(query.update(closed)) map { _ > 0 }
  }
  def getOpen() : DBIO[Seq[Offer]] = Offers.filter(e=> e.closed === false).result
  def getClosed() : DBIO[Seq[Offer]] = Offers.filter(e=> e.closed === true).result

  def getOfferJob() : DBIO[Seq[Offer]] = Offers.filter(e=> e.Offer_Type === jobOffer && e.closed === false ).result
  def getOfferInter() : DBIO[Seq[Offer]] = Offers.filter(e=> e.Offer_Type === internshipOffer && e.closed === false ).result




}

