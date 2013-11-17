package com.http.server

import java.nio.channels.ServerSocketChannel
import java.net.InetSocketAddress
import java.nio.ByteBuffer

/**
 * www.cordinc.com/blog/2010/08/java-nio-server-example.html
 * www.java2s.com/Tutorial/Java/0320__Network/UsingaSelectortoManageNonBlockingServerSockets.htm
 * tutorials.jenkov.com/java-nio/server-socket-channel.html
 */
class ServerNio(port: Int) {

    val serverSocketChannel = ServerSocketChannel.open()

    serverSocketChannel.socket().bind(new InetSocketAddress(port))
    serverSocketChannel.configureBlocking(false)

    def start() {
        while(true) { // TODO vire ca !!!
            val clientChannel = serverSocketChannel.accept()

            if(clientChannel != null) { // non-blocking mode client channel could be null at one time
                println("toto")
                val response = s"Hello"//${clientChannel.socket().getInetAddress} on port ${clientChannel.socket().getPort} \r\n"

                val data = response.getBytes("UTF-8")
                val buffer = ByteBuffer.wrap(data)
                buffer.rewind()

                while(buffer.hasRemaining) clientChannel.write(buffer)

                clientChannel.close()
            }
        }
    }

    def stop() {
        serverSocketChannel.close()
    }
}