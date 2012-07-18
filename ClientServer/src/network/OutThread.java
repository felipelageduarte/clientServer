package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutThread extends Thread {

    private ObjectOutputStream out;
    private Boolean stop;
    private boolean stopped;
    protected LinkedList<Communication> sendQueue;

    public OutThread(ObjectOutputStream out) {
        this.sendQueue = new LinkedList<Communication>();
        this.out = out;
        stop = false;
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

    public void addRequest(CommunicationType reason, Object request) {
        synchronized (sendQueue) {
            sendQueue.offer(new Communication(reason, request));
        }
    }

    private Communication getIncommingRequest() throws InterruptedException {
        synchronized (sendQueue) {
            return sendQueue.pollFirst();
        }
    }

    @Override
    public void run() {
        this.stopped = false;
        Log.info("OutThread running...");
        Object obj;

        while (!isStop()) {
            try {
                //busy wait for incomming request from client
                while ((obj = getIncommingRequest()) == null) {
                    ServerThread.sleep(100);
                    if (isStop()) {
                        break;
                    }
                }
                if (!isStop()) {
                    try {
                        out.writeObject(obj);
                    } catch (IOException ex) {
                        shutdown();
                        continue;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Log.info("OutThread shuting down...");

        if (out != null) {
            try {
                Log.debug("Closing out socket;");
                out.close();
                Log.debug("Closed out socket;");
            } catch (IOException ex) {
                Log.error("Out Thread coud not close Output Stream", ex);
            }
        }



        this.stopped = true;
    }
}
