package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.Network.ClientServer;
import ClientServer.igeom.usp.br.Network.CommunicationType;
import ClientServer.igeom.usp.br.Network.MessagePojo;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Client extends NetworkElement {

    private ClientConfiguration config;
    private ClientServer clientServer;
    private Socket socket;
    private Boolean stop;
    private Integer index;
    private boolean stopped;

    public Client(ClientServer clientServer, ClientConfiguration config) throws UnknownHostException, IOException {
        Log.info("New Client...");
        this.type = CLIENT;
        this.config = config;
        this.index = -1;
        stop = false;
        this.clientServer = clientServer;
        Log.debug("Trying to connect to server " + this.config.getServerAddress() + ":" + this.config.getServerPort());
        setChanged();
        socket = new Socket(this.config.getServerAddress(), this.config.getServerPort());
    }

    public boolean isStopped() {
        return stopped;
    }
    
    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public void shutdown() {
        synchronized (stop) {
            if (outThread != null) {
                outThread.newMessage(index, CommunicationType.Exit, null);
            }
            stop = true;
        }
    }

    @Override
    public void run() {
        Log.info("Client Running...");
        stopped = false;
        MessagePojo message = null;

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
                try {
                    //busy wait for incomming request from client
                    while ((message = getMessage()) == null) {
                        try {
                            Thread.sleep(100);
                            if (isStop()) {
                                break;
                            }
                        } catch (InterruptedException ex) {
                        }
                    }
                    if (!stop && message != null) {
                        Log.debug("Incoming message: " + message.getReason().toString());
                        //process incomming request
                        switch (message.getReason()) {
                            case Exit:
                                Log.debug("Incomming Exit request");
                                shutdown();
                                break;
                            case ChallengeNumber:
                                challengeNumber(message);
                                break;
                            case ConnectionAccept:
                                Log.debug("Connection Accepted");
                                clientServer.newMessage(message);
                                break;
                            case ConnectionNotAccept:
                                Log.fatal("Connection Not Accepted");
                                shutdown();
                                break;
                            case PasswordRequired:
                                passwordRequired();
                                break;
                            case WrongPassword:
                                wrongPassword(message);
                                break;
                            case NickNameRequired:
                                NickNameRequired(message);
                                break;
                            case NickName:
                                NickName(message);
                                break;
                            case SendData:
                                message.setWhoSending(index);
                                message.setReason(CommunicationType.IncommingData);
                                outThread.newMessage(message);
                                break;
                            case IncommingData:
                                clientServer.newMessage(message);
                                break;
                            default:
                                Log.warn("Unexpected message: " + message.getReason().toString());
                                break;
                        }
                    }
                } catch (InterruptedException ex) {
                    Log.warn("Interrupted Exception", ex);
                } catch (ClassCastException ex) {
                    Log.warn("Problem on interpret Object of the incoming message", ex);
                }
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
            Log.error("Problem stoping ServerThread - " + ex.getMessage());
        }
        clientServer.newMessage(index, CommunicationType.ClientDown, null);
        stopped = true;
        Log.info("Client Stoped");
        Log.debug("------------------------------------------------------------");
    }

    private void challengeNumber(MessagePojo message) {
        try {
            index = message.whoSend();
            Double challengeNumber = (Double) message.getObj();
            Log.debug("Incomming Challenge Number: " + challengeNumber);
            Double challengeAnswer = ConnectionChallenge.calc(challengeNumber);
            Log.debug("Challenge Answer: " + challengeAnswer);
            outThread.newMessage(index, CommunicationType.ChallengeAnswer, challengeAnswer);
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.newMessage(index, CommunicationType.Exit, null);
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
                outThread.newMessage(index, CommunicationType.Exit, null);
                shutdown();
            } else {
                outThread.newMessage(index, CommunicationType.Password, new String(pwd.getPassword()));
            }
        } else {
            Log.debug("Sending Password: " + config.getPassword());
            outThread.newMessage(index, CommunicationType.Password, config.getPassword());
        }
    }

    private void wrongPassword(MessagePojo communication) {
        Log.debug("Wrong password");
        JOptionPane.showMessageDialog(null, "Wrong Password", "Wrong Password", JOptionPane.ERROR_MESSAGE);
    }

    private void NickNameRequired(MessagePojo communication) {
        outThread.newMessage(index, CommunicationType.NickName, config.getNickName());
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
}
