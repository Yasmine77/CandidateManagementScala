package models.enums
import play.api.libs.json.{Format, JsString, JsSuccess, JsValue, Json, Reads, Writes}
import play.api.mvc.QueryStringBindable
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.MappedColumnType

object OfferType extends Enumeration {
  type OfferType = Value
  val internshipOffer = Value("internshipOffer")
  val jobOffer = Value("jobOffer")

  /*****   OBJECT <=> STRING   *****/
  //val A, B, C = Value // if you want to use a,b,c instead, feel free to do it
  implicit val OfferMapper: BaseColumnType[OfferType] = MappedColumnType.base[OfferType, String](
    e => e.toString,
    s => OfferType.withName(s)
  )
  /*****   WRITE & READ ENUM VALUES  *****/
  implicit val readsMyEnum = Reads.enumNameReads(OfferType)
  implicit val writesMyEnum = Writes.enumNameWrites

  /*****   ADD OFFERTYPE TO ROUTES SO ROUTES CAN DEFINE OFFERTYPE  AND SEARCH BY TYPE   *****/
  implicit val queryStringBindable: QueryStringBindable[OfferType] =
    new QueryStringBindable[OfferType] {
      override def bind(
                         key: String,
                         params: Map[String, Seq[String]]
                       ): Option[Either[String, OfferType]] =
        params.get(key).collect {
          case Seq(s) =>
            OfferType.values.find(_.toString == s).toRight("invalid value")
        }
      override def unbind(key: String, value: OfferType): String =
        implicitly[QueryStringBindable[String]].unbind(key, value.toString)
    }

}

