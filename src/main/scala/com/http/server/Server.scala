package com.http.server

import java.net.ServerSocket
import akka.actor.ActorSystem
import com.http.server.concurrent.SocketHandler._
import akka.pattern.gracefulStop
import scala.concurrent.Await
import scala.concurrent.duration._
import com.http.server.concurrent.SocketClient
import akka.routing.RoundRobinRouter

class Server(port: Int) {
    private val serverSocket = new ServerSocket(port)
    private val socketHandler = ActorSystem("server").actorOf(props.withRouter(RoundRobinRouter(nrOfInstances = 2)), name)

    def start() {
        while(true) {
            val socket = serverSocket.accept()
            socketHandler ! SocketClient(socket)
        }
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

    def stop() {
        Await.result(gracefulStop(socketHandler, 1 seconds), 16 seconds)
        serverSocket.close()
    }
}

object Server {
    def main(args: Array[String]) { new Server(8080).start() }
}