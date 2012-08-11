package ClientServer.igeom.usp.br.network;

import ClientServer.igeom.usp.br.Log.Log;
import ClientServer.igeom.usp.br.core.Client;
import ClientServer.igeom.usp.br.core.ClientConfiguration;
import ClientServer.igeom.usp.br.core.Server;
import ClientServer.igeom.usp.br.core.ServerConfiguration;
import ClientServer.igeom.usp.br.protocol.ClientServerState;
import ClientServer.igeom.usp.br.protocol.CommunicationType;
import ClientServer.igeom.usp.br.view.ClientServerView;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ClientServer extends Observable implements Observer {

    private ClientServerView view = null;
    private Client client;
    private Server server;

    public ClientServer() {
        Log.info("New Client/Server...");
        this.view = new ClientServerView(this);
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
        //check if current assign client is running
        if (this.client != null && !this.client.isStopped()) {
            Log.warn("You can't assign a new Client with a older one running. Please, shutdown the current client to assign a new one");
            return false;
        } else {
            //if current client is not running and newClient is null, assgin and return
            if (newClient == null) {
                this.client = newClient;
                return true;
            } else {
                //if new Client is not null, we should check the server to avoid
                //assign newClient running with a server alredy running
                if (this.server == null) {
                    this.client = newClient;
                    this.client.addObserver(this);
                    return true;
                } else {
                    //if server is stopped assign the new client
                    if (this.server.isStopped()) {
                        this.client = newClient;
                        this.client.addObserver(this);
                        return true;
                    } else {
                        //if server is not stopped we can only accept new client stopped or null
                        if (newClient.isStopped()) {
                            this.client = newClient;
                            this.client.addObserver(this);
                            return true;
                        } else {
                            Log.warn("You can't assign a running new Client with a server running. Please, shutdown the current server or the new client before assign it");
                            return false;
                        }
                    }
                }
            }
        }
    }

    // this code is necessary to avoid inconsistency when user want to add 
    // a Server in running state, while we are already handling another class 
    // in running state
    public boolean setServer(Server newServer) {
        //check if current assign server is running
        if (this.server != null && !this.server.isStopped()) {
            Log.warn("You can't assign a new Server with a older one running. Please, shutdown the current server to assign a new one");
            return false;
        } else {
            //if current server is not running and newServer is null, assgin and return
            if (newServer == null) {
                this.server = newServer;
                return true;
            } else {
                //if new Server is not null, we should check the client to avoid
                //assign newServer running with a client alredy running
                if (this.client == null) {
                    this.server = newServer;
                    this.server.addObserver(this);
                    return true;
                } else {
                    //if client is stopped assign the new server
                    if (this.client.isStopped()) {
                        this.server = newServer;
                        this.server.addObserver(this);
                        return true;
                    } else {
                        //if client is not stopped we can only accept new server stopped or null
                        if (newServer.isStopped()) {
                            this.server = newServer;
                            this.server.addObserver(this);
                            return true;
                        } else {
                            Log.warn("You can't assign a running new Server with a client running. Please, shutdown the current client or the new server before assign it");
                            return false;
                        }
                    }
                }
            }
        }
    }

    //show view
    public void showView() {
        this.view.setVisible(true);
    }

    public synchronized void newData(Object cameFrom, Object obj) {
        if (cameFrom != this) {
            notifyObservers(obj);
            setChanged();
            notifyAll();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.debug("Incoming update: " + ((ClientServerState) arg).toString());
        switch ((ClientServerState) arg) {
            case CLIENT_CONNECTED:
                view.clientConected();
                break;
            default:
                break;
        }
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
        } catch (IOException ex) {
            Log.warn(ex.getMessage());
        }
    }

    public void shutdownServer() {
        server.addAction(this, CommunicationType.Exit, null);
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
            JOptionPane.showMessageDialog(null, "Servidor nao encontrado...\n Verifique a porta/IP informado", "Unknow Host", JOptionPane.WARNING_MESSAGE);
            view.clientStopped();
        } catch (IOException ex) {
            Log.error("Could not initiate socket - " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Socket nao criado - Conexão não estabelecida", "I/O Exception", JOptionPane.ERROR_MESSAGE);
            view.clientStopped();
        }
    }

    public void shutdownClient() {
        if (client != null && !client.isStopped()) {
            client.shutdown();
        }
    }

    private void updateClientListInterface() {
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
