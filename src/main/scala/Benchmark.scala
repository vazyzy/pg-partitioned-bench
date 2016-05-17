import java.util.UUID

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

trait Benchmark extends Metrics {
  val conf = ConfigFactory.load()
  val db = Database.forConfig("pgConf")

  def prepare(): Future[Unit]

  def insert(partition: Int, attr: String): Future[Unit]

  def run(velocity: Long,
          requestCount: Int,
          numPartitions: Int,
          concurrency: Long): Future[Unit] = {

    Await.ready(prepare(), Duration.Inf)

    val requests = 0 to requestCount map (i => {
      val partition = Random.nextInt(numPartitions)
      val attr = UUID.randomUUID().toString

      Future(Thread.sleep(60000/velocity))
        .flatMap(_ => {
          val start = System.currentTimeMillis()
          db.run(PartitionedTable.insert(partition, attr))
            .map(_ => addMetric(System.currentTimeMillis() - start))
        })
    })

    Future
      .sequence(requests)
      .mapTo[Unit]
  }
}
