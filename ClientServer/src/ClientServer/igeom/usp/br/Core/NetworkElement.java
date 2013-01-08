package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Network.CommunicationType;
import ClientServer.igeom.usp.br.Network.MessagePojo;
import java.util.LinkedList;
import java.util.Observable;

public abstract class NetworkElement extends Observable implements Runnable {

    private LinkedList<MessagePojo> queue = null;
    protected InThread inThread = null;
    protected OutThread outThread = null;
    protected int type = -1;
    public final static int SERVER = 0;
    public final static int CLIENT = 1;

    public NetworkElement() {
        queue = new LinkedList<MessagePojo>();
    }

    public void newMessage(MessagePojo message) {
        synchronized (queue) {
            queue.offer(message);
        }
    }
    
    public void newMessage(Integer whoSending, CommunicationType reason, Object request) {
        newMessage(new MessagePojo(whoSending, reason, request));        
    }

    protected MessagePojo getMessage() throws InterruptedException {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }

    public abstract void shutdown();
}
