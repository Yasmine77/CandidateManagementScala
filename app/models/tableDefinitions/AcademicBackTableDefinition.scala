package models.tableDefinitions
import models.{AcademicBack, academicBackId}
import models.academicBackId.academicBackId
import slick.lifted.{TableQuery, Tag}
import slick.jdbc.MySQLProfile.api._
class AcademicBackTableDefinition(tag: Tag) extends BaseTable[AcademicBack](tag,"AcademicBack") with TableId[academicBackId]{
  override def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
  def date = column[String]("date")
  def description = column[String]("description")
  def userRef=column[Long] ("userRef")
  def resumeRef=column[Long] ("resumeRef")

  def  userFk =  foreignKey("USER_FK", userRef,  TableQuery[UserTableDef]) (_.id)  // , ForeignKeyAction.NoAction,ForeignKeyAction.Cascade
  def  resumeFk =  foreignKey("RESUME_FK", resumeRef,  TableQuery[ResumeTableDef]) (_.id)  // , ForeignKeyAction.NoAction,ForeignKeyAction.Cascade

  override def * = (id,date,description,userRef,resumeRef) <> ((AcademicBack.apply _).tupled,AcademicBack.unapply)

}
