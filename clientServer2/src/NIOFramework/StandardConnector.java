/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NIOFramework;

import Log.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author felipelageduarte
 */
public class StandardConnector {

    private SocketChannel serverSocket;
    private Dispatcher dispatcher;
    private InputHandlerFactory inputHandlerFactory;

    public StandardConnector(Dispatcher dispatcher, InputHandlerFactory inputHandlerFactory) throws IOException {
        this.dispatcher = dispatcher;
        this.inputHandlerFactory = inputHandlerFactory;
        this.serverSocket = SocketChannel.open();
        this.serverSocket.configureBlocking(false);
    }

    public void connect(String address, int port) throws IOException {
        InetAddress addr = InetAddress.getByName(address);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(addr, port);
        serverSocket.connect(inetSocketAddress);
        try {
            dispatcher.registerClientChannel(serverSocket, inputHandlerFactory.newHandler());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StandardConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StandardConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.error("Caught an exception shutting down", e);
        }
    }
}
