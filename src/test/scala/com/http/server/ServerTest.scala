package com.http.server

import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}
import scala.io.Source
import java.io.FileNotFoundException

class ServerTest extends FunSuite with Matchers with BeforeAndAfterAll {
    val server = HttpServer.addContext(8080, "/") { request =>
        implicit def StringToBody(value: String) = Body(value)

        request.path match {
            case "/" => Response("YO", 200)
            case "/otherPath" => Response("TITI", 200)
            case _ => Response("error", 404)
        }
    }

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

    test("test API") {
        Source.fromURL("http://localhost:8080").mkString should include ("YO")
        Source.fromURL("http://localhost:8080/otherPath").mkString should include ("TITI")
        the [FileNotFoundException] thrownBy Source.fromURL("http://localhost:8080/badPath").mkString should have message "http://localhost:8080/badPath"
    }
}