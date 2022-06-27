package mapping


import models.enums.{OfferType, Roles}
import play.api.data.{FormError, Forms, Mapping}
import play.api.data.format.Formatter

object OfferMapping {

  implicit val appTypeFormatter = new Formatter[OfferType.OfferType] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], OfferType.OfferType] = {
      data.get(key).map { value =>
        try {
          Right(OfferType.withName(value))
        } catch {
          case e: NoSuchElementException => error(key, value + " is not a valid Offer type")
        }
      }.getOrElse(error(key, "No Offer type provided."))
    }

    private def error(key: String, msg: String) = Left(List(new FormError(key, msg)))

    override def unbind(key: String, value: OfferType.OfferType): Map[String, String] = {
      Map(key -> value.toString())
    }
  }

  def Offer_Type: Mapping[OfferType.OfferType] = Forms.of[OfferType.OfferType]
}
