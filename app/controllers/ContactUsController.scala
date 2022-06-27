package controllers
import models.User
import play.api.libs.mailer.{Email, _}
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.data.Forms.{email, mapping, nonEmptyText}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{ApplicationService, MailerService, OfferService, ProfileService, ResumeService}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.apache.commons.mail.HtmlEmail;


class ContactUsController  @Inject()(val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents,mailerClient: MailerClient, environment: Environment,mailerService: MailerService)
                                    (implicit val conf:Configuration = Configuration.reference) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{

  case class createContactForm(subject:String,firstName:String,lastName:String,senderEmail: String,message:String)
  private def ContactForm[createContactForm] = Form {
    mapping(
      "subject" ->nonEmptyText,
      "firstName" ->nonEmptyText,
      "lastName"->nonEmptyText,
      "senderEmail"->nonEmptyText,
      "message"->nonEmptyText,
    )(createContactForm.apply)(createContactForm.unapply)
  }

 def sendEmail = Action.async { implicit request =>
   ContactForm.bindFromRequest.fold(
     e => {
       Future(BadRequest(e.errors.toString))
     },
     valid => {

       val email = Email("ContactUsEmail", "Robot <" + valid.senderEmail + ">", Seq("TO <mejrijasmine77@gmail.com>"), bodyText = Some(valid.message),
         bodyHtml = Some(
           s"""<html>
              |<body>
              |<h3>This Email is From the user:</h3> ${valid.senderEmail}
              | <h3><b>FullName</b>:</h3>${valid.firstName} ${ valid.lastName }
              |<p><h3><b> Message:</b></h3>${valid.message}</p>
              |</body>
              |</html>
              |""".stripMargin)
      )

       //mohamed.bechir.said@imbus.tn
       mailerClient.send(email)
       Future(Ok("Message sent"))

     }

   )
 }









}
