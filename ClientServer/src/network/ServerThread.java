/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends ArchitectureThread {

    private Socket clientSocket;
    private int index;
    private boolean stop;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    ServerThread(Socket clientSocket, int index) throws IOException {
        super();
        this.clientSocket = clientSocket;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void shutdown() {
        stop = true;
        try {
            if (inThread != null) {
                inThread.shutdown();
                while (!inThread.isStopped()) {
                    sleep(100);
                }
            }
            if (outThread != null) {
                outThread.shutdown();
                while (!outThread.isStopped()) {
                    sleep(100);
                }
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            Log.error("Problem stoping ServerThread - " + ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Log.info("New ServerThread running...");
        Communication communication = null;

        // I/O Threads
        try {
            inThread = new InThread(this, new ObjectInputStream(clientSocket.getInputStream()));
            outThread = new OutThread(new ObjectOutputStream(clientSocket.getOutputStream()));
            inThread.start();
            outThread.start();
        } catch (IOException ex) {
            Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
            stop = true;
        }

        while (!stop) {
            try {
                //busy wait for incomming request from client
                while ((communication = requestQueue.take()) == null && !stop) {
                    ServerThread.sleep(100);
                }
                if (!stop) {
                    //process incomming request
                    switch (communication.getReason()) {
                        case Exit:
                            Log.debug("Incomming Exit request");
                            break;
                        case ChallengeNumber:
                            Log.debug("Incomming Challenge Number");
                            break;
                        case ChallengeAnswer:
                            Log.debug("Incomming Challenge Answer");
                            break;
                        case Password:
                            Log.debug("Incomming Password");
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.shutdown();
        Log.info("ServerThread shuting down...");
    }
}
