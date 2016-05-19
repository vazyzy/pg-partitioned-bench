package vazyzy.pgbench

import scala.collection.mutable

trait Metrics {

  val metricsStorage: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer[Int]()

  def addMetric(value: Int): Unit = {
    metricsStorage += value
  }

  def getMax: Int = {
    metricsStorage.max
  }

  def getMin: Int = {
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
