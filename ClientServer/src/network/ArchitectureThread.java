package network;

import Log.Log;
import java.util.LinkedList;

public class ArchitectureThread extends Thread {

    private LinkedList<Communication> requestQueue;
    protected InThread inThread;
    protected OutThread outThread;

    public ArchitectureThread() {
        requestQueue = new LinkedList<Communication>();
    }

    public void addRequest(Communication request) {
        synchronized (requestQueue) {
            requestQueue.addLast(request);
        }
    }

    public Communication getIncommingRequest() throws InterruptedException {
        synchronized (requestQueue) {
            return requestQueue.pollFirst();
        }
    }

    void addRequest(Object request) {
        addRequest((Communication) request);
    }

    public InThread getInThread() {
        return inThread;
    }

    public OutThread getOutThread() {
        return outThread;
    }
}
