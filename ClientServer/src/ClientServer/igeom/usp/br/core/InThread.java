package ClientServer.igeom.usp.br.core;

import ClientServer.igeom.usp.br.Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InThread extends Thread {

    private ObjectInputStream in;
    private NetworkElement architectureThread;
    private Boolean stop;
    private boolean stopped;

    public InThread(NetworkElement architectureThread, ObjectInputStream in) {
        this.in = in;
        this.architectureThread = architectureThread;
        stop = false;
    }

    public boolean isStopped() {
        return stopped;
    }
    
    private Boolean isStop() {
        synchronized (stop) {
            return stop;
        }
    }

    public void shutdown() {
        synchronized (stop) {
            stop = true;
        }
    }

    @Override
    public void run() {
        this.stopped = false;
        Log.info("InThread running...");
        MessagePojo request;
        while (!isStop()) {
            try {
                request = (MessagePojo) in.readObject();
                architectureThread.addMessage(request);
            } catch (IOException ex) {
                shutdown();
            } catch (ClassNotFoundException ex) {
                Log.error("Class not Found exception", ex);
            }
        }
        
        Log.info("InThread shuting down...");
        if (in != null) {
            try {
                Log.debug("Closing in socket;");
                in.close();
                Log.debug("Closed in socket;");
            } catch (IOException ex) {
                Log.error("Could not close InThread InputStream", ex);
            }
        }
        this.stopped = true;
    }
}
