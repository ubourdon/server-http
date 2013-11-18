package com.http.server.concurrent

import akka.actor.{Props, ActorLogging, Actor}
import java.net.Socket
import java.io.{OutputStreamWriter, PrintWriter, InputStreamReader, BufferedReader}
import com.http.server.Server.Context
import com.http.server.Request

object SocketHandler {
    def props(context: List[Context]) = Props.apply(new SocketHandler(context))
    val name = "socketHandler"
}

class SocketHandler(contexts: List[Context]) extends Actor with ActorLogging {
    def receive = {
        case SocketClient(socket) => {
            val (_, action) = contexts.head

            println(s"Received message in actor ${self.path.name}")
            Thread.sleep(100)

            // TODO faut-il fermer les Stream anonymes ???
            //val fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream))
            val fromClient = io.Source.fromInputStream(socket.getInputStream)
            val toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream), true)   // auto flush

            def parseClient(lines: Stream[String], acc: List[String] = Nil): List[String] = {
                val line = lines.head
                if(line.isEmpty) acc    // \r\n
                else parseClient(lines.tail, acc :+ line)
            }

            val lines = parseClient(fromClient.getLines().toStream)

            val verb = lines.head.split(" ")(0)
            val path = lines.head.split(" ")(1)

            val response = action(Request(path))

            // TODO parser lines pour construire Request
            //lines.find( _ matches "GET" )

            // TODO appliquer action pour recupérer Response
            //println(lines.mkString("\n"))

            val header = s"HTTP/1.1 ${response.code} ${codeToResponse(response.code)}\n" +
                "Content-Type: text/plain\n\n"

            toClient.println(s"$header ${response.body.value}")

            fromClient.close()
            toClient.close()
            socket.close()
        }
    }

    private def codeToResponse(code: Int): String = code match {
        case 200 => "OK"
        case 404 => "Not Found"
    }

}

case class SocketClient(socket: Socket)