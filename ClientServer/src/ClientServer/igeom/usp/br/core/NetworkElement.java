package ClientServer.igeom.usp.br.core;

import ClientServer.igeom.usp.br.protocol.ClientServerState;
import java.util.LinkedList;
import java.util.Observable;

public abstract class NetworkElement extends Observable implements Runnable {
    private final LinkedList<MessagePojo> messageQueue;
    protected InThread inThread;
    protected OutThread outThread;
    protected ClientServerState state;
    protected int type;
    public final static int SERVER = 0;
    public final static int CLIENT = 1;
    
    public NetworkElement() {
        messageQueue = new LinkedList<MessagePojo>();
    }

    public ClientServerState getState() {
        return state;
    }

    public void setState(ClientServerState state) {
        this.state = state;
    }

    public void addMessage(MessagePojo request) {
        synchronized (messageQueue) {
            messageQueue.addLast(request);
        }
    }

    public MessagePojo getIncommingMessage() throws InterruptedException {
        synchronized (messageQueue) {
            return messageQueue.pollFirst();
        }
    }

    void addMessage(Object request) {
        addMessage((MessagePojo) request);
    }

    public InThread getInThread() {
        return inThread;
    }

    public OutThread getOutThread() {
        return outThread;
    }
    
    public abstract void shutdown();
}
