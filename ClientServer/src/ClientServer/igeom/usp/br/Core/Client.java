package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.Protocol.ClientServerState;
import ClientServer.igeom.usp.br.Protocol.CommunicationType;
import ClientServer.igeom.usp.br.Protocol.ConnectionChallenge;
import java.io.IOException;
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
    private Integer index;
    private boolean stopped;

    public Client(ClientConfiguration config) throws UnknownHostException, IOException {
        Log.info("New Client...");
        this.type = CLIENT;
        this.config = config;
        this.index = -1;
        stop = false;

        Log.debug("Trying to connect to server " + this.config.getServerAddress() + ":" + this.config.getServerPort());
        this.state = ClientServerState.CLIENT_CONNECTING;
        setChanged();
        socket = new Socket(this.config.getServerAddress(), this.config.getServerPort());
    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        synchronized (stop) {            
            outThread.addRequest(index, CommunicationType.Exit, null);
            this.state = ClientServerState.CLIENT_STOPPING;
            notifyObservers(state); 
            stop = true;
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
                outThread = new OutThread(socket);
                inThread = new InThread(this, socket);
                inThread.start();
                outThread.start();
            } catch (IOException ex) {
                Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
                stop = true;
            }
            while (!stop) {
                //busy wait for incomming request from client
                while ((communication = getIncommingMessage()) == null) {
                    try {
                        Thread.sleep(100);
                        if (!stop) {
                            break;
                        }
                    } catch (InterruptedException ex) {
                    }
                }
                if (!stop && communication != null) {
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
                notifyObservers(state);
                clearChanged();
            }
        }

        Log.info("Client shuting Down...");
        try {            
            if (outThread != null) {
                outThread.shutdown();
                while (!outThread.isStopped()) {
                    Thread.sleep(100);
                }
            }
            if (inThread != null) {
                inThread.shutdown();
                while (!inThread.isStopped()) {
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
            outThread.addRequest(index, CommunicationType.ChallengeAnswer, challengeAnswer);
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.addRequest(index, CommunicationType.Exit, null);
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
                outThread.addRequest(index, CommunicationType.Exit, null);
                shutdown();
            } else {
                outThread.addRequest(index, CommunicationType.Password, new String(pwd.getPassword()));
            }
        } else {
            Log.debug("Sending Password: " + config.getPassword());
            outThread.addRequest(index, CommunicationType.Password, config.getPassword());
        }
    }

    private void wrongPassword(MessagePojo communication) {
        Log.debug("Wrong password");
        JOptionPane.showMessageDialog(null, "Wrong Password", "Wrong Password", JOptionPane.ERROR_MESSAGE);
    }

    private void NickNameRequired(MessagePojo communication) {
        this.index = (Integer)communication.getObj();
        outThread.addRequest(index, CommunicationType.NickName, config.getNickName());
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

    public void sendData(Object data) {
        outThread.addRequest(index, CommunicationType.Data, data);
    }
}
