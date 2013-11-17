package com.http.server

import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}
import scala.io.Source

class ServerTest extends FunSuite with Matchers with BeforeAndAfterAll {
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
        val start = System.currentTimeMillis()

        val results = (1 to 5).par.map { x => Source.fromURL("http://localhost:8080").mkString }

        val stop = System.currentTimeMillis()

        println(s"time elapsed for ${results.size} requests = ${stop - start}")
        (stop - start) should be < 500L
    }
}