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
    private Boolean stop;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Double challengeNumber;
    private ServerConfiguration config;

    ServerThread(Socket clientSocket, int index, ServerConfiguration config) throws IOException {
        super();
        this.clientSocket = clientSocket;
        this.index = index;
        this.stop = false;
        this.config = config;
    }

    public int getIndex() {
        return index;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public void shutdown() {
        synchronized (stop) {
            stop = true;
            outThread.addRequest(CommunicationType.Exit, null);
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

        challengeNumber = ConnectionChallenge.getNumber();
        Log.debug("Sending Challenge number: " + challengeNumber);
        outThread.addRequest(CommunicationType.ChallengeNumber, challengeNumber);

        while (!isStop()) {
            try {
                //busy wait for incomming request from client
                while ((communication = getIncommingRequest()) == null) {
                    ServerThread.sleep(100);
                    if (isStop()) {
                        break;
                    }
                }
                if (!isStop()) {
                    //process incomming request
                    switch (communication.getReason()) {
                        case Exit:
                            Log.debug("Incomming Exit request");
                            shutdown();
                            break;
                        case ChallengeAnswer:
                            ChallengeAnswer(communication);
                            break;
                        case Password:
                            Log.debug("Incomming Password");
                            password(communication);
                            break;
                        default:
                            Log.warn("Incoming unexpected message: " + communication.getReason().toString());
                            break;
                    }
                }
            } catch (InterruptedException ex) {
                Log.warn("Interrupted Exception", ex);
            } catch (ClassCastException ex) {
                Log.warn("Problem on interpret Object of the incoming message", ex);
            }
        }
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
        Log.info("ServerThread shuting down...");
    }

    private void ChallengeAnswer(Communication communication) {
        try {
            Double challengeAnswer = (Double) communication.getObj();
            Log.debug("Incomming Challenge Answer: " + challengeAnswer);
            if (Math.abs(challengeAnswer - ConnectionChallenge.calc(challengeNumber)) <= Math.pow(10, -5)) {
                if (config.isPasswordRequired()) {
                    Log.debug("Password Required");
                    outThread.addRequest(CommunicationType.PasswordRequired, null);
                } else {
                    Log.debug("Conection accept");
                    outThread.addRequest(CommunicationType.ConnectionAccept, null);
                }
            } else {
                Log.fatal("Connection not accept");
                outThread.addRequest(CommunicationType.ConnectionNotAccept, null);
                shutdown();
            }
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.addRequest(CommunicationType.ConnectionNotAccept, null);
            shutdown();
        }
    }

    private void password(Communication communication) {
        try {
            String password = (String) communication.getObj();
            Log.debug("Incomming Password Answer: " + password);
            if (password.equals(config.getPassword())) {
                Log.debug("Conection accept");
                outThread.addRequest(CommunicationType.ConnectionAccept, null);
            } else {
                Log.fatal("Wrong Password");
                outThread.addRequest(CommunicationType.WrongPassword, null);
                shutdown();
            }
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.addRequest(CommunicationType.ConnectionNotAccept, null);
            shutdown();
        }
    }
}
