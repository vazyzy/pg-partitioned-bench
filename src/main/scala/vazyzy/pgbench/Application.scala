package vazyzy.pgbench

import java.io.{File, PrintWriter}

import com.typesafe.config.ConfigFactory
import vazyzy.pgbench.Benchmark.Conf

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Application extends App {
  val conf = ConfigFactory.load()

  val bench1Conf = Conf(
    velocity = 10,
    requestCount = 10,
    numPartitions = 100,
    concurrency = 10
  )

  val bench = new PartitionBench
  val bench2 = new PartitionBench
  Await.ready(bench.run(bench1Conf), Duration.Inf)
  Await.ready(bench2.run(bench1Conf), Duration.Inf)

  val pw = new PrintWriter(new File("report.html" ))
  pw.write(vazyzy.pgbench.html.report(bench, bench2).toString())
  pw.close()
}



