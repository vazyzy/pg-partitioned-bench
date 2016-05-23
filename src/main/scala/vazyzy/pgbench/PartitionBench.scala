package vazyzy.pgbench

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import slick.driver.PostgresDriver.api._

class PartitionBench(db: Database) extends Benchmark(db) {
  
  override def prepare(): Unit = {
    val request = for {
      _ <- db.run(PartitionedTable.createTable)
      _ <- db.run(PartitionedTable.createIsertFunction)
      _ <- db.run(PartitionedTable.createTrigger)
    } yield {}

    Await.ready(request, Duration.Inf)
  }

  override def insert(partition: Int, attr: String): Future[Unit] = {
    db.run(PartitionedTable.insert(partition, attr))
      .map(_ => Unit)
  }
}
