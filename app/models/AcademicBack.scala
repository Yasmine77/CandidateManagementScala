package models

import play.api.libs.json.Json

case class AcademicBack(id: academicBackId.academicBackId, date:String, description:String,userRef:Long,resumeRef:Long) extends BaseEntity[academicBackId.academicBackId]

object AcademicBack{
  implicit val AcademicBackformat = Json.format[AcademicBack]


}
object academicBackId {
  type academicBackId = Long
}
