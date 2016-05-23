package vazyzy.pgbench

import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import slick.driver.PostgresDriver.api._

class SimpleBench(db: Database) extends Benchmark(db) {
  
  override def prepare(): Unit = {
    val request = db.run(
      sqlu"""
          CREATE TABLE IF NOT EXISTS simple_table(
            partitionKey varchar(100) NOT NULL,
            id SERIAL PRIMARY KEY,
            attr text
          );
    """).map(_ => {})
    Await.ready(request, Duration.Inf)
  }

  override def insert(partition: Int, attr: String): Future[Unit] = {
    db.run(sqlu"""
          INSERT INTO simple_table(partitionKey, attr) VALUES($partition, $attr)
        """)
      .map(_ => Unit)
  }
}
