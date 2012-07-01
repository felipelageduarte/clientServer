/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Log.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected Socket clientSocket;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ArrayList<ServerThread> serverThread = null;

    public Server(int port) {
        this.serverPort = port;
        this.serverThread = new ArrayList<ServerThread>();
    }

    public synchronized void stop() {
        Log.info("Stoping main server thread...");
        this.isStopped = true;
        try {
            this.serverSocket.close();
            Log.info("Stoping connection threads...");
            ServerThread stAux = null;
            for (int i = 0; i < serverThread.size(); ++i) {
                stAux = serverThread.get(i);
                if (stAux != null) {
                    stAux.stop();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    @Override
    public void run() {
        Log.info("Server Up and Running...");

        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        //try to open server socket
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException ex) {
            Log.fatal("Could not create the server: " + ex.getMessage());
        }

        while (!isStopped()) {
            clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                Log.debug("New Client - " + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
            } catch (IOException e) {
                if (isStopped()) {
                    Log.warn("Server Stopped.");
                    return;
                }
                Log.error("Error accepting client connection" + e.getMessage());
            }

            ServerThread sT = null;
            try {
                Log.debug("Starting ServerThread to handle new Client...");
                sT = new ServerThread(clientSocket, serverThread.size());
                sT.run();
                serverThread.add(sT);
            } catch (IOException ex) {
                Log.fatal("Server Thread could not be initialized - " + ex.getMessage());
                if (sT != null) {
                    sT.stop();
                }
            }

            //run through Thread vector to remove some thread that already shut down
            Log.debug("Serching for some dead Thread");
            for (int i = 0; i < serverThread.size(); ++i) {
                if (serverThread.get(i) == null) {
                    Log.debug("Remove dead Thread from threads vector");
                    serverThread.remove(i);
                }
            }
        }
        Log.info("Server Stopped.");
    }
}
