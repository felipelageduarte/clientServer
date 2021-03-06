package felipelageduarte.br.Core;

import felipelageduarte.br.Log.Log;
import felipelageduarte.br.Network.CommunicationType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection implements Runnable {

    private ServerSocket serverSocket;
    private Server server;
    private boolean stop;
    private boolean stopped;

    public ServerConnection(Server server) throws UnknownHostException, IOException {
        this.server = server;
        //try to open server socket

        this.serverSocket = new ServerSocket(server.getConfig().getPort());
        Log.debug("server initiated on address: "
                + InetAddress.getLocalHost().getHostAddress() + ":"
                + server.getConfig().getPort());

    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        Log.debug("Stopping ServerConnection...");
        stop = true;        
        try {
            if (serverSocket != null) {
                this.serverSocket.close();
                while(!serverSocket.isClosed()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
                Log.debug("serverSocker closed");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing ServerConnection", e);
        }
    }

    @Override
    public void run() {
        Log.info("ServerConnection running.");
        Socket clientSocket;
        stopped = false;
        while (!stop) {
            clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                Log.debug("New Client - " + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
                server.newMessage(0, CommunicationType.NewClient, clientSocket);
            } catch (IOException e) {
                if (stop) {
                    continue;
                }
                Log.error("Error accepting client connection" + e.getMessage());
            }            
        }        

        stopped = true;
        Log.debug("ServerConnection - stopped:true");
    }
}
