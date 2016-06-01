package vazyzy.pgbench

import java.util.UUID

import slick.driver.PostgresDriver.api._
import vazyzy.pgbench.Benchmark.Conf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait Benchmark extends Metrics {

  /**
   * Prepare tables for benchmark.
   */
  def prepare(): Unit

  /**
   * Insert command.
   * @param partition - key in table for partitioning.
   * @param attr - arbitrary attribute.
   */
  def insert(partition: Int, attr: String): Future[Unit]

  /**
   * Read command.
   * @param partition - key in table for partitioning.
   * @param attr - arbitrary attribute.
   */
  def read(partition: Int, attr: String): Future[Unit]

  /**
   * @param conf - benchmark Config.
   */
  def runInserts(conf: Conf): Future[Unit] = {
    prepare()
    val requests = 0 to conf.requestCount map (i => {
      val partition = Random.nextInt(conf.numPartitions)
      val attr = UUID.randomUUID().toString
      val timeout = Math.pow(10, -9) / conf.velocity * i
      Utils.schedule(timeout.toLong) {
        val start = System.nanoTime()
        this.insert(partition, attr)
          .map(_ => addMetric(i, (System.nanoTime() - start) / 1000000))
      }
    })

    Future
      .sequence(requests)
      .map(_ => {})
  }
}


object Benchmark {

  /**
   * @param velocity - requests per second.
   * @param requestCount - number of operations.
   * @param numPartitions - number of table partitions.
   */
  case class Conf(velocity: Long,
                  requestCount: Int,
                  numPartitions: Int)

}