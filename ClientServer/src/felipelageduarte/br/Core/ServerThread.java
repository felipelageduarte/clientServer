/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package felipelageduarte.br.Core;

import felipelageduarte.br.Log.Log;
import felipelageduarte.br.Network.CommunicationType;
import felipelageduarte.br.Network.MessagePojo;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ServerThread extends NetworkElement {

    private Server server;
    private Socket clientSocket;
    private Integer index;
    private Boolean stop;
    private Boolean stopped;
    private Double challengeNumber;
    private ServerConfiguration config;
    private Boolean connectionAccept;
    private String nickName;

    ServerThread(Server server, Socket clientSocket, int index, ServerConfiguration config) {
        super();
        this.server = server;
        this.clientSocket = clientSocket;
        this.index = index;
        this.stop = false;
        this.connectionAccept = false;
        this.config = config;
        this.stopped = false;
        this.nickName = "";
    }

    public int getIndex() {
        return index;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public ServerConfiguration getConfig() {
        return config;
    }

    public String getClientNickName() {
        return nickName;
    }

    private void connectionAccept() {
        this.connectionAccept = true;
        server.newMessage(index, CommunicationType.ConnectionAccept, nickName);
    }

    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public void shutdown() {
        synchronized (stop) {
            outThread.newMessage(0, CommunicationType.Exit, null);
            stop = true;
        }
    }

    @Override
    public void run() {
        stopped = false;
        Log.info("New ServerThread running...");
        MessagePojo message = null;

        // I/O Threads
        try {
            inThread = new InThread(this, clientSocket);
            outThread = new OutThread(clientSocket);
            inThread.start();
            outThread.start();
        } catch (IOException ex) {
            Log.fatal("could not create Object Stream, Thread sutting down - " + ex.getMessage());
            stop = true;
        }

        challengeNumber = ConnectionChallenge.getNumber();
        Log.debug("Sending Challenge number: " + challengeNumber);
        outThread.newMessage(index, CommunicationType.ChallengeNumber, challengeNumber);

        while (!isStop()) {
            try {
                //busy wait for incomming request from client
                while ((message = getMessage()) == null) {
                    Thread.sleep(100);
                    if (isStop()) {
                        break;
                    }
                }
                if (!isStop()) {
                    Log.debug(nickName + ": processing message-" + index + ": " + message.getReason().toString());
                    //process incomming request
                    switch (message.getReason()) {
                        case Exit:
                            shutdown();
                            break;
                        case ChallengeAnswer:
                            ChallengeAnswer(message);
                            break;
                        case Password:
                            password(message);
                            break;
                        case NickName:
                            nickName(message);
                            break;
                        case SendData:
                            Log.debug(nickName + ": Sending data " + message.getObj().toString());
                            message.setReason(CommunicationType.IncommingData);
                            outThread.newMessage(message);
                            break;
                        case IncommingData:
                            if (connectionAccept) {
                                server.newMessage(message);
                            }
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
        Log.info(nickName + " ServerThread shuting down...");
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
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            Log.error("Problem stoping ServerThread - " + ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        stopped = true;
        Log.debug("ServerThread (" + nickName + ") - stopped:true");
        server.newMessage(index, CommunicationType.ClientDown, null);
    }

    private void ChallengeAnswer(MessagePojo communication) {
        try {
            Double challengeAnswer = (Double) communication.getObj();
            Log.debug("Incomming Challenge Answer: " + challengeAnswer);
            if (Math.abs(challengeAnswer - ConnectionChallenge.calc(challengeNumber)) <= Math.pow(10, -5)) {
                if (config.isPasswordRequired()) {
                    Log.debug("Password Required");
                    outThread.newMessage(0, CommunicationType.PasswordRequired, null);
                } else {
                    Log.debug("Nick Name Required");
                    outThread.newMessage(0, CommunicationType.NickNameRequired, null);
                }
            } else {
                Log.fatal("Connection not accept");
                outThread.newMessage(0, CommunicationType.ConnectionNotAccept, null);
                shutdown();
            }
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.newMessage(0, CommunicationType.ConnectionNotAccept, null);
            shutdown();
        }
    }

    private void password(MessagePojo communication) {
        try {
            String password = (String) communication.getObj();
            Log.debug("Incomming Password Answer: " + password);
            if (password.equals(config.getPassword())) {
                Log.debug("Conection accept");
                outThread.newMessage(0, CommunicationType.NickNameRequired, index);
            } else {
                Log.fatal("Wrong Password");
                outThread.newMessage(0, CommunicationType.WrongPassword, null);
                shutdown();
            }
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.newMessage(0, CommunicationType.ConnectionNotAccept, null);
            shutdown();
        }
    }

    private void nickName(MessagePojo communication) {
        try {
            this.nickName = (String) communication.getObj();
            Log.debug("Incomming NickName Answer: " + nickName);
            if (nickName.isEmpty()) {
                nickName = "Client-" + index;
                outThread.newMessage(0, CommunicationType.NickName, nickName);
            }
            if (config.isConfirmConnection()) {
                //Custom button text
                Object[] options = {"Sim", "Não"};
                int action = JOptionPane.showOptionDialog(null,
                        "O Cliente \"" + nickName + "\" esta pedindo autorização para se conectar.\nPermitir Conexão?",
                        "Novo Cliente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, //do not use a custom Icon
                        options, //the titles of buttons
                        options[0]); //default button title
                if (action == JOptionPane.YES_OPTION || action == JOptionPane.OK_OPTION) {
                    Log.info("Connection Accept");
                    outThread.newMessage(0, CommunicationType.ConnectionAccept, null);
                    this.connectionAccept();
                } else {
                    Log.info("Connection Not Accept");
                    outThread.newMessage(0, CommunicationType.ConnectionNotAccept, null);
                    shutdown();
                    return;
                }
            } else {
                Log.debug("Conection accept");
                outThread.newMessage(0, CommunicationType.ConnectionAccept, null);
                this.connectionAccept();
            }
        } catch (ClassCastException ex) {
            Log.warn("Problem on interpret Object of the incoming message", ex);
            outThread.newMessage(0, CommunicationType.ConnectionNotAccept, null);
            shutdown();
        }
    }
}
