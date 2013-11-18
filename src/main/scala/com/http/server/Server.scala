package com.http.server

import java.net.ServerSocket
import akka.actor.ActorSystem
import com.http.server.concurrent.SocketHandler._
import akka.pattern.gracefulStop
import scala.concurrent.Await
import scala.concurrent.duration._
import com.http.server.concurrent.SocketClient
import akka.routing.RoundRobinRouter
import com.http.server.Server.{Path, Context, Code}

object HttpServer {
    def addContext(port: Int, path: Path)(action: Request => Response): Server = {
        new Server(port, List((path, action)))
    }
}

class Server(port: Int, contexts: List[Context]) {
    private val serverSocket = new ServerSocket(port)
    private val socketHandler = ActorSystem("server").actorOf(props(contexts).withRouter(RoundRobinRouter(nrOfInstances = 2)), name)


    def start() {
        while(true) {
            val socket = serverSocket.accept()
            socketHandler ! SocketClient(socket)
        }
    }

    def stop() {
        Await.result(gracefulStop(socketHandler, 1 seconds), 16 seconds)
        serverSocket.close()
    }

    /*def start() {
        while(true) {
            import java.io.{OutputStreamWriter, PrintWriter, InputStreamReader, BufferedReader}
            val socket = serverSocket.accept()

            Thread.sleep(100)

            // TODO faut-il fermer les Stream anonymes ???
            val fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream))
            val toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream), true)   // auto flush

            val header = "HTTP/1.1 200 OK\n" +
                "Content-Type: text/plain\n\n"

            toClient.println(s"$header YO")

            fromClient.close()
            toClient.close()
            socket.close()
        }
    }*/
}

object Server {
    type Context = (Path, Request => Response)
    type Path = String
    type Code = Int

    def main(args: Array[String]) {
        HttpServer.addContext(8080, "/") { request =>
            implicit def StringToBody(value: String) = Body(value)

            request.path match {
                case "/" => Response("YO", 200)
                case "/otherPath" => Response("TITI", 200)
                case _ => Response("error", 404)
            }
        }.start()
    }
}

case class Request(path: Path)
case class Response(body: Body, code: Code)
case class Body(value: String)