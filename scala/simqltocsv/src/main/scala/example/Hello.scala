package example

import me.kerfume.simql.Module
import java.sql.ResultSet
import org.apache.spark.SparkConf

object Main {
  def main(args: Array[String]): Unit = {
    (new Application(ImpureSpark)).exec()
  }
}

class Application(m: Impure) {
  import m._

  def exec(): Unit = {
    val pd = loadPredef()
    val ud = loadUserdef()
    while(true) {
      val query = scala.io.StdIn.readLine()
      val sql = Module.simqlToMysql(query, Some(pd), Some(ud))
      sql match {
        case Right(q) =>
          val rs = submitSQL(sql.right.get)
          printResult(rs)
        case Left(e) =>
          println(s"parse faild: $e")
      }
    }
    close()
  }
}

abstract class Impure {
  type H2Res

  def loadPredef(): String
  def loadUserdef(): String
  def submitSQL(sql: String): H2Res
  def printResult(rs: H2Res): Unit
  def close(): Unit
}

object ImpureSpark extends Impure {
  import org.apache.spark.sql.{SparkSession, DataFrame }
  import org.apache.log4j.{Logger, Level}

  type H2Res = Option[DataFrame]
  val spark = SparkSession.builder.master("local[2]").getOrCreate()

  def loadPredef(): String = "define {}"
  def loadUserdef(): String =
    """
    | define {
    |   defun csv(file: String) => Raw {
    |      let fs = $st2sy($file)
    |      q{ $`csv.`?` `($fs) }
    |   }
    |}
    """.stripMargin
  def submitSQL(sql: String): H2Res = {
    scala.util.Try { spark.sql(sql) }.toOption
  }
  def printResult(rs: H2Res): Unit = {
    rs match {
      case Some(ds) => ds.show()
      case None => println("invalid SQL.")
    }
  }
  def close(): Unit = {
    spark.stop()
  }
}
