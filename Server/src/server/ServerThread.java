/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author felipelageduarte
 */
public class ServerThread implements Runnable {

    private Socket clientSocket;
    private int index;
    private InThread inThread;
    private OutThread outThread;

    ServerThread(Socket clientSocket, int index) throws IOException {
        this.clientSocket = clientSocket;
        this.index = index;                
    }

    public int getIndex() {
        return index;
    }
    
    private boolean connectionChallenge() throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        
        Double numberReceived = in.readDouble();
        out.writeDouble(ConnectChallenge.calc(numberReceived));
        Double challengeNumber = ConnectChallenge.getNumber();
        out.writeDouble(challengeNumber);
        Double ChalengeNumberReceived = in.readDouble();
        if(Math.abs(ChalengeNumberReceived - ConnectChallenge.calc(challengeNumber)) < Math.pow(10, -5)){
            inThread = new InThread(in);
            outThread = new OutThread(out);
        }
        
        
        
        
        
        return true;
    }

    @Override
    public void run() {
        
        boolean acceptConnection = connectionChallenge();
        
    }
}
