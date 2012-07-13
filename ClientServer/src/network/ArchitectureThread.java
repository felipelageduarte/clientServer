package network;

import java.util.concurrent.SynchronousQueue;

public class ArchitectureThread extends Thread {

    protected SynchronousQueue<Communication> requestQueue;
    protected InThread inThread;
    protected OutThread outThread;

    public ArchitectureThread() {
        requestQueue = new SynchronousQueue<Communication>();
    }

    public void addRequest(Communication request) {
        requestQueue.offer(request);
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
