package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutThread extends Thread {

    private ObjectOutputStream out;
    private boolean stop;
    private boolean stopped;
    protected SynchronousQueue<Communication> sendQueue;

    public OutThread(ObjectOutputStream out) {
        this.sendQueue = new SynchronousQueue<Communication>();
        this.out = out;
        this.stop = false;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        stop = true;
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                Log.error("Out Thread coud not close Output Stream", ex);
            }
        }
    }

    void addRequest(CommunicationType reason, Object request) {
        sendQueue.offer(new Communication(reason, request));
    }

    @Override
    public void run() {
        this.stopped = false;
        Log.info("OutThread running...");
        Object obj;

        while (!stop) {
            try {
                //busy wait for incomming request from client
                while ((obj = sendQueue.take()) == null && !stop) {
                    ServerThread.sleep(100);
                }
                if (!stop) {
                    try {
                        out.writeObject(obj);
                    } catch (IOException ex) {                        
                        stop = true;
                        continue;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Log.info("OutThread shuting down...");
        this.shutdown();
        this.stopped = true;
    }
}
