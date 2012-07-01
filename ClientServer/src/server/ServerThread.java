/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import ConnectionChallenge.ConnectionChallenge;
import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author felipelageduarte
 */
public class ServerThread extends Thread {

    private Socket clientSocket;
    private int index;
    private InThread inThread;
    private OutThread outThread;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    ServerThread(Socket clientSocket, int index) throws IOException {
        this.clientSocket = clientSocket;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    private boolean connectionChallenge() {
        Double numberReceived,
                challengeNumber,
                ChalengeNumberReceived;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
            return false;
        }

        try {
            //recive challenge number comming from client side
            numberReceived = (Double) in.readObject();
            //calculate the challenge and send back the result
            out.writeObject(ConnectionChallenge.calc(numberReceived));
            if ((Boolean) in.readObject()) {
                //get new random number
                challengeNumber = ConnectionChallenge.getNumber();
                //send the random number as challenge to the client
                out.writeObject(challengeNumber);
                System.out.println(challengeNumber);
                //get the chanllenge answer comming from client
                ChalengeNumberReceived = (Double)in.readObject();
                
                //verify if challenge was responded correctly
                if (Math.abs(ChalengeNumberReceived - ConnectionChallenge.calc(challengeNumber)) < Math.pow(10, -5)) {
                    System.out.println("here");
                    //Send to client that connection was accepted
                    out.writeObject(true);
                    //wait for connection accepted confirmation comming from client
                    if ((Boolean) in.readObject()) {
                        inThread = new InThread(in);
                        outThread = new OutThread(out);
                        inThread.run();
                        outThread.run();
                    } else {
                        Log.info("Connection was not accepted for client");
                        return false;
                    }
                } else {
                    Log.info("Wrong challenge answer");
                    out.writeObject(false);
                    return false;
                }
            } else {
                Log.info("Disconnected from client side");
                return false;
            }
        } catch (IOException ex) {
            Log.fatal("Problem on verify client / server challenge - " + ex.getMessage());
            return false;
        } catch (ClassNotFoundException ex) {
            Log.fatal("Problem on verify client / server challenge - " + ex.getMessage());
            return false;
        }
        return true;
    }

    public void shutdown() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (inThread != null) {
                inThread.stop();
            }
            if (outThread != null) {
                outThread.stop();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            Log.error("Problem stoping ServerThread - " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        Log.info("New ServerThread running...");
        if (connectionChallenge()) {
            shutdown();
        } else {
            Log.warn("Connection not accept");
            shutdown();
        }
        Log.info("Bye...");
    }
}
