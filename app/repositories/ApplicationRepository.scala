package repositories

import models.Application
import models.ApplicationsId.ApplicationId
import models.enums.AppStatus.AppStatus
import models.tableDefinitions.ApplicationTableDef
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ApplicationRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BaseRepository[Application, ApplicationTableDef, ApplicationId](dbConfigProvider,executionContext,TableQuery[ApplicationTableDef]){

  implicit val applications = TableQuery[ApplicationTableDef]

  def getById(id: Long) = applications.filter(a => a.id === id).result.headOption

  def updateApplication(application: Application) = update(application.id,application)

 /* def updateStatus(id: ApplicationId,application: Application)= {
    db.run(applications.filter(_.id === id)
      .map(x => (x.appStatus))
      .update(application.appStatus)
    )
  }*/

  def updateStatus(id: ApplicationId, appStatus: AppStatus, application: Application)= {
      val query = for (application <- applications.filter(_.id === id))
        yield (application.appStatus)
      db.run(query.update(appStatus)) map {
        _ > 0
      }

  }

  def getApplicationByRefId (offerRef: Long,userRef:Long) : DBIO[Option[Application]] = applications.filter(e => e.offerRef === offerRef && e.userRef===userRef ).result.headOption
  def getAppByOfferRef (offerRef: Long) : DBIO[Seq[Application]] = applications.filter(e => e.offerRef === offerRef ).result
  def countApplicant (offerRef : Long)  ={
    applications.filter(e => e.offerRef === offerRef ).size.result

  }

  def getAppByUserRef (userRef: Long) : DBIO[Seq[Application]] = applications.filter(e => e.userRef === userRef ).result

}