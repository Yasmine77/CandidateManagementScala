package models.tableDefinitions
  import models.Resume
  import models.ResumesId.ResumeId
  import slick.lifted.{TableQuery, Tag}
  import slick.jdbc.MySQLProfile.api._

class ResumeTableDef (tag: Tag) extends BaseTable[Resume](tag,"resumes") with TableId[ResumeId]{

    override def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
    def userRef = column[Long]("userRef")
    def academicBackground = column[String]("academicBackground")

  def acadDate = column[String]("acadDate")

  def workDate = column[String]("workDate")

  def skills = column[String]("skills")
    def workExperience = column[String]("workExperience")

  override def * = (id,userRef,academicBackground,acadDate,skills.?,workExperience.?,workDate.?) <> ((Resume.apply _).tupled,Resume.unapply)

  }


