package controllers
  import models.ProfilesId.ProfileId
  import models.{Offer, Profile, User}
  import play.api.Configuration
  import play.api.data.Form
  import play.api.data.Forms._
  import _root_.mapping.RoleMapping
  import models.UsersId.UserId
  import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
  import play.api.libs.json.Json
  import play.api.mvc.{AbstractController, ControllerComponents}
  import services.{OfferService, ProfileService, UserService}
  import slick.jdbc.JdbcProfile

  import scala.concurrent.ExecutionContext.Implicits.global
  import javax.inject.Inject
  import scala.concurrent.Future


  class ProfileController @Inject()(val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents , profileService: ProfileService , userService: UserService)
                                 (implicit val conf:Configuration = Configuration.reference)
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{


    case class createProfileForm( profileType: String/*ref:Long*/)
    private def ProfileForm: Form[createProfileForm] = Form {
      mapping(
        "profileType" -> nonEmptyText,
/*
      "ref" -> longNumber,
*/
      )(createProfileForm.apply)(createProfileForm.unapply)
    }

    private def userForm: Form[User] = Form {
      mapping(
        "id" -> longNumber,
        "firstName" -> nonEmptyText,
        "lastName" -> nonEmptyText,
        "phoneNumber" -> nonEmptyText,
        "username" -> nonEmptyText,
        "email" -> nonEmptyText,
        "role" -> RoleMapping.RolesType,
        "password" -> nonEmptyText
      )(User.apply)(User.unapply)
    }
    /*****   Create Profile  *****/
    var userID:Long = 0

    def createProfile(email: String) = Action.async{ implicit request =>

      ProfileForm.bindFromRequest.fold(
        e => { Future(BadRequest(e.errors.toString)) },
        valid => {
          userService.checkEmail(email).flatMap{

                case Some(User(id, _,_,_,_,_,_,_)) => userID = id
                  val profile = Profile(1, valid.profileType, userID)
                  profileService.createProfile(profile).map{
                    case Some(profile) => Created(Json.toJson(profile))
                    case None => BadRequest("profile can not be created")
                  }
          }

        }
      )
    }

    /******  READ OFFER  ******/
    def getProfile(id: ProfileId)  = Action.async{ implicit request =>
      profileService.getProfileById(id).map{
        offer => Ok(Json.toJson(offer))
      }
    }

    def getProfileByUserId(userRef: UserId)  = Action.async{ implicit request =>
      profileService.getProfileByUserId(userRef).map{
        profile => Ok(Json.toJson(profile))
      }
    }

    private def uprofileForm: Form[Profile] = Form {
      mapping(
        "id" -> longNumber,
        "profileType" -> nonEmptyText,
        "UserRef" -> longNumber,
      )(Profile.apply)(Profile.unapply)
    }

    /******  UPDATE Profile  ******/
    def updateProfile = Action.async { implicit request =>
      uprofileForm.bindFromRequest.fold(
        _ => {
          Future(BadRequest("invalid input"))},
        valid => {
          profileService.getProfileById(valid.id).flatMap(_ => profileService.updateProfile(valid)).map{
            case Some(_) => Ok("profile updated")
            case None => Ok("Can't update profile")
          }
        }
      )
    }
    /*****   Delete Profile  *****/
    def deleteProfile(id: ProfileId) = Action.async{ implicit request =>
      profileService.getProfileById(id).flatMap{
        case None => Future.successful(NotFound("Can't find profile"))
        case _ => profileService.deleteProfile(id).map{
          case 0 => BadRequest("Can't delete profile")
          case _ => Ok("Profile deleted")
        }
      }
    }
  }
