package felipelageduarte.br.Core;

import felipelageduarte.br.Log.Log;
import felipelageduarte.br.Network.CommunicationType;
import felipelageduarte.br.Network.MessagePojo;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutThread extends Thread {

    private ObjectOutputStream out;
    private Boolean stop;
    private boolean stopped;
    protected final LinkedList<MessagePojo> queue;

    public OutThread(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.queue = new LinkedList<MessagePojo>();
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

    public void newMessage(MessagePojo message) {
        synchronized (queue) {
            queue.offer(message);
        }
    }
    
    public void newMessage(Integer whoSending, CommunicationType reason, Object request) {
        newMessage(new MessagePojo(whoSending, reason, request));        
    }

    private MessagePojo getMessage() throws InterruptedException {
        synchronized (queue) {
            return queue.pollFirst();
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
                while ((obj = getMessage()) == null) {
                    Thread.sleep(100);
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
        Log.debug("OutThread - stopped:true");
    }
}
