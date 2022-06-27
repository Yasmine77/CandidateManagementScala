package mapping

import models.enums.Roles
import play.api.data.{FormError, Forms, Mapping}
import play.api.data.format.Formatter

object RoleMapping {

  implicit val appTypeFormatter = new Formatter[Roles.Roles] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Roles.Roles] = {
      data.get(key).map { value =>
        try {
          Right(Roles.withName(value))
        } catch {
          case e: NoSuchElementException => error(key, value + " is not a valid role type")
        }
      }.getOrElse(error(key, "No role type provided."))
    }

    private def error(key: String, msg: String) = Left(List(new FormError(key, msg)))

    override def unbind(key: String, value: Roles.Roles): Map[String, String] = {
      Map(key -> value.toString())
    }
  }

  def RolesType: Mapping[Roles.Roles] = Forms.of[Roles.Roles]
}