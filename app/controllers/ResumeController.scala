package controllers

import akka.parboiled2.RuleTrace.Optional

import javax.inject.{Inject, Singleton}
import models.{AcademicBack, Resume}
import models.ResumesId.ResumeId
import models.enums.Roles.{Roles, candidate}
import play.api.data.Forms._
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.Configuration
import play.api.libs.json.Json
import services.ResumeService
import play.api.data.Form

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResumeController @Inject()(cc: ControllerComponents , resumeService: ResumeService)(implicit val conf:Configuration = Configuration.reference) extends AbstractController(cc) {

  private def ResumeForm: Form[Resume] = Form {
    mapping(
      "id" -> longNumber,
      "userRef" ->longNumber,
      "academicBackRef" ->nonEmptyText,
      "acadDate" -> nonEmptyText,
      "skills"->optional(nonEmptyText),
      "workExperience" -> optional(nonEmptyText),
      "workDate" -> optional(nonEmptyText),


    )(Resume.apply)(Resume.unapply)
  }



  case class createResumeForm(userRef:Long,academicBackground:String,acadDate:String,skills:Option[String],workExperience:Option[String],workDate:Option[String])

  private def resumeCreateForm[createResumeForm] = Form {
    mapping(

    "userRef" ->longNumber,
    "academicBackground" ->nonEmptyText,
    "acadDate"->nonEmptyText,
    "skills"->optional(nonEmptyText),
    "workExperience"->optional(nonEmptyText),
    "workDate"->optional(nonEmptyText)

    )(createResumeForm.apply)(createResumeForm.unapply)
  }
  /******  CREATE RESUME  ******/
  def createResume2(role:Roles)= Action.async{ implicit request =>
    resumeCreateForm.bindFromRequest.fold(
      ex => { Future(BadRequest(ex.errors.toString))
      },
      valid => {
        if (role == candidate) {
          val resume = Resume(1, valid.userRef, valid.academicBackground,valid.acadDate,valid.skills, valid.workExperience,valid.workDate)
          resumeService.createResume(resume: Resume).map {
            case Some(_) => Created(Json.toJson(resume))
            case None => BadRequest("Can't create Resume")
          }
        }
        else {
          Future(BadRequest("Resume can't be created for "+role))
        }
      }
    )
  }

  /******  READ RESUME  ******/
  def getResume(id: ResumeId)  = Action.async{ implicit request => {
      resumeService.getResumeById(id).map {
        offer => Ok(Json.toJson(offer))
      }
    }

  }
  /******  GET ALL RESUMES  ******/

  def getAllResume = Action.async{ implicit request =>
    resumeService.getAllResume.map{
      offer => Ok(Json.toJson(offer))
    }
  }

  /******  SEARCH RESUME  BY SKILLS ******/

  def searchResumeBySkills(skills : String ) = Action.async{ implicit request =>
    resumeService.searchBySkills(skills).map{
      resume => Ok(Json.toJson(resume))
    }
  }

  /******  UPDATE OFFER  ******/
  def updateResume (role:Roles) = Action.async { implicit request =>
    ResumeForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("invalid input"))},
      valid => {
        if (role == candidate) {
          resumeService.getResumeById(valid.id).flatMap(_ => resumeService.updateResume(valid)).map {
            case Some(_) => Ok("Resume updated")
            case None => Ok("Can't update Resume")
          }
        }
        else {
          Future(BadRequest("Resume can't be updated for "+role))
        }
      }
    )
  }




  def adWorkExp(id:Long, workExperience: String,workDate:String)= Action.async { implicit request =>

    resumeService.getResumeById(id).flatMap(_ => resumeService.adWorkExp(id, workExperience,workDate)).map {
      case true => Ok("added work experience")
      case false => Ok("can't added work experience")
    }
  }


  def createResume()= Action.async{ implicit request =>
    var id=0
    id+=1
    resumeCreateForm.bindFromRequest.fold(
      ex => { Future(BadRequest(ex.errors.toString))
      },
      valid => {
        val resume = Resume(id , valid.userRef, valid.academicBackground,valid.acadDate,valid.skills, valid.workExperience,valid.workDate)
        resumeService.createResume(resume: Resume).map {
          case Some(_) => Created(Json.toJson(resume))
          case None => BadRequest("Can't create Resume")
        }


      }
    )
  }

  def Skills(id:Long, skills:String)= Action.async { implicit request =>

    resumeService.getResumeById(id).flatMap(_ => resumeService.Skills(id, skills)).map {
      case true => Ok("added Skills ")
      case false => Ok("can't added Skills")
    }
  }


  def updateAcademic(id:Long, academicBackground:String,acadDate:String)= Action.async { implicit request =>

    resumeService.getResumeById(id).flatMap(_ => resumeService.updateAcademic(id, academicBackground,acadDate)).map {
      case true => Ok("added academic experience")
      case false => Ok("can't added academic experience")
    }
  }


  def getResumeByUser(userRef: Long)  = Action.async{ implicit request =>
    resumeService.getResumeByUser(userRef).map{
      app => Ok(Json.toJson(app))
    }
  }
  def deleteResume(id: ResumeId)= Action.async { implicit request =>
    resumeService.getResumeById(id).flatMap {
      case None => Future.successful(NotFound("resume doesn't exist"))
      case _ => resumeService.delete(id).map {
        case 0 => BadRequest("Can't delete Resume")
        case _ => Ok("resume deleted")
      }
    }
  }




}

