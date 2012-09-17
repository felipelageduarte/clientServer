package Server;

import NIOFramework.Buffer;
import Log.Log;
import NIOFramework.BufferFactory;
import NIOFramework.InputHandlerFactory;
import NIOFramework.NioDispatcher;
import NIOFramework.StandardAcceptor;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    private Executor executor;
    private BufferFactory bufFactory;
    private NioDispatcher dispatcher;
    private InputHandlerFactory factory;
    private StandardAcceptor acceptor;
    private static int instanceCount = 0;

    public Server() {
    }

    public boolean start(int port) {
        executor = Executors.newCachedThreadPool();
        bufFactory = new Buffer(1024);
        try {
            dispatcher = new NioDispatcher(executor, bufFactory);
        } catch (IOException ex) {
            Log.fatal("Server couldn't start", ex);
            return false;
        }
        factory = (InputHandlerFactory) new ServerProtocol();
        try {
            acceptor = new StandardAcceptor(port, dispatcher, factory);
        } catch (IOException ex) {
            Log.fatal("Server couldn't start", ex);
            dispatcher.shutdown();
            return false;
        }

        if (instanceCount == 0) {
            dispatcher.start();
            acceptor.newThread();
            Log.info("Server Started...");
            instanceCount++;
        } else {
            Log.error("Server is alredy running...");
        }
        return true;
    }

    public void shutdown() {
        if (instanceCount > 0) {
            dispatcher.shutdown();
            acceptor.shutdown();
            Log.error("Server has shutdown...");
            instanceCount--;
        } else {
            Log.error("There is no Server running...");
        }
    }
    
    public void sendMessage(){
        //this.dispatcher.
    }
}
