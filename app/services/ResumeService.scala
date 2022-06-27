package services
import models.OfferId.OfferId
import models.ResumesId.ResumeId
import models.{AcademicBack, Offer, Resume}
import models.enums.OfferType.OfferType
import play.api.db.slick.HasDatabaseConfigProvider
import repositories.{AcademicRepository, OfferRepository, ResumeRepository}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

class ResumeService @Inject() (val dbConfigProvider: DatabaseConfigProvider, executionContext: ExecutionContext , resumes : ResumeRepository,academicBacks:AcademicRepository) extends HasDatabaseConfigProvider[JdbcProfile] {

  def createResume(resume: Resume) = db.run(resumes.save(resume)(executionContext))

  def getAllResume = db.run(resumes.all())

  def searchBySkills(skills: String): Future[Seq[Resume]] = db.run(resumes.searchByskills(skills: String))

  def getResumeById(id: ResumeId) = db.run(resumes.find(id))

  def updateResume(resume: Resume) = db.run(resumes.updateResume(resume: Resume))

   def deleteResume(id: OfferId) = db.run(resumes.deleteById(id))
  def getResumeByUserId(userId: Long) = db.run(resumes.getResumeByUserId(userId))

  def backgroundSave(resume: Resume) = db.run(resumes.save(resume)(executionContext))

    def adWorkExp(id: ResumeId , workExperience: String,workDate:String) = resumes. adWorkExp(id , workExperience,workDate)
  def getResumeByUser(userRef: Long) = db.run(resumes.getResumeByUser(userRef))

  def Skills(id: ResumeId, skills: String) = resumes.Skills(id, skills)

  def delete(id: ResumeId) = db.run(resumes.deleteById(id))
    def updateAcademic(id: ResumeId ,academicBackground:String,acadDate:String) = resumes.updateAcademic(id , academicBackground,acadDate)

  def createAcademic(academicBack: AcademicBack) = db.run(academicBacks.save(academicBack)(executionContext))


  def getacademicByUserRef(userRef : Long): Future[Seq[AcademicBack]] = db.run(academicBacks.getacademicByUserRef(userRef : Long))
}