package controllers
import javax.inject.{Inject, Singleton}
import models.UsersId.UserId
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json, __}
import play.api.mvc.{AbstractController, ControllerComponents, Request, Result, Session}
import play.api.{Configuration, mvc}
import services.{ApplicationService, ProfileService, UserService}
import models.enums.Roles.{Roles, admin, candidate, manager, supervisor}
import org.mindrot.jbcrypt.BCrypt
import _root_.mapping.RoleMapping
import models.{Application, Profile, User}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserController @Inject()(cc: ControllerComponents ,userService: UserService, profileService:ProfileService,applicationService: ApplicationService)(implicit val conf:Configuration = Configuration.reference) extends AbstractController(cc) {
  //implicit val newTodoListJson = Json.format[NewTodoListItem]

  case class CreateUserForm(firstName: String, lastName: String, phoneNumber: String, username: String, email: String, role:Roles, password: String) //role: Option[Roles]

  private def userForm: Form[CreateUserForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "phoneNumber" -> nonEmptyText,
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
      "role" -> RoleMapping.RolesType,
      "password" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  private def updateUserForm: Form[User] = Form {
    mapping(
      "id" -> longNumber,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "phoneNumber" -> nonEmptyText,
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
      "role" -> RoleMapping.RolesType, //optional(RoleMapping.RolesType)
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  }

  /** *****    Method : (createUser OR register) AND createProfile   **** */

  def register()   = Action.async{ implicit  request =>

    userForm.bindFromRequest.fold(
      ex => {
        Future(BadRequest(ex.errors.toString))
      },
      valid =>  {
        val user = User(10, valid.firstName, valid.lastName, valid.phoneNumber, valid.username, valid.email, valid.role, BCrypt.hashpw(valid.password, BCrypt.gensalt(12))) //Role.Candidate,

        userService.createUser(user).map{

          case Some(user) => Created(Json.toJson(user))

          case None => BadRequest("cannot create user ! ")

        }})
  }
  def createUser(email: String) = Action.async { implicit request =>

    userService.checkEmail(email).flatMap {

      case Some(_) =>
        Future(BadRequest("User already exists!"))

      case None =>
        userForm.bindFromRequest.fold(
          ex => {
            Future(BadRequest(ex.errors.toString))
          },
          valid => {
            val user = User(10, valid.firstName, valid.lastName, valid.phoneNumber, valid.username, valid.email, valid.role, BCrypt.hashpw(valid.password, BCrypt.gensalt(12))) //Role.Candidate,
            userService.createUser(user).map {
              case Some(user) => {
                val profile = Profile(1, "", user.id)
                profileService.createProfile(profile).map {
                  case Some(profile) =>{Created(Json.toJson(profile)) }
                  case None => BadRequest("profile can not be created")
                }
                //Ok(Json.toJson(profile))
                Ok(Json.toJson(user))
              }
              case None => BadRequest("cannot create user ! ")
            }
          })
    }
  }


  def getUser(id: UserId) = Action.async { implicit request =>
    userService.getUserById(id).map {
      users => Ok(Json.toJson(users))
    }
  }

  def login(email: String, password: String) = Action.async(new mvc.BodyParsers.Default(cc.parsers)) { implicit request =>
    var idV: UserId = 0

    userService.checkEmail(email)
      .flatMap {
        case Some(User(id, _, _, _, username2, _,roleAffected, password2)) => {
          val user =User(id, _, _, _, username2, _,roleAffected, password2)
          val check1 = BCrypt.checkpw(password, password2)
          //println("roleAffected"+ roleAffected.get.toString)
          idV = id
          check1 match {
            case true => {
              userService.getUserById(id).map {
                users => Ok(Json.toJson(users))

              }
              //Future.successful(Ok("You are logged in"))
            }
            case false => Future(Unauthorized("Invalid username or password"))
          }

        }

        case None => Future(Unauthorized("Can't find user"))
      }

  }
  /** *****    Form : ResetPassword   **** */
  case  class resetPassword (oldPassword: String, NewPassword: String,ConfirmNewPassword: String)
  private def resetPasswordForm: Form[resetPassword] = Form {
    mapping(
      "oldPassword" -> nonEmptyText,
      "NewPassword" -> nonEmptyText,
      "ConfirmNewPassword" -> nonEmptyText,
    )(resetPassword.apply)(resetPassword.unapply)
  }
  /** *****    Methode : ResetPassword   **** */
  def resetPassword(email: String) = Action.async { implicit request =>
    resetPasswordForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("Invalid input"))
      },
      valid => {
        userService.checkEmail(email)
          .flatMap {
            case Some(User(id, _, _, _,_, _, _, password2))  => {
              val check1 = BCrypt.checkpw(valid.oldPassword, password2)
              check1 match {
                case true => {
                  val newPassword = BCrypt.hashpw(valid.NewPassword, BCrypt.gensalt(12))
                  val check2 = BCrypt.checkpw(valid.ConfirmNewPassword,newPassword)
                  check2 match {
                    case true =>{
                      val confirmNewPassword = BCrypt.hashpw(valid.ConfirmNewPassword, BCrypt.gensalt(12))
                      userService.ChangePassword(id, confirmNewPassword).map {
                        case true => Ok("Password updated")
                        case false => Ok("Can't update password")
                      }
                    }
                    case false=> {
                      Future(Unauthorized("Password and confirm password does not match"))
                    }
                  }
                 // Future.successful(Ok("Your password is changed"))
                }
                case false => Future(Unauthorized("Your current password is incorrect"))
              }
            }
            case None => Future(Unauthorized)
          }
      }
    )
  }
  /** ***** Delete User **** */

  def deleteUser(id: UserId) = Action.async { implicit request =>
    userService.getUserById(id).flatMap {
      case None => Future.successful(NotFound("User doesn't exist"))
      case _ => userService.delete(id).map {
        case 0 => BadRequest("Can't delete user")
        case _ => Ok("User deleted")
      }
    }
  }
  /** ***** update User **** */

  def updateUser() = Action.async{ implicit request =>
    updateUserForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("Invalid input"))
      },
      valid => {
          valid.password = BCrypt.hashpw(valid.password,BCrypt.gensalt(12))
        userService.getUserById(valid.id).flatMap(_ => userService.updateUser(valid)).map {
          case Some(_) => Ok("User updated")
          case None => Ok("Can't update user")

        }
      }
    )
  }

  /** ***** update User Probleme**** */

  case  class userUpdate (id: UserId,firstName: String, lastName: String,phoneNumber:String,username:String,email:String)
  private def userUpdateForm: Form[userUpdate] = Form {
    mapping(
      "id" -> longNumber,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "phoneNumber" -> nonEmptyText,
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
    )(userUpdate.apply)(userUpdate.unapply)
  }
  def usersUpdate() = Action.async{ implicit request =>
    userUpdateForm.bindFromRequest.fold(
      _ => {
        Future(BadRequest("Invalid input"))
      },
      valid => {
        userService.getUserById(valid.id).flatMap(_ => userService.userUpdate(valid.id,valid.firstName,valid.lastName,valid.phoneNumber,valid.username,valid.email)).map {
          case true => Ok("User updated")
          case false => Ok("Can't update user")

        }
      }
    )
  }
  /** ***** update User Probleme**** */

  /****** FORM CHANGE ROLE ******/
  case class changeRoleForm(id:Long,AffectRole:Roles)
  private def roleForm: Form[changeRoleForm] = Form {
    mapping(
      "id"-> longNumber,
      "AffectRole" -> RoleMapping.RolesType,
    )(changeRoleForm.apply)(changeRoleForm.unapply)
  }

  /****** METHODE CHANGE ROLE  STATUS ******/
  def changeRole(id:Long,roleAffected:Roles):Action[AnyContent] = Action.async { implicit request =>

    userService.getUserById(id).flatMap(_ => userService.ChangeRole(id, roleAffected)).map {
      case true => Ok("Status updated")
      case false => Ok("Can't update status")
    }

  }


  /****** to review ******/

  def changeRole2(id:Long,roleAffected:Roles,userRole: Roles):Action[AnyContent] = Action.async{ implicit request =>

    if (userRole == admin ) {
      userService.getUserById(id).flatMap(_ => userService.ChangeRole(id,roleAffected)).map {
        case true => Ok("Status updated")
        case false=> Ok("Can't update status")
      }
    }
    else {
      Future(BadRequest("Status can't be changed by "+userRole))
    }


  }
  /****** to review ******/

  def getAllUsers = Action.async{ implicit request =>
    userService.getAllUsers.map{
      users => Ok(Json.toJson(users))
    }
  }
  def searchUserByRole(role : Roles ) = Action.async{ implicit request =>
    userService.searchByRole(role).map{
      user => Ok(Json.toJson(user))
    }
  }
}





