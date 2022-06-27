package models

import play.api.libs.json.Json

case class Resume (id:ResumesId.ResumeId, userRef:Long, academicBackground:String,acadDate:String,skills:Option[String],workExperience:Option[String],workDate:Option[String]) extends BaseEntity[ResumesId.ResumeId]
object Resume {
  implicit val ResumeFormat = Json.format[Resume]
}
object  ResumesId {
  type ResumeId = Long
}

