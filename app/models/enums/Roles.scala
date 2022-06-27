package models.enums
import play.api.libs.json.{Format, JsString, JsSuccess, JsValue, Json, Reads, Writes}
import slick.jdbc.MySQLProfile.api._
import play.api.mvc.QueryStringBindable
import slick.jdbc.MySQLProfile.MappedColumnType
object Roles extends Enumeration {
  type Roles = Value
  val admin = Value("admin")
  val candidate = Value("candidate")
  val manager = Value("manager")
  val supervisor = Value("supervisor")
  //val A, B, C = Value // if you want to use a,b,c instead, feel free to do it
  implicit val roleMapper: BaseColumnType[Roles] = MappedColumnType.base[Roles, String](
    e => e.toString,
    s => Roles.withName(s)
  )
implicit val readsMyEnum = Reads.enumNameReads(Roles)
implicit val writesMyEnum = Writes.enumNameWrites

  /*****   ADD OFFERTYPE TO ROUTES SO ROUTES CAN DEFINE OFFERTYPE  AND SEARCH BY TYPE   *****/
  implicit val queryStringBindable: QueryStringBindable[Roles] =
    new QueryStringBindable[Roles] {
      override def bind(
                         key: String,
                         params: Map[String, Seq[String]]
                       ): Option[Either[String, Roles]] =
        params.get(key).collect {
          case Seq(s) =>
            Roles.values.find(_.toString == s).toRight("invalid value")
        }
      override def unbind(key: String, value: Roles): String =
        implicitly[QueryStringBindable[String]].unbind(key, value.toString)
    }


}

