package vazyzy.pgbench

import java.io.{File, PrintWriter}

import com.typesafe.config.ConfigFactory
import vazyzy.pgbench.Benchmark.Conf
import vazyzy.pgbench.Utils.ExtendedFuture
import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

object Application extends App {

  val conf = ConfigFactory.load()
  val db = Database.forConfig("pgConf")

  val bench1Conf = Conf(
    velocity = 100,
    requestCount = 1000,
    numPartitions = 10
  )

  val bench = new PartitionBench(db)
  val bench2 = new PartitionBench(db)
  bench.runInserts(bench1Conf).awaitInf
  bench2.runInserts(bench1Conf).awaitInf

  val pw = new PrintWriter(new File("report.html" ))
  pw.write(vazyzy.pgbench.html.report(bench, bench2).toString())
  pw.close()
  db.close()


}



