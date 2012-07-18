package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Client extends ArchitectureThread {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private Boolean stop;

    public Client(String serverAddress, int serverPort) {
        Log.info("New Client...");
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        stop = false;
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
        Log.info("Client Running...");

        Communication communication = null;

        try {
            Log.debug("Trying to connect to server " + this.serverAddress + ":" + this.serverPort);
            socket = new Socket(this.serverAddress, this.serverPort);
        } catch (UnknownHostException ex) {
            Log.error("Server not found - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Servidor nao encontrado...\n Verifique a porta/IP informado", "Unknow Host", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            Log.error("Could not initiate socket - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Socket nao criado - Conexão não estabelecida", "I/O Exception", JOptionPane.ERROR_MESSAGE);
        }
        if (socket != null) {
            // I/O Threads
            try {
                outThread = new OutThread(new ObjectOutputStream(socket.getOutputStream()));
                inThread = new InThread(this, new ObjectInputStream(socket.getInputStream()));
                inThread.start();
                outThread.start();
            } catch (IOException ex) {
                Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
                stop = true;
            }



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
        }

        Log.info("Client shuting Down...");
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
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Log.error("Problem stoping ServerThread - " + ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
