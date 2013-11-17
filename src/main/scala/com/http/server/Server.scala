package com.http.server

import java.net.ServerSocket
import java.io.{OutputStreamWriter, PrintWriter, InputStreamReader, BufferedReader}
import java.nio.charset.Charset

/**
 * http://queinnec.perso.enseeiht.fr/Ens/Chat/socket-java.html
 * http://defaut.developpez.com/tutoriel/java/serveur/multithread/
 * http://b.kostrzewa.free.fr/java/td-serveur/serveur.html
 * https://gist.github.com/fsarradin/6449549
 */
class Server(port: Int) {

    val serverSocket = new ServerSocket(port)

    def start() {
        while(true) {
            val socket = serverSocket.accept()

            Thread.sleep(100)
            // TODO faut-il fermer les Stream anonymes ???
            val fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream))
            val toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream), true)   // auto flush

            val header = "HTTP/1.1 200 OK\n" + "Content-Type: text/plain\n\n"
            toClient.println(s"$header YO"/*.getBytes(Charset.forName("UTF-8"))*/)

            fromClient.close()
            toClient.close()
            socket.close()
        }
    }

    def stop() { serverSocket.close() }
}

object Server {
    def main(args: Array[String]) { new Server(8080).start() }
}