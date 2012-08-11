package ClientServer.igeom.usp.br.core;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.protocol.ClientServerState;
import ClientServer.igeom.usp.br.protocol.CommunicationType;
import ClientServer.igeom.usp.br.protocol.ConnectionChallenge;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Client extends NetworkElement {

    private ClientConfiguration config;
    private Socket socket;
    private Boolean stop;
    private boolean stopped;

    public Client(ClientConfiguration config) throws UnknownHostException, IOException {
        Log.info("New Client...");
        this.type = CLIENT;
        this.state = ClientServerState.CLIENT_RUNNING;
        this.config = config;
        stop = false;

        Log.debug("Trying to connect to server " + this.config.getServerAddress() + ":" + this.config.getServerPort());
        this.state = ClientServerState.CLIENT_CONNECTING;
        setChanged();
        socket = new Socket(this.config.getServerAddress(), this.config.getServerPort());

    }

    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        synchronized (stop) {
            stop = true;
            outThread.addRequest(CommunicationType.Exit, null);
            this.state = ClientServerState.CLIENT_STOPPING;
            notifyObservers(state);
        }
    }

    @Override
    public void run() {
        Log.info("Client Running...");
        stopped = false;
        MessagePojo communication = null;

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
                    while ((communication = getIncommingMessage()) == null) {
                        Thread.sleep(100);
                        if (isStop()) {
                            break;
                        }
                    }
                    if (!isStop()) {
                        Log.debug("Incoming message: " + communication.getReason().toString());
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
                                this.state = ClientServerState.CLIENT_CONNECTED;
                                setChanged();
                                break;
                            case ConnectionNotAccept:
                                Log.fatal("Connection Not Accepted");
                                shutdown();
                                break;
                            case PasswordRequired:
                                passwordRequired();
                                break;
                            case WrongPassword:
                                wrongPassword(communication);
                                break;
                            case NickNameRequired:
                                NickNameRequired(communication);
                                break;
                            case NickName:
                                NickName(communication);
                                break;
                            case Data:
                                state = ClientServerState.INCOMMING_DATA;
                                setChanged();
                                break;
                            default:
                                Log.warn("Unexpected message: " + communication.getReason().toString());
                                break;
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                notifyObservers(state);
                clearChanged();
            }
        }

        Log.info("Client shuting Down...");
        try {
            if (inThread != null) {
                inThread.shutdown();
                while (!inThread.isStopped()) {
                    Thread.sleep(100);
                }
            }
            if (outThread != null) {
                outThread.shutdown();
                while (!outThread.isStopped()) {
                    Thread.sleep(100);
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

        this.state = ClientServerState.CLIENT_STOPPED;
        setChanged();
        notifyObservers(state);
        clearChanged();
        stopped = true;
        Log.info("Client Stoped");
        Log.debug("------------------------------------------------------------");
    }

    private void challengeNumber(MessagePojo communication) {
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
            }
        } else {
            Log.debug("Sending Password: " + config.getPassword());
            outThread.addRequest(CommunicationType.Password, config.getPassword());
        }
    }

    private void wrongPassword(MessagePojo communication) {
        Log.debug("Wrong password");
        JOptionPane.showMessageDialog(null, "Wrong Password", "Wrong Password", JOptionPane.ERROR_MESSAGE);
    }

    private void NickNameRequired(MessagePojo communication) {
        outThread.addRequest(CommunicationType.NickName, config.getNickName());
        Log.debug("NickName sended: " + config.getNickName());
    }

    private void NickName(MessagePojo communication) {
        try {
            config.setNickName((String) communication.getObj());
            Log.debug("Incomming NickName: " + config.getNickName());
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
        }
    }

    private void incommingData(MessagePojo communication) {
        try {
            communication.getObj();
            Log.debug("Incomming Data");
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
        }
    }
}
