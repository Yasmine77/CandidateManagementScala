package models.enums

import models.enums.AppStatus.AppStatus
import play.api.libs.json.{Format, JsString, JsSuccess, JsValue, Json, Reads, Writes}
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.MappedColumnType
import play.api.mvc.QueryStringBindable
object AppStatus extends Enumeration {
  type AppStatus = Value
  val noResponse = Value("noResponse")
  val accepted = Value("accepted")
  val inProgress = Value("inProgress")
  val refused = Value("refused")
  //val A, B, C = Value // if you want to use a,b,c instead, feel free to do it
  implicit val AppStatusMapper: BaseColumnType[AppStatus] = MappedColumnType.base[AppStatus, String](
    e => e.toString,
    s => AppStatus.withName(s)
  )

  implicit val readsMyEnum = Reads.enumNameReads(AppStatus)
  implicit val writesMyEnum = Writes.enumNameWrites
  /*****   ADD OFFERTYPE TO ROUTES SO ROUTES CAN DEFINE OFFERTYPE  AND SEARCH BY TYPE   *****/
  implicit val queryStringBindable: QueryStringBindable[AppStatus] =
    new QueryStringBindable[AppStatus] {
      override def bind(
                         key: String,
                         params: Map[String, Seq[String]]
                       ): Option[Either[String, AppStatus]] =
        params.get(key).collect {
          case Seq(s) =>
            AppStatus.values.find(_.toString == s).toRight("invalid value")
        }
      override def unbind(key: String, value: AppStatus): String =
        implicitly[QueryStringBindable[String]].unbind(key, value.toString)
    }


}
