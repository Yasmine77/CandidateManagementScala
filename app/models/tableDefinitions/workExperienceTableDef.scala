package models.tableDefinitions
import models.workExperienceId.workExperienceId
import models.WorkExperience
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class workExperienceTableDef (tag: Tag) extends BaseTable[WorkExperience](tag,"WorkExperience") with TableId[workExperienceId]{
  override def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
  def date = column[String]("date")
  def description = column[String]("description")
  override def * = (id,date,description) <> ((WorkExperience.apply _).tupled,WorkExperience.unapply)

}
