package controllers
import models.Application
import models.enums.AppStatus._
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import _root_.mapping.AppStatusMapping
import models.OfferId.OfferId
import models.UsersId.UserId
import models.enums.Roles.{Roles, admin, candidate, manager}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{ApplicationService, OfferService, ProfileService, ResumeService}
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationController @Inject()(val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents, applicationService: ApplicationService,profileService: ProfileService,resumeService: ResumeService,offerService: OfferService)
                                     (implicit val conf:Configuration = Configuration.reference)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{


  /***** CREATE APPLICATION *****/
  def Apply2(offerId: Long, userId: Long,role:Roles) = Action.async { implicit request =>
    if (role == candidate) {
      applicationService.getApplicationByRefId(offerId, userId).flatMap {
        case Some(application) =>
          Future.successful(Ok("You already applied check your application"))
        case _ =>
          resumeService.getResumeByUserId(userId).flatMap {
            case Some(_) =>
              val application = Application(0, offerId, userId, LocalDate.now.toString, noResponse)
              applicationService.createApplication(application).map {
                case Some(_) =>
                  Created(Json.toJson(application))
                case None =>
                  BadRequest("Can't submit application")
              }
            case _ =>
              Future.successful(BadRequest("A resume should be created in order to apply for offers."))
          }
      }
    }
    else {
      Future(BadRequest("Application can't be created for "+role))
    }
  }
  /***** CREATE APPLICATION *****/
  /***** CREATE APPLICATION *****/
  def Apply(offerId: Long, userId: Long) = Action.async { implicit request =>

      applicationService.getApplicationByRefId(offerId, userId).flatMap {
        case Some(application) =>
          Future.successful(Ok("You already applied check your application"))
        case _ =>
          resumeService.getResumeByUserId(userId).flatMap {
            case Some(_) =>
              val application = Application(0, offerId, userId, LocalDate.now.toString, noResponse)
              applicationService.createApplication(application).map {
                case Some(_) =>
                  Created(Json.toJson(application))
                case None =>
                  BadRequest("Can't submit application")
              }
            case _ =>
              Future.successful(BadRequest("A resume should be created in order to apply for offers."))
          }
      }


  }
  /***** CREATE APPLICATION *****/
  def ApplyOnlyOnce(offerRef:Long, userRef: Long):Action[AnyContent] = Action.async{ implicit request =>
    applicationService.getApplicationByRefId(offerRef,userRef).flatMap
    {
      case Some(_) =>
        Future.successful(Ok("You already applied check your application"))

      case None =>
        val application = Application(0, offerRef, userRef, LocalDate.now.toString,noResponse)
        applicationService.createApplication(application).map{
          case Some(_) => Created("Application submitted")
          case None => BadRequest("Can't submit application")
        }

    }
  }

  /****** FORM CHANGE APPLICATION STATUS ******/
  case class updateStatusForm(id: Long, appStatus: AppStatus)
  private def statusForm: Form[updateStatusForm] = Form {
    mapping(
      "id"-> longNumber,
      "appStatus" -> AppStatusMapping.App_Status
    )(updateStatusForm.apply)(updateStatusForm.unapply)
  }
  /****** METHODE CHANGE APPLICATION STATUS ******/
 /* def updateStatus2(id:Long,appStatus:AppStatus,role:Roles):Action[AnyContent] = Action.async{ implicit request =>

        if (role == admin || role == manager) {
          val app = Application(id, 0, 0, "",appStatus)
          applicationService.getApplicationById(id).flatMap(_ => applicationService.updateStatus(id,app)).map {
            case 1 => Ok("Status updated")
            case 0 => Ok("Can't update status")
          }
        }
        else {
          Future(BadRequest("Status can't be changed by "+role))
        }


  }*/
  /****** METHODE CHANGE APPLICATION STATUS ******/
  def updateStatus(id:Long,appStatus:AppStatus):Action[AnyContent] = Action.async{ implicit request =>

    val app = Application(id, 0, 0, "",appStatus)
      applicationService.getApplicationById(id).flatMap(_ => applicationService.updateStatus(id,appStatus,app)).map {
        case true => Ok("Status Updated")
        case false => BadRequest("Status can't be updated")
      }

  }
  def getAppByOfferRef(offerRef: Long)  = Action.async{ implicit request =>
    applicationService.getAppByOfferRef(offerRef).map{
      app => Ok(Json.toJson(app))
    }
  }
def countAppByOffers(offerRef: Long)=Action.async { implicit request =>
  applicationService.countApplicant(offerRef).map {
    app => Ok(Json.toJson(app))
  }
}
  def getAppByUserRef(userRef: Long)  = Action.async{ implicit request =>
    applicationService.getAppByUserRef(userRef).map{
      app => Ok(Json.toJson(app))
    }
  }



}




