package models
import models.enums.AppStatus
import models.enums.AppStatus.AppStatus
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._


case class Application(id: ApplicationsId.ApplicationId, offerRef:Long, userRef: Long, applyDate: String, appStatus: AppStatus) extends BaseEntity[ApplicationsId.ApplicationId]

object Application{
  implicit val applicationFormat = Json.format[Application]

  implicit val AppStatusMapper: BaseColumnType[AppStatus]  = MappedColumnType.base[AppStatus, String](
    e => e.toString, s => AppStatus.withName(s)
  )
}
object ApplicationsId {
  type ApplicationId = Long
}

