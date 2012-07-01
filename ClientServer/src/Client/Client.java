/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import ConnectionChallenge.ConnectionChallenge;
import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author felipelageduarte
 */
public class Client implements Runnable {

    private String serverAddress;
    private int serverPort;
    private Socket socket = null;
    private InThread inThread;
    private OutThread outThread;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    private boolean connectionChallenge() {
        Double numberReceived,
                challengeNumber,
                ChalengeNumberReceived;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
            return false;
        }

        try {
            //get new random number
            challengeNumber = ConnectionChallenge.getNumber();
            //send the random number as challenge to the server
            out.writeDouble(challengeNumber);
            //get the chanllenge answer comming from server
            ChalengeNumberReceived = in.readDouble();
            //verify if challenge was responded correctly
            if (Math.abs(ChalengeNumberReceived - ConnectionChallenge.calc(challengeNumber)) < Math.pow(10, -5)) {
                //send challenge accepted
                out.writeObject(true);
                //recive challenge number comming from client side
                numberReceived = in.readDouble();
                //calculate the challenge and send back the result
                out.writeDouble(ConnectionChallenge.calc(numberReceived));

                //wait for connection accepted confirmation comming from client
                if (in.readBoolean()) {
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
                out.writeBoolean(false);
                return false;
            }
        } catch (IOException ex) {
            Log.fatal("Problem on verify client / server challenge - " + ex.getMessage());
            return false;
        }
        return true;
    }

    public void stop() {
        try {
            if (socket != null) {
                socket.close();
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
        try {
            socket = new Socket(this.serverAddress, this.serverPort);
        } catch (UnknownHostException ex) {
            Log.error("Server not found - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Servidor nao encontrado...\n Verifique a porta/IP informado", "Unknow Host", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            Log.error("Could not initiate socket - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Socket nao criado - Conexão não estabelecida", "I/O Exception", JOptionPane.ERROR_MESSAGE);
        }

        if (connectionChallenge()) {
            stop();
        } else {
            Log.warn("Connection not accept");
            stop();
        }
        Log.info("Bye...");
    }
}
