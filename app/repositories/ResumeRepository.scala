package repositories
import models.{Offer, Profile, Resume, User}
import models.tableDefinitions.{OfferTableDef, ResumeTableDef, UserTableDef}
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import slick.jdbc.MySQLProfile.api._
import models.ResumesId.ResumeId
import slick.dbio.DBIO

class ResumeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends BaseRepository[Resume, ResumeTableDef,ResumeId](dbConfigProvider, executionContext, TableQuery[ResumeTableDef]) {

  implicit val resumes = TableQuery[ResumeTableDef]


  def searchByskills (skills : String ) : DBIO[Seq[Resume]] = resumes.filter(e=> e.skills === skills).result


  def updateResume(resume: Resume) = update(resume.id,resume)

  def getResumeByUserId (userId: Long) : DBIO[Option[Resume]] = resumes.filter(e => e.userRef=== userId).result.headOption

 def adWorkExp(id: ResumeId , workExperience: String,workDate:String) = {
    val query = for (resume <- resumes.filter(_.id === id))
      yield (resume.workExperience,resume.workDate)
    db.run(query.update(workExperience,workDate)) map { _ > 0 }
  }

  def Skills (id: ResumeId , skills: String) = {
    val query = for (resume <- resumes.filter(_.id === id))
      yield (resume.skills)
    db.run(query.update(skills)) map { _ > 0 }
  }
  def updateAcademic(id: ResumeId , academicBackground:String,acadDate:String) = {
    val query = for (resume <- resumes.filter(_.id === id))
      yield (resume.academicBackground,resume.acadDate)
    db.run(query.update(academicBackground,acadDate)) map { _ > 0 }
  }

  def getResumeByUser  (userRef: Long  ) : DBIO[Option[Resume]] = resumes.filter(e=> e.userRef === userRef).result.headOption





}
