package scala.slick.driver

import scala.concurrent.{ExecutionContext, Future}
import scala.slick.action.Action
import scala.slick.jdbc.JdbcModelBuilder
import scala.slick.jdbc.meta.MTable
import scala.slick.model.Model

trait JdbcModelComponent { driver: JdbcDriver =>
  /** Jdbc meta data for all tables included in the Slick model by default */
  def defaultTables(implicit ec: ExecutionContext): Action[Seq[MTable]] = MTable.getTables

  /** Gets the Slick data model describing this data source
    * @param tables used to build the model, uses defaultTables if None given
    * @param ignoreInvalidDefaults logs unrecognized default values instead of throwing an exception */
  def createModel(tables: Option[Action[Seq[MTable]]] = None, ignoreInvalidDefaults: Boolean = true)(implicit ec: ExecutionContext): Action[Model] = {
    val tablesA = tables.getOrElse(defaultTables)
    tablesA.flatMap(t => createModelBuilder(t, ignoreInvalidDefaults).buildModel)
  }

  def createModelBuilder(tables: Seq[MTable], ignoreInvalidDefaults: Boolean)(implicit ec: ExecutionContext): JdbcModelBuilder =
    new JdbcModelBuilder(tables, ignoreInvalidDefaults)
}
