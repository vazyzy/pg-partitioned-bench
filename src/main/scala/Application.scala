import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

object Application extends App {
  val conf = ConfigFactory.load()
  val db = Database.forConfig("pgConf")

  Await.result(db.run(PartitionedTable.createTable), Duration.Inf)
  Await.result(db.run(PartitionedTable.createIsertFunction), Duration.Inf)
  Await.result(db.run(PartitionedTable.createTrigger), Duration.Inf)




  val await = Future.sequence(
    0 to count map (i => {

      val partition = Random.nextInt(partitionsCount)
      Future(Thread.sleep(Random.nextInt(kuchnost)))
        .flatMap(_ => {
        val start = System.currentTimeMillis()
        val await = db.run(PartitionedTable.insert(partition, attr))
        await.onSuccess({
          case a =>
            bench.addMetric(System.currentTimeMillis() - start)
        })
        await
      })
    })
  )

  await.onFailure({ case e => println(e) })
  Await.ready(await, Duration.Inf)

  println(bench.getAvg)
  println(bench.getMax)
  println(bench.getMin)
  println(conf.getInt("pgConf.numThreads"))

  bench.saveToFile("/home/vazyzy/tmp_output_1")
}

class BenchMark() {
}
