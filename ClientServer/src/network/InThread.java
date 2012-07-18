package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InThread extends Thread {

    private ObjectInputStream in;
    private ArchitectureThread architectureThread;
    private Boolean stop;
    private boolean stopped;

    public InThread(ArchitectureThread architectureThread, ObjectInputStream in) {
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
        Communication request;
        while (!isStop()) {
            try {
                request = (Communication) in.readObject();
                architectureThread.addRequest(request);
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
