import java.util.UUID

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

trait Metrics {
  var metricsStorage: mutable.ArrayBuffer[Long] = mutable.ArrayBuffer[Long]()

  def addMetric(value: Long): Unit = {
    metricsStorage += value
  }

  def getMax: Long = {
    metricsStorage.max
  }

  def getMin: Long = {
    metricsStorage.max
  }

  def getAvg: Double = {
    metricsStorage.sum / metricsStorage.length
  }

  def saveToFile(path: String): Unit = {
    import java.io._

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try {
        op(p)
      } finally {
        p.close()
      }
    }

    printToFile(new File(path)) { p =>
      metricsStorage.foreach(p.println)
    }
  }

}
