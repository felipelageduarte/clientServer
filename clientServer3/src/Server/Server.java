/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Log.Log;
import ch.unifr.nio.framework.Dispatcher;
import ch.unifr.nio.framework.FrameworkTools;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Server {
   
    private NIOServer server;
    private Dispatcher dispatcher;

    public Server() {
    }

    public boolean start(Integer integer) {
        try {
            // start NIO Framework
            dispatcher = new Dispatcher();
            dispatcher.start();

            // start EchoServer
            int port = 8080;// Integer.parseInt(args[0]);
            SocketAddress socketAddress = new InetSocketAddress(port);
            server = new NIOServer(dispatcher, socketAddress);
            server.start();
            Log.info("EchoServer is running at port " + port + "...");

        } catch (Exception ex) {
            Log.fatal("Could not initiate server",ex);
            return false;
        }
        return true;
    }

    public void stop() {
        dispatcher.interrupt();
        server.interrupt();
    }

    public void send(Object obj) {
    }
}
