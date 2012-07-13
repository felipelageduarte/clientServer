package network;

import Log.Log;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InThread extends Thread {

    private ObjectInputStream in;
    private ArchitectureThread architectureThread;
    private boolean stop;
    private boolean stopped;

    public InThread(ArchitectureThread architectureThread, ObjectInputStream in) {
        this.in = in;
        this.architectureThread = architectureThread;
        stop = false;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void shutdown() {
        stop = true;
        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                Log.error("Could not close InThread InputStream", ex);
            }
        }
    }

    @Override
    public void run() {
        this.stopped = false;
        Log.info("InThread running...");
        while (!stop) {
            try {
                architectureThread.addRequest(in.readObject());
            } catch (IOException ex) {
                stop = true;
                continue;
            } catch (ClassNotFoundException ex) {
                Log.error("Class not Found exception", ex);
            }
        }
        Log.info("InThread shuting down...");
        this.shutdown();
        this.stopped = true;
    }
}
