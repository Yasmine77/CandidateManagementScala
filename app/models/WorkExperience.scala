package models

import play.api.libs.json.Json

case class WorkExperience(id: workExperienceId.workExperienceId, date:String, description:String ) extends BaseEntity[academicBackId.academicBackId]

object WorkExperience{
  implicit val WorkExperienceformat = Json.format[WorkExperience]


}
object workExperienceId {
  type workExperienceId = Long
}