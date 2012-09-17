package ClientServer.igeom.usp.br.Network;

import ClientServer.igeom.usp.br.Core.Client;
import ClientServer.igeom.usp.br.Core.ClientConfiguration;
import ClientServer.igeom.usp.br.Core.Server;
import ClientServer.igeom.usp.br.Core.ServerConfiguration;
import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.Protocol.ClientServerState;
import ClientServer.igeom.usp.br.Protocol.CommunicationType;
import ClientServer.igeom.usp.br.View.ClientServerView;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ClientServer extends Observable implements Observer {

    private ClientServerView view = null;
    private Client client;
    private Server server;
    private int whoIsRunning;
    private static final int NOBODY = -1;
    private static final int SERVER = 0;
    private static final int CLIENT = 1;

    public ClientServer() {
        Log.info("New Client/Server...");
        this.view = new ClientServerView(this);
        whoIsRunning = NOBODY;
    }

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
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
                this.client.addObserver(this);
            }
            return true;
        }
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
                this.server.addObserver(this);
            }
            return true;
        }
    }

    //show view
    public void showView() {
        this.view.setVisible(true);
    }

    //used for incomming new data from network
    public synchronized void newData(Integer cameFrom, Object obj) {
        if (cameFrom != 0) {
            notifyObservers(obj);
            setChanged();
            notifyAll();
        }
    }

    //used for incomming new data from software
    public void sendData(Object data) {
        if (whoIsRunning == CLIENT) {
            client.sendData(data);
        } else if (whoIsRunning == SERVER) {
            server.sendData(data);
        } else {
            Log.warn("No client and neither server is running. Data could not be"
                    + " sent via network");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //incomming update
        Log.debug("Incoming update: " + ((ClientServerState) arg).toString());

        //BroadCast if Server is running
        switch ((ClientServerState) arg) {
            case CLIENT_CONNECTED:
                view.clientConected();
                break;
            default:
                break;
        }

        //pass incomming data do iGeom
    }

    public void newServer(ServerConfiguration config) {
        try {
            Server server = new Server(this, config);
            new Thread(server).start();
            if (!setServer(server)) {
                server.shutdown();
                Log.warn("Problem to assign new server");
            }
        } catch (UnknownHostException ex) {
            Log.warn(ex.getMessage());
            view.serverStopped();
        } catch (IOException ex) {
            Log.error(ex.getMessage());
            view.serverStoppedAndAlert("Warning", "A porta esta sendo utilizada"
                    + " por outra aplicação!\nTente outra porta para iniciar o "
                    + "servidor");
        }
    }

    public void shutdownServer() {
        server.addAction(0, CommunicationType.Exit, null);
    }

    public void newClient(ClientConfiguration config) {
        try {
            Client $client = new Client(config);
            new Thread($client).start();
            if (!setClient($client)) {
                $client.shutdown();
                Log.warn("Problem to assign new client");
            }
            updateClientListInterface();
        } catch (UnknownHostException ex) {
            Log.error("Server not found - " + ex.getMessage());
            view.clientStoppedAndAlert("Unknow Host", "Servidor nao encontrado!"
                    + "\n Verifique a porta/IP informado");
        } catch (IOException ex) {
            Log.error("Could not initiate socket - " + ex.getMessage());
            view.clientStoppedAndAlert("I/O Exception", "Socket nao criado -"
                    + " Conexão não estabelecida");
        }
    }

    public boolean shutdownClient() {
        if (client != null && !client.isStopped()) {
            client.shutdown();
        }
        return true;
    }

    public void updateClientListInterface() {
        String list = "";
        if (server != null) {
            ArrayList<String> clientList = server.getClientList();
            for (int i = 0; i < clientList.size(); ++i) {
                list += (i + 1) + ": " + clientList.get(i) + "\n";
            }
        }
        if (view != null) {
            view.updateClientListInterface(list);
        }
    }
}
