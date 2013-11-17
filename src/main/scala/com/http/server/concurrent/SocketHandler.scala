package com.http.server.concurrent

import akka.actor.{Props, ActorLogging, Actor}
import java.net.Socket
import java.io.{OutputStreamWriter, PrintWriter, InputStreamReader, BufferedReader}

object SocketHandler {
    val props = Props[SocketHandler]
    val name = "socketHandler"
}

class SocketHandler extends Actor with ActorLogging {
    def receive = {
        case SocketClient(socket) => {
            println(s"Received message in actor ${self.path.name}")
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
    }
}

case class SocketClient(socket: Socket)