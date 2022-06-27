package models

import play.api.libs.json.Json

case class Profile (id:ProfilesId.ProfileId, profileType:String, userRef:Long) extends BaseEntity[ProfilesId.ProfileId]


object Profile {
  implicit val ProfileFormat = Json.format[Profile]
}

object  ProfilesId {
  type ProfileId = Long
}