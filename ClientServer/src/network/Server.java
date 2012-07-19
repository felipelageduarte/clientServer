/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Interface.Interface;
import Log.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Server extends Thread {

    protected ServerConfiguration config;
    protected ServerSocket serverSocket = null;
    protected Socket clientSocket;
    protected volatile Boolean stop = false;
    protected Thread runningThread = null;
    protected ArrayList<ServerThread> serverThread = null;

    public Server(ServerConfiguration config) {
        this.config = config;
        this.serverThread = new ArrayList<ServerThread>();
    }

    public synchronized void shutdown() {
        synchronized (stop) {
            Log.info("Stoping main server thread...");
            this.stop = true;
            try {
                this.serverSocket.close();

            } catch (IOException e) {
                throw new RuntimeException("Error closing server", e);
            }
        }
    }

    public synchronized boolean isStopped() {
        synchronized (stop) {
            return this.stop;
        }
    }

    @Override
    public void run() {
        Log.info("Server Up and Running...");

        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        //try to open server socket
        try {
            this.serverSocket = new ServerSocket(this.config.getPort());
            Log.debug("server initiated on address: "
                    + InetAddress.getLocalHost().getHostAddress() + ":"
                    + this.config.getPort());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Servidor não pode ser criado: " + ex.getMessage(), "Unknow Host", JOptionPane.WARNING_MESSAGE);
            Log.fatal("Could not create the server: " + ex.getMessage());
        }

        while (!isStopped() && serverSocket != null) {
            updateClientListInterface();
            clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                Log.debug("New Client - " + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());                
            } catch (IOException e) {
                if (isStopped()) {
                    continue;
                }
                Log.error("Error accepting client connection" + e.getMessage());
            }

            ServerThread sT = null;
            try {
                Log.debug("Starting ServerThread to handle new Client...");
                sT = new ServerThread(clientSocket, serverThread.size(), config);
                sT.start();
                serverThread.add(sT);
            } catch (IOException ex) {
                Log.fatal("Server Thread could not be initialized - " + ex.getMessage());
                if (sT != null) {
                    sT.shutdown();
                }
            }

            //run through Thread vector to remove some thread that already shut down
            for (int i = 0; i < serverThread.size(); ++i) {
                if (serverThread.get(i) == null) {
                    Log.debug("Remove dead Thread from threads vector");
                    serverThread.remove(i);
                }
            }
        }

        Log.info("Stoping connection threads...");
        ServerThread stAux = null;
        for (int i = 0; i < serverThread.size(); ++i) {
            stAux = serverThread.get(i);
            if (stAux != null) {
                stAux.shutdown();
            }
        }

        Interface.getInstance().serverStoped();
        Log.info("Server Stopped.");
    }

    private void updateClientListInterface() {
        String clientList = "";
        Socket socket;
        for (int i = 0; i < serverThread.size(); ++i) {
            if (serverThread.get(i) != null) {
                socket = serverThread.get(i).getClientSocket();
                clientList += (i + 1) + " - " + socket.getInetAddress().toString()
                        + ":" + socket.getPort() + "\n";
            }
        }
        Interface.getInstance().getServerClientListTextArea().setText(clientList);
    }
}
