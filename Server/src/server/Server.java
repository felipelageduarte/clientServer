/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Log.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;

    public Server(int port) {
        this.serverPort = port;
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

    @Override
    public void run() {
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
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(
                    new WorkerRunnable(
                    clientSocket, "Multithreaded Server")).start();
        }
        System.out.println("Server Stopped.");
    }

    public static void main(String[] args) {
        Log.debug("!!!debug!!!");
        Log.error("!!!error!!!");
        Log.fatal("!!!fatal!!!");
        Log.info("!!!info!!!");
        Log.warn("!!!warn!!!");
        Log.warn("!!!warn2!!!");
    }
}
