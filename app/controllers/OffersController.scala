package controllers
import javax.inject.{Inject, Singleton}
import models.Offer
import play.api.data.Forms._
import _root_.mapping.OfferMapping
import models.OfferId.OfferId
import models.enums.OfferType.OfferType
import models.enums.Roles.{Roles, admin, manager, supervisor}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.Configuration
import play.api.libs.json.Json
import services.{OfferService, UserService}
import scala.concurrent.Future
import play.api.data.Form

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable

@Singleton
class OffersController @Inject()(cc: ControllerComponents , offerService: OfferService,userService: UserService)(implicit val conf:Configuration = Configuration.reference) extends AbstractController(cc) {
  private val OfferList = new mutable.ListBuffer[Offer]()

  private def offerForm: Form[Offer] = Form {
    mapping(
      "id" -> longNumber,
      "title" ->optional(nonEmptyText),
      "description" ->optional(nonEmptyText),
      "mission" ->optional(nonEmptyText),
      "keyRequirement" ->optional(nonEmptyText),
      "offerRef"->nonEmptyText,
      "offer_Type" -> optional(OfferMapping.Offer_Type),
      "domain"->optional(nonEmptyText),
      "publishDate"->optional(nonEmptyText),
      "expiredDate"->optional(nonEmptyText),
      "userRef"->optional(longNumber), //optional(longNumber)
      "closed"->optional(boolean)

    )(Offer.apply)(Offer.unapply)
  }

  case class createOfferForm( title: Option[String],description:Option[String],mission:Option[String],keyRequirement:Option[String],offerRef:String,offer_Type:Option[OfferType],domain:Option[String],publishDate:Option[String],expiredDate:Option[String],userRef:Option[Long],closed:Option[Boolean]) //Option[Long]
  private def offerCreateForm[createOfferForm] = Form {
    mapping(
      "title" ->optional(nonEmptyText),
      "description" ->optional(nonEmptyText),
      "mission" ->optional(nonEmptyText),
      "keyRequirement" ->optional(nonEmptyText),
      "offerRef"->nonEmptyText,
      "offer_Type" -> optional(OfferMapping.Offer_Type),
      "domain"->optional(nonEmptyText),
      "publishDate"->optional(nonEmptyText),
      "expiredDate"->optional(nonEmptyText),
      "userRef"->optional(longNumber), //optional(longNumber)
      "closed"->optional(boolean)

    )(createOfferForm.apply)(createOfferForm.unapply)
  }
  /******  CREATE OFFER  with roles ******/
  def createOffers(role:Roles)= Action.async{ implicit request =>
    offerCreateForm.bindFromRequest.fold(
      ex => { Future(BadRequest(ex.errors.toString))
      },

      valid => {
        val offer = Offer(1, valid.title, valid.description,valid.mission,valid.keyRequirement, valid.offerRef, valid.offer_Type, valid.domain, valid.publishDate, valid.expiredDate, valid.userRef,valid.closed)
        if (role == admin || role == manager || role == supervisor) {
          offerService.createOffer(offer: Offer).map {
            case Some(_) => Created(Json.toJson(offer))
            case None => BadRequest("offer can't be created")
          }
        }
        else {
          Future(BadRequest("offer can't be created for "+role))
        }

      }
    )
  }
  /******  review OFFER  without roles ******/


  def createOffer()= Action.async{ implicit request =>
    offerCreateForm.bindFromRequest.fold(
      ex => { Future(BadRequest(ex.errors.toString))
      },
      valid => {
        val offer = Offer(10, valid.title, valid.description,valid.mission,valid.keyRequirement, valid.offerRef, valid.offer_Type, valid.domain, valid.publishDate, valid.expiredDate, valid.userRef,valid.closed)

          offerService.createOffer(offer: Offer).map {
            case Some(_) => Created(Json.toJson(offer))
            case None => BadRequest("offer can't be created")
          }

      }
    )
  }
  /******  READ OFFER  ******/
  def getOffer(id: OfferId)  = Action.async{ implicit request =>
    offerService.getOfferById(id).map{
      offer => Ok(Json.toJson(offer))
    }
  }
  /******  SEARCH OFFER  BY TYPE ******/

  def searchOfferByType(OfferType : OfferType )= Action.async{ implicit request =>
      offerService.searchByType(OfferType).map {
        offer => Ok(Json.toJson(offer))
      }
  }
  /******  SEARCH OFFER  BY TITLE ******/

  def searchOfferByTitle(title : String ) = Action.async{ implicit request =>
    offerService.searchByTitle(title).map{
      offer => Ok(Json.toJson(offer))
    }
  }

  /******  UPDATE OFFER  ******/
  def updateOffer2(role:Roles) = Action.async { implicit request =>
    offerForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("invalid input"))},
      valid => {
        if (role == admin || role == manager || role == supervisor) {
          offerService.getOfferById(valid.id).flatMap(_ => offerService.updateOffer(valid)).map {
            case Some(_) => Ok("Offer updated")

            case None => Ok("Can't update offer")
          }
        }
        else {
          Future(BadRequest("offer can't be created for "+role))
        }
      }

    )
  }
  def getAllOffers = Action.async{ implicit request =>
    offerService.getAllOffers.map{
      offers => Ok(Json.toJson(offers))
    }
  }
  /******  UPDATE OFFER  ******/
  def updateOffer() = Action.async { implicit request =>
    offerForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("invalid input"))},
      valid => {

          offerService.getOfferById(valid.id).flatMap(_ => offerService.updateOffer(valid)).map {
            case Some(_) => Ok("Offer updated")

            case None => Ok("Can't update offer")
          }


      }

    )
  }
  def closed(id:Long,closed:Boolean)= Action.async{ implicit request =>

    offerService.getOfferById(id).flatMap(_ => offerService.closed(id,closed)).map {
        case true => Ok("Status updated")
        case false=> Ok("Can't update status")
      }
  }

  def getOpen() = Action.async{ implicit request =>
    offerService.getOpen().map{

      offers=> Ok(Json.toJson(offers))
    }
  }

  def getClosed() = Action.async{ implicit request =>
    offerService.getClosed().map{

      offers=> Ok(Json.toJson(offers))
    }
  }
  def searchByOfferRef(offerRef: String)  = Action.async{ implicit request =>
    offerService.searchByOfferRef(offerRef).map{
      offer => Ok(Json.toJson(offer))
    }
  }
  def getOfferJob() = Action.async{ implicit request =>
    offerService.getOfferJob().map{

      offers=> Ok(Json.toJson(offers))
    }
  }


  def getOfferInter() = Action.async{ implicit request =>
    offerService.getOfferInter().map{

      offers=> Ok(Json.toJson(offers))
    }
  }
}

