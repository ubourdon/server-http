package com.http.server

import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}
import scala.io.Source
import scala.concurrent.duration._

class ServerTest extends FunSuite with Matchers with BeforeAndAfterAll with DurationTestTools {
    val server = new Server(8080)

    override def beforeAll {
        import scala.concurrent.ExecutionContext.Implicits.global
        import scala.concurrent.Future

        Future { server.start() }
    }

    override def afterAll { server.stop() }

    test("ping server") {
        val response = Source.fromURL("http://localhost:8080").mkString
        response should include ("YO")
    }

    test("ping server with 2 client") {
        val (time, results) = mesureTimeExecutionOf {
            (1 to 5).par.map { x => Source.fromURL("http://localhost:8080").mkString }
        }

        println(s"time elapsed for ${results.size} requests = $time")

        time shouldBeMinusThan (500 milliseconds)
    }
}

trait DurationTestTools {
    implicit def duration2DurationCompare(duration: Duration): DurationCompare = DurationCompare(duration)

    case class DurationCompare(duration: Duration) {
        def shouldBeMinusThan(expectedDuration: Duration) { assert(duration.compare(expectedDuration) < 0, s"$duration was not minus than $expectedDuration") }
        def shouldBeMinusOrEqualThan(expectedDuration: Duration) { assert(duration.compare(expectedDuration) <= 0, s"$duration was not minus or equal than $expectedDuration") }
        def shouldBeGreaterThan(expectedDuration: Duration) { assert(duration.compare(expectedDuration) > 0, s"$duration was not greater than $expectedDuration") }
        def shouldBeGreaterOrEqualThan(expectedDuration: Duration) { assert(duration.compare(expectedDuration) >= 0, s"$duration was not greater or equal than $expectedDuration") }
        def shouldBeEqualTo(expectedDuration: Duration) { assert(duration.compare(expectedDuration) == 0, s"$duration was not equal to $expectedDuration") }
    }

    def mesureTimeExecutionOf[T](toMesure: => T): (Duration, T) = {
        val start = System.currentTimeMillis()
        val result = toMesure
        val stop = System.currentTimeMillis()

        ((stop - start) milliseconds, result)
    }
}