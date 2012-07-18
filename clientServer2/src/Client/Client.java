package Client;

import Log.Log;
import NIOFramework.*;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Client {

    private Executor executor;
    private BufferFactory bufFactory;
    private NioDispatcher dispatcher;
    private InputHandlerFactory factory;
    private StandardConnector connector;
    private static int instanceCount = 0;

    public Client() {
    }

    public boolean start(String address, int port) {

        executor = Executors.newCachedThreadPool();
        bufFactory = new Buffer(1024);
        try {
            dispatcher = new NioDispatcher(executor, bufFactory);
        } catch (IOException ex) {
            Log.fatal("Client couldn't start", ex);
            return false;
        }
        factory = (InputHandlerFactory) new ClientProtocol();

        try {
            connector = new StandardConnector(dispatcher, factory);
            if (instanceCount == 0) {
                connector.connect(address, port);
                dispatcher.start();
                Log.info("Client Started...");
                instanceCount++;
            } else {
                Log.error("Client is alredy running...");
            }
        } catch (IOException ex) {
            Log.fatal("Client couldn't start", ex);
            dispatcher.shutdown();
            return false;
        }

        return true;
    }

    public void shutdown() {
        if (instanceCount > 0) {
            dispatcher.shutdown();
            connector.shutdown();
            Log.error("Client has shutdown...");
            instanceCount--;
        } else {
            Log.error("There is no Client running...");
        }
    }

    public boolean sendMessage(Object obj) {
        
        return true;
    }
}
