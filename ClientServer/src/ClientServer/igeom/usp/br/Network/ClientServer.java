package ClientServer.igeom.usp.br.Network;

import ClientServer.igeom.usp.br.Core.*;
import ClientServer.igeom.usp.br.Log.Log;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ClientServer extends Observable implements Runnable {

    protected final LinkedList<MessagePojo> queue;
    private Client client;
    private Server server;
    private Boolean stop;
    private boolean stopped;
    private int whoIsRunning;
    private static final int NOBODY = -1;
    private static final int SERVER = 0;
    private static final int CLIENT = 1;

    public ClientServer() {
        Log.info("New Client/Server...");
        this.queue = new LinkedList<MessagePojo>();
        whoIsRunning = NOBODY;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        synchronized (stop) {
            stop = true;
        }
    }

    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    //used for incomming new data from software
    public void sendData(Object data) {
        if (whoIsRunning == CLIENT) {
            client.newMessage(-1, CommunicationType.SendData, data);
        } else if (whoIsRunning == SERVER) {
            server.newMessage(0, CommunicationType.SendData, data);
        } else {
            //nothing to be done
        }
    }

    // this code is necessary to avoid inconsistency when user want to add 
    // a Client in running state, while we are already handling another class 
    // in running state
    public boolean setClient(Client newClient) {
        if (whoIsRunning == CLIENT) {
            //check if current assigned client is running
            Log.warn("You can't assign a new Client with a older one running. "
                    + "Please, shutdown the current client to assign a new one");
            return false;
        } else if (whoIsRunning == SERVER) {
            //if server is running we can only accept new client stopped or null
            if (newClient.isStopped()) {
                this.client = newClient;
                return true;
            } else {
                Log.warn("You can't assign a running new Client with a server "
                        + "running. Please, shutdown the current server or the "
                        + "new client before assign it");
                return false;
            }
        } else {
            //If nobody is running
            this.client = newClient;
            if (!newClient.isStopped()) {
                whoIsRunning = CLIENT;
            }
            return true;
        }
    }

    public boolean newClient(ClientConfiguration config) {
        try {
            Client _client = new Client(this, config);
            new Thread(_client).start();
            if (!setClient(_client)) {
                _client.shutdown();
                Log.warn("Problem to assign new client");
            }
        } catch (UnknownHostException ex) {
            Log.error("Server not found - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Servidor nao "
                    + "encontrado!\n Verifique a porta/IP informado", 
                    "Unknow Host", 
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (IOException ex) {
            Log.error("Could not initiate socket - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Socket nao "
                    + "criado - Conexão não estabelecida", 
                    "I/O Exception",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    
    // this code is necessary to avoid inconsistency when user want to add 
    // a Server in running state, while we are already handling another class 
    // in running state
    public boolean setServer(Server newServer) {
        if (whoIsRunning == SERVER) {
            //check if current assigned client is running
            Log.warn("You can't assign a new Server with a older one running. "
                    + "Please, shutdown the current server to assign a new one");
            return false;
        } else if (whoIsRunning == CLIENT) {
            //if server is running we can only accept new client stopped or null
            if (newServer.isStopped()) {
                this.server = newServer;
                return true;
            } else {
                Log.warn("You can't assign a running new Server with a client "
                        + "running. Please, shutdown the current client or the "
                        + "new server before assign it");
                return false;
            }
        } else {
            //If nobody is running
            this.server = newServer;
            if (!newServer.isStopped()) {
                whoIsRunning = SERVER;
            }
            return true;
        }
    }

    public boolean newServer(ServerConfiguration config) {
        try {
            Server server = new Server(this, config);
            new Thread(server).start();
            if (!setServer(server)) {
                server.shutdown();
                Log.warn("Problem to assign new server");
            }
        } catch (UnknownHostException ex) {
            Log.warn(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Log.error(ex.getMessage());
            JOptionPane.showMessageDialog(null, "A porta esta sendo "
                    + "utilizada por outra aplicação!\nTente outra porta para "
                    + "iniciar o servidor", "Warning", 
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    
    public void newMessage(MessagePojo message) {
        synchronized (queue) {
            queue.offer(message);
        }
    }

    public void newMessage(Integer whoSending, CommunicationType reason, Object request) {
        newMessage(new MessagePojo(whoSending, reason, request));
    }

    protected MessagePojo getMessage() throws InterruptedException {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }

    @Override
    public void run() {
        Log.info("ClientServer Up...");
        stopped = false;
        stop = false;
        MessagePojo message = null;

        while (!stop) {
            try {
                //busy wait for incomming request from client
                while ((message = getMessage()) == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    if (!stop) {
                        break;
                    }
                }
                if (!stop && message != null) {
                    Log.debug("Incoming message: " + message.getReason().toString());
                    //process incomming request
                    switch (message.getReason()) {
                        case ConnectionAccept:
                        case IncommingData:
                            setChanged();
                            break;
                        case Exit:
                            shutdown();
                            break;  
                        default:
                            Log.warn("Unexpected message: " + message.getReason().toString());
                            break;
                    }
                    notifyObservers(message);
                    clearChanged();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Log.info("ClientServer Stopped .");
        Log.debug("------------------------------------------------------------");
        stopped = true;
    }

    //used for incomming new data from network
    public synchronized void IncommingData(MessagePojo message) {
    }

    
    public void shutdownServer() {
        server.newMessage(0, CommunicationType.Exit, null);
    }

    
    public boolean shutdownClient() {
        if (client != null && !client.isStopped()) {
            client.shutdown();
        }
        return true;
    }

    public ArrayList<String> getClientList() {
        ArrayList<String> list = new ArrayList<String>();
        if (whoIsRunning == SERVER) {
            list = server.getClientList();            
        }
        return list;    
    }
}
