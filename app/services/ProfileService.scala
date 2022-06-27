package services

import com.google.inject.Inject
import models.Profile
import models.ProfilesId.ProfileId
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ProfileRepository
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


class ProfileService @Inject()(val dbConfigProvider: DatabaseConfigProvider, profiles: ProfileRepository, executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  def createProfile(profile: Profile) = db.run(profiles.save(profile)(executionContext))

  def getProfileById(id: ProfileId) = db.run(profiles.find(id))

  def updateProfile(profile: Profile) = db.run(profiles.updateProfile(profile))

  def deleteProfile(id: ProfileId) = db.run(profiles.deleteById(id))

  def getProfileByUserId(userRef: Long) = db.run(profiles.getProfileByUserId(userRef))

}
