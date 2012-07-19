package network;

import Interface.Interface;
import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Client extends ArchitectureThread {

    private ClientConfiguration config;
    private Socket socket;
    private Boolean stop;

    public Client(ClientConfiguration config) {
        Log.info("New Client...");
        this.config = config;
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
            Log.debug("Trying to connect to server " + this.config.getServerAddress() + ":" + this.config.getServerPort());
            socket = new Socket(this.config.getServerAddress(), this.config.getServerPort());
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
                                challengeNumber(communication);
                                break;
                            case ConnectionAccept:
                                Log.debug("Connection Accepted");
                                break;
                            case ConnectionNotAccept:
                                Log.fatal("Connection Not Accepted");
                                shutdown();
                                break;
                            case PasswordRequired:
                                passwordRequired();
                                break;
                            case WrongPassword:
                                Log.debug("Incomming Wrong Password");
                                wrongPassword(communication);
                                break;
                            default:
                                Log.warn("Incoming unexpected message: " + communication.getReason().toString());
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

        Interface.getInstance().clientStoped();
    }

    private void challengeNumber(Communication communication) {
        try {
            Double challengeNumber = (Double) communication.getObj();
            Log.debug("Incomming Challenge Number: " + challengeNumber);
            Double challengeAnswer = ConnectionChallenge.calc(challengeNumber);
            Log.debug("Challenge Answer: " + challengeAnswer);
            outThread.addRequest(CommunicationType.ChallengeAnswer, challengeAnswer);
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.addRequest(CommunicationType.Exit, null);
            shutdown();
        }
    }

    private void passwordRequired() {
        Log.debug("Password Required for connection");
        if (config.getPassword().isEmpty()) {
            JPasswordField pwd = new JPasswordField(10);
            int action = JOptionPane.showConfirmDialog(null, pwd, "Entre com a senha", JOptionPane.OK_CANCEL_OPTION);
            if (action < 0) {
                Log.debug("User closed password box");
                outThread.addRequest(CommunicationType.Exit, null);
                shutdown();
            } else {
                outThread.addRequest(CommunicationType.Password, new String(pwd.getPassword()));
                JOptionPane.showMessageDialog(null, "Your password is " + new String(pwd.getPassword()));
            }
        } else {
            Log.debug("Sending Password: " + config.getPassword());
            outThread.addRequest(CommunicationType.Password, config.getPassword());
        }
    }

    private void wrongPassword(Communication communication) {
        Log.debug("Wrong password");
        JOptionPane.showMessageDialog(null, "Wrong Password", "Wrong Password", JOptionPane.ERROR_MESSAGE);
    }
}
