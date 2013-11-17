package com.http.server

import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}
import scala.io.Source
import com.github.simplyscala.http.client.AsyncHttpClient
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.matchers.ShouldMatchers


class ServerTest extends FunSuite with Matchers with BeforeAndAfterAll {
    //val server = new ServerNio(8080)
    val server = new Server(8080)

    override def beforeAll {
        Future { server.start() }
    }

    override def afterAll {
        server.stop()
    }

    ignore("ping server NIO") {
        val response = Source.fromURL("http://localhost:8080").mkString
        println(s"Response : $response")
    }

    test("ping server") {
        val response = Source.fromURL("http://localhost:8080").mkString
        response should include ("YO")
    }

    test("ping server with 2 client") {
        val start = System.currentTimeMillis()

        Source.fromURL("http://localhost:8080").mkString
        Source.fromURL("http://localhost:8080").mkString

        val stop = System.currentTimeMillis()

        (stop - start) should be < 200L
    }
}