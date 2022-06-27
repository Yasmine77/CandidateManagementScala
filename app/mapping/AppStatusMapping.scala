package mapping

import models.enums.AppStatus
import models.enums.AppStatus.AppStatus
import play.api.data.{FormError, Forms, Mapping}
import play.api.data.format.Formatter

object AppStatusMapping {

  implicit val appTypeFormatter = new Formatter[AppStatus] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], AppStatus] = {
      data.get(key).map { value =>
        try {
          Right(AppStatus.withName(value))
        } catch {
          case _: NoSuchElementException => error(key, value + " is not a valid application status")
        }
      }.getOrElse(error(key, "No application status provided"))
    }

    private def error(key: String, msg: String) = Left(List(new FormError(key, msg)))

    override def unbind(key: String, value: AppStatus): Map[String, String] = {
      Map(key -> value.toString())
    }
  }
  def App_Status: Mapping[AppStatus.AppStatus] = Forms.of[AppStatus.AppStatus]
}
