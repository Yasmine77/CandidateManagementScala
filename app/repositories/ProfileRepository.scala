package repositories

import models.Profile
import models.ProfilesId.ProfileId
import models.tableDefinitions.ProfileTableDef
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.ExecutionContext

import slick.jdbc.MySQLProfile.api._



class ProfileRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BaseRepository[Profile, ProfileTableDef, ProfileId](dbConfigProvider, executionContext, TableQuery[ProfileTableDef]){

  implicit val profiles = TableQuery[ProfileTableDef]

  def getById (id: ProfileId) : DBIO[Option[Profile]] = profiles.filter(e => e.id === id).result.headOption

  def updateProfile(profile: Profile) = update(profile.id,profile)

  def getProfileByUserId (userRef: Long) : DBIO[Option[Profile]] = profiles.filter(e => e.userRef === userRef).result.headOption

}

