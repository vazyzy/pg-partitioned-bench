package vazyzy.pgbench

import java.util.UUID

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._
import vazyzy.pgbench.Benchmark.Conf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

trait Benchmark extends Metrics {

  val conf = ConfigFactory.load()
  val db = Database.forConfig("pgConf")

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
   * Start benchmark asynchronyosly
   * @param conf
   * @return
   */
  def run(conf: Conf): Future[Unit] = {
    prepare()
    val requests = 0 to conf.requestCount map (i => {
      val partition = Random.nextInt(conf.numPartitions)
      val attr = UUID.randomUUID().toString

      Future(Thread.sleep(1000/conf.velocity * i))
        .flatMap(_ => {
          val start = System.currentTimeMillis()
          db.run(PartitionedTable.insert(partition, attr))
            .map(_ => addMetric((System.currentTimeMillis() - start).toInt))
        })
    })

    Future
      .sequence(requests)
      .mapTo[Unit]
  }
}
object Benchmark {

  case class Conf(velocity: Long,
                  requestCount: Int,
                  numPartitions: Int,
                  concurrency: Long)

}