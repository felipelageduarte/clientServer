package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.Network.ClientServer;
import ClientServer.igeom.usp.br.Network.CommunicationType;
import ClientServer.igeom.usp.br.Network.MessagePojo;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ServerConfiguration config;
    private ClientServer clientServer;
    private ServerConnection serverConnection;
    private ArrayList<ServerThread> serverThreadList = null;
    protected final LinkedList<MessagePojo> queue;
    private Boolean stop;
    private boolean stopped;
    private static int clientNumber;

    public Server(ClientServer clientServer, ServerConfiguration config) throws UnknownHostException, IOException {
        this.config = config;
        this.serverConnection = new ServerConnection(this);
        this.serverThreadList = new ArrayList<ServerThread>();
        this.queue = new LinkedList<MessagePojo>();
        this.clientServer = clientServer;
        this.stop = false;
        this.clientNumber = 0;
    }

    public synchronized boolean isStopped() {
        return stopped;
    }
    
    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public ServerConfiguration getConfig() {
        return config;
    }

    public synchronized void shutdown() {
        Log.info("Stoping main server thread...");

        //Stop Thread that accept Connection
        serverConnection.shutdown();
        while (!serverConnection.isStopped()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

        this.stop = true;
    }

    public void newMessage(MessagePojo message) {
        synchronized (queue) {
            queue.offer(message);
        }
    }

    public void newMessage(Integer whoSending, CommunicationType reason, Object request) {
        newMessage(new MessagePojo(whoSending, reason, request));
    }

    private MessagePojo getMessage() throws InterruptedException {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }

    private synchronized void updateThreadVector() {
        synchronized (serverThreadList) {
            //run through Thread vector to remove some thread that already shut down
            for (int i = 0; i < serverThreadList.size(); ++i) {
                if (serverThreadList.get(i).isStopped()) {
                    Log.debug("Remove dead Thread from threads vector");
                    serverThreadList.remove(i);
                }
            }
        }
    }

    public ArrayList<String> getClientList() {
        updateThreadVector();
        ArrayList<String> clientList = new ArrayList<String>();
        synchronized (serverThreadList) {
            for (ServerThread st : serverThreadList) {
                clientList.add(st.getClientNickName());
            }
        }
        return clientList;
    }

    @Override
    public void run() {
        Log.info("Server Up...");
        stopped = false;
        MessagePojo message = null;

        new Thread(serverConnection).start();

        while (!stop) {
            try {
                //busy wait for incomming request from client
                while ((message = getMessage()) == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    if (isStop()) {
                        break;
                    }
                }
                if (!stop && message != null) {
                    Log.debug("Incoming action request: " + message.getReason().toString());
                    //process incomming request
                    switch (message.getReason()) {
                        case ConnectionAccept:
                            clientServer.newMessage(message);
                            break;
                        case IncommingData:
                            clientServer.newMessage(message);
                            sendData(message);
                            break;
                        case Exit:
                            shutdown();
                            break;
                        case NewClient:
                            newClient((Socket) message.getObj());
                            break;
                        case ClientDown:
                            clientServer.newMessage(message);
                            break;
                        case SendData:
                            sendData(message);
                            break;
                        default:
                            Log.warn("Unexpected message: " + message.getReason().toString());
                            break;
                    };
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Log.debug("Stopping connected threads...");
        synchronized (serverThreadList) {
            //ShutDown all clients connected to server
            for (ServerThread st : serverThreadList) {
                st.shutdown();
            }
            //verify if all server Thread is down.
            for (ServerThread st : serverThreadList) {
                while(!st.isStopped()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        Log.info("Server Stopped.");
        Log.debug("------------------------------------------------------------");
        stopped = true;
    }

    private void newClient(Socket clientSocket) {
        ServerThread sT = null;
        synchronized (serverThreadList) {
            Log.debug("Starting ServerThread to handle new Client(" + ++clientNumber + ")...");
            sT = new ServerThread(this, clientSocket, clientNumber, config);
            new Thread(sT).start();
            serverThreadList.add(sT);
        }
    }

    /**
     * On incomming Data send it in broadCast
     *
     * @param cameFrom Variable that informe who has created and invoke the new
     * data
     * @param obj The Object that should be sent
     */
    void newClient(Integer index, String nickName) {
        //clientServer.updateClientListInterface();
    }

    public void sendData(MessagePojo message) {
        if (message != null) {
            Log.debug("Sending Data: " + message.toString());
            synchronized (serverThreadList) {
                for (ServerThread sT : serverThreadList) {
                    if (message.whoSend() != sT.getIndex()) {
                        sT.newMessage(new MessagePojo(message.whoSend(),
                                CommunicationType.SendData,
                                message.getObj()));
                    }
                }
            }
        }
    }
}
