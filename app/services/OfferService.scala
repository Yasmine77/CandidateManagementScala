package services

import models.OfferId.OfferId
import models.Offer
import models.enums.OfferType.OfferType
import play.api.db.slick.HasDatabaseConfigProvider
import repositories.OfferRepository
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

    class OfferService @Inject() (val dbConfigProvider: DatabaseConfigProvider, executionContext: ExecutionContext , offers : OfferRepository) extends HasDatabaseConfigProvider[JdbcProfile]  {
      def createOffer(offer: Offer ) = db.run(offers.save(offer)(executionContext))

      def getAllOffers = db.run(offers.all())

      def searchByTitle (title : String   ) =  db.run(offers.searchByTitle(title ))

      def searchByType (OfferType : OfferType   ) : Future[Seq[Offer]] =  db.run(offers.searchByType(OfferType :OfferType))

      def searchByOfferRef (offerRef : String   ) =  db.run(offers.searchByRef(offerRef))

      def getOfferById(id: OfferId) = db.run(offers.find(id))

      def updateOffer(offer: Offer) = db.run(offers.updateOffer(offer))

      def deleteOffer(id: OfferId) = db.run(offers.deleteById(id))

      def closed(id: OfferId , closed: Boolean) = offers.closed(id, closed)

      def getOpen() =  db.run(offers.getOpen())

      def getOfferByRef(id: OfferId) = db.run(offers.find(id))

      def getOfferJob() =  db.run(offers.getOfferJob())
      def getOfferInter() =  db.run(offers.getOfferInter())
      def getClosed() =  db.run(offers.getClosed())


    }
