package models.tableDefinitions

import models.BaseEntity
import slick.jdbc.MySQLProfile.api._



abstract class BaseTable  [E <: BaseEntity[_]  ](tag: Tag, tableName: String) extends Table[E](tag, tableName)  {
}

trait  TableId [P] {
  def id: Rep[P]
}
