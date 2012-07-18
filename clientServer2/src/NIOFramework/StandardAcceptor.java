package NIOFramework;

import Log.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA. 
 * User: ron 
 * Date: Mar 17, 2007 
 * Time: 6:07:38 PM
 * 
 * Modified by Felipe Duarte. 
 * email: felipelageduarte at gmail dot com
 * Date: Jul 12, 2012 
 */
public class StandardAcceptor {

    private final Dispatcher dispatcher;
    private final InputHandlerFactory inputHandlerFactory;
    private final ServerSocketChannel listenSocket;
    private final Listener listener;
    private final List<Thread> threads = new ArrayList<Thread>();
    private volatile boolean running = true;

    public StandardAcceptor(ServerSocketChannel listenSocket, Dispatcher dispatcher, InputHandlerFactory inputHandlerFactory) {
        this.listenSocket = listenSocket;
        this.dispatcher = dispatcher;
        this.inputHandlerFactory = inputHandlerFactory;

        listener = new Listener();
    }

    public StandardAcceptor(SocketAddress socketAddress, Dispatcher dispatcher, InputHandlerFactory inputHandlerFactory) throws IOException {
        this(ServerSocketChannel.open(), dispatcher, inputHandlerFactory);

        listenSocket.socket().bind(socketAddress);
    }

    public StandardAcceptor(int port, Dispatcher dispatcher, InputHandlerFactory inputHandlerFactory) throws IOException {
        this(new InetSocketAddress(port), dispatcher, inputHandlerFactory);
    }

    private class Listener implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    SocketChannel client = listenSocket.accept();

                    if (client == null) {
                        continue;
                    }

                    dispatcher.registerChannel(client, inputHandlerFactory.newHandler());

                } catch (ClosedByInterruptException e) {
                    Log.debug("ServerSocketChannel closed by interrupt: ", e);
                    return;

                } catch (ClosedChannelException e) {
                    Log.debug("Exiting, serverSocketChannel is closed: ", e);
                    return;

                } catch (Throwable t) {
                    Log.debug("Exiting, Unexpected Throwable doing accept: " + t.getMessage());

                    try {
                        listenSocket.close();
                    } catch (Throwable e1) {}
                    
                    return;
                }
            }
        }
    }

    public synchronized Thread newThread() {
        Thread thread = new Thread(listener);

        threads.add(thread);

        thread.start();

        return thread;
    }

    public synchronized void shutdown() {
        running = false;

        for (Iterator it = threads.iterator(); it.hasNext();) {
            Thread thread = (Thread) it.next();

            if ((thread != null) && (thread.isAlive())) {
                thread.interrupt();
            }
        }

        for (Iterator it = threads.iterator(); it.hasNext();) {
            Thread thread = (Thread) it.next();

            try {
                thread.join();
            } catch (InterruptedException e) {
                // nothing
            }

            it.remove();
        }

        try {
            listenSocket.close();
        } catch (IOException e) {
            Log.error( "Caught an exception shutting down", e);
        }
    }
}
