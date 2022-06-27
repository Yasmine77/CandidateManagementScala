package services

import models.Application
import models.ApplicationsId.ApplicationId
import models.enums.AppStatus.AppStatus
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ApplicationRepository
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(val dbConfigProvider: DatabaseConfigProvider, applications: ApplicationRepository,executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  def createApplication(application: Application) = db.run(applications.save(application)(executionContext))

  def updateApplication(application: Application) = db.run(applications.updateApplication(application))

  def getApplicationById(id: ApplicationId) = db.run(applications.find(id))

  def updateStatus(id: ApplicationId,appStatus: AppStatus,application: Application) = applications.updateStatus(id,appStatus,application)

  def getApplicationByRefId(offerRef:Long,userRef: Long) = db.run(applications.getApplicationByRefId(offerRef,userRef))

  def getAppByOfferRef(offerRef:Long) = db.run(applications.getAppByOfferRef(offerRef))


  def getAppByUserRef(userRef:Long) = db.run(applications.getAppByUserRef(userRef))


  def countApplicant (offerRef : Long)=
    db.run(applications.countApplicant(offerRef))


}