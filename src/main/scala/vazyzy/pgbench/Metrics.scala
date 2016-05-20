package vazyzy.pgbench

import scala.collection.mutable

trait Metrics {

  val metricsStorage: mutable.ArrayBuffer[Long] = mutable.ArrayBuffer[Long]()

  def addMetric(value: Long): Unit = {
    metricsStorage += value
  }

  def getMax: Long = {
    metricsStorage.max
  }

  def getMin: Long = {
    metricsStorage.min
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
