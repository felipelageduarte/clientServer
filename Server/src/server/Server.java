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

    protected int serverPort = 8080;
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
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort);
    }

    private synchronized boolean isStopped() {
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
            openServerSocket();
        } catch (IOException ex) {
            Log.error("Error closing server: " + ex.getMessage());
        }
        
        while (!isStopped()) {
            clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    Log.warn("Server Stopped.");
                    return;
                }
                Log.error("Error accepting client connection" + e.getMessage());
            }
            
            ServerThread sT = new ServerThread(clientSocket, serverThread.size());
            sT.run();
            serverThread.add(sT);
        }
        Log.info("Server Stopped.");
    }
}
