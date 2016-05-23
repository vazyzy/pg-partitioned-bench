package vazyzy.pgbench
import scala.collection.convert.decorateAsScala._
import java.util.concurrent.ConcurrentHashMap



trait Metrics {

  val metricsStorage = new ConcurrentHashMap[Long, Long]().asScala

  def getSeries(): Seq[(Long, Long)] ={
    val sortedSeries = metricsStorage.toSeq.sortBy(_._1)
    sortedSeries.map({case (x, y) => (x - sortedSeries.head._1, y)})
  }

  def addMetric(key: Long, value: Long): Unit = {
    metricsStorage.put(key, value)
  }

  def getMax: Long = {
    metricsStorage.values.max
  }

  def getMin: Long = {
    metricsStorage.values.min
  }

  def getAvg: Double = {
    metricsStorage.values.sum / metricsStorage.size
  }

}
