package repositories
import models.{AcademicBack, Profile, academicBackId}
import models.academicBackId.academicBackId
import models.tableDefinitions.{AcademicBackTableDefinition, ProfileTableDef}
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import slick.jdbc.MySQLProfile.api._
class AcademicRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BaseRepository[AcademicBack, AcademicBackTableDefinition, academicBackId](dbConfigProvider, executionContext, TableQuery[AcademicBackTableDefinition]){

  implicit val academicBacks = TableQuery[AcademicBackTableDefinition]

  def getById (id: academicBackId) : DBIO[Option[AcademicBack]] = academicBacks.filter(e => e.id === id).result.headOption

  def updateAcademic(id: academicBackId , date:String,description:String) = {
     val query = for (academicBack <- academicBacks.filter(_.id === id))
       yield (academicBack.description,academicBack.date)
     db.run(query.update(date,description)) map { _ > 0 }
   }
  def getacademicByUserRef (userRef : Long ) : DBIO[Seq[AcademicBack]] = academicBacks.filter(e=> e.userRef === userRef).result

}
