package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.Network.ClientServer;
import ClientServer.igeom.usp.br.Protocol.CommunicationType;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;

public class Server extends Observable implements Runnable {

    private ServerConfiguration config;
    private ServerConnection serverConnection;
    private ArrayList<ServerThread> serverThreadList = null;
    protected LinkedList<MessagePojo> actionQueue;
    private boolean stop;
    private boolean stopped;
    private static int clientNumber;
    private ClientServer clientServer;

    public Server(ClientServer clientServer, ServerConfiguration config) throws UnknownHostException, IOException {
        this.config = config;
        this.serverConnection = new ServerConnection(this);
        this.clientServer = clientServer;
        this.serverThreadList = new ArrayList<ServerThread>();
        this.actionQueue = new LinkedList<MessagePojo>();
        clientNumber = 0;
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public ServerConfiguration getConfig() {
        return config;
    }

    public synchronized void shutdown() {
        Log.info("Stoping main server thread...");
        this.stop = true;

        //Stop Thread that accept Connection
        serverConnection.shutdown();
        while (!serverConnection.isStopped()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void addAction(Integer whoSending, CommunicationType reason, Object request) {
        synchronized (actionQueue) {
            actionQueue.offer(new MessagePojo(whoSending, reason, request));
        }
    }

    private MessagePojo getIncommingRequest() {
        synchronized (actionQueue) {
            return actionQueue.pollFirst();
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
            for (int i = 0; i < serverThreadList.size(); ++i) {
                clientList.add(serverThreadList.get(i).getClientNickName());
            }
        }
        return clientList;
    }

    @Override
    public void run() {
        Log.info("Server Up...");
        stopped = false;
        MessagePojo communication = null;

        new Thread(serverConnection).start();

        while (!stop) {
            //busy wait for incomming request from client
            while ((communication = getIncommingRequest()) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
                if (!stop) {
                    break;
                }
            }
            if (!stop && communication != null) {
                Log.debug("Incoming action request: " + communication.getReason().toString());
                //process incomming request
                switch (communication.getReason()) {
                    case Exit:
                        shutdown();
                        break;
                    case NewClient:
                        newClient((Socket) communication.getObj());
                        break;
                    case Data:
                        newData(communication.getWhoSend(), communication.getObj());
                        break;
                    default:
                        Log.warn("Unexpected message: " + communication.getReason().toString());
                        break;
                }
            }
        }

        Log.debug("Stopping connected threads...");
        ServerThread stAux = null;
        synchronized (serverThreadList) {
            for (int i = 0; i < serverThreadList.size(); ++i) {
                stAux = serverThreadList.get(i);
                if (stAux != null) {
                    stAux.shutdown();
                    while (!stAux.isStopped()) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception ex) {
                        }
                        updateThreadVector();
                    }
                }
            }
        }

//        Log.debug("Stopping connetion Thread");
//        serverConnection.shutdown();
//        while (!serverConnection.isStopped()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//            }
//        }

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
    private void newData(int cameFrom, Object obj) {
        ServerThread sT = null;
        Log.debug("Broadcasting incomming Data: " + obj.toString());
        synchronized (serverThreadList) {
            clientServer.newData(cameFrom, obj);
            for (int i = 0; i < serverThreadList.size(); ++i) {
                sT = serverThreadList.get(i);
                if (sT.isStopped()) {
                    Log.debug("Remove dead Thread from threads vector");
                    serverThreadList.remove(i);
                } else {
                    sT.sendData(cameFrom, obj);
                }
            }
        }
    }

    void newClient(Integer index, String nickName) {
        clientServer.updateClientListInterface();
    }

    void clientShutdown() {
        clientServer.updateClientListInterface();
    }

    public void sendData(Object data) {
        ServerThread sT = null;
        Log.debug("Sending Data: " + data.toString());
        synchronized (serverThreadList) {
            for (int i = 0; i < serverThreadList.size(); ++i) {
                sT = serverThreadList.get(i);
                if (sT.isStopped()) {
                    Log.debug("Remove dead Thread from threads vector");
                    serverThreadList.remove(i);
                } else {
                    sT.sendData(0, data);
                }
            }
        }
    }
}
