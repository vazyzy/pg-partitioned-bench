package vazyzy.pgbench

import java.util.concurrent.{TimeUnit, Executors}

import scala.concurrent.duration.Duration
import scala.concurrent.{Awaitable, Await, Promise, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object Utils {

  implicit class ExtendedFuture[A](future: Future[A]) {

    /**
     * Await inf duration.
     */
    def awaitInf: A = Await.result(future, Duration.Inf)

    /**
     * Map to empty Future.
     */
    def toUnit: Future[Unit] = future.map(_ => {})
  }

  /**
   * Run async operation after timeout, without blocking threads.
   */
  def schedule[T](timeout: Long,
               timeUnit: TimeUnit = TimeUnit.NANOSECONDS,
               pool: Int = 10)(f: => Future[T]): Future[T] = {
    if (timeout <= 0)
      return f

    val scheduler =  Executors.newScheduledThreadPool(pool)
    val promise = Promise[T]()
    scheduler.schedule(new Runnable {
      override def run(): Unit = {
        f.onComplete({
          case s: Success[T] => promise.complete(s)
          case Failure(t) => promise.failure(t)
        })
      }
    }, timeout, timeUnit)
    promise.future
  }
}
