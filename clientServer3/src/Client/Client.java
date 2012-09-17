package Client;

import ch.unifr.nio.framework.*;
import ch.unifr.nio.framework.transform.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

/**
 * An echo client application that demonstrates how to connect to a server
 * in a non-blocking way.
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class Client extends AbstractClientSocketChannelHandler {

    private static final Logger logger =
            Logger.getLogger(Client.class.getName());
    private final String host;
    private final Lock lock = new ReentrantLock();
    private final Condition inputArrived = lock.newCondition();

    /** Creates a new instance of Client
     * @param host the host name of the server
     * @param port the port of the server
     */
    public Client(String host, int port) {
        this.host = host;

        // setup input transformation chain:
        // reader -> byteBufferToString -> Client
        ByteBufferToStringTransformer byteBufferToStringTransformer =
                new ByteBufferToStringTransformer();
        byteBufferToStringTransformer.setNextForwarder(
                new ClientTransformer());
        reader.setNextForwarder(byteBufferToStringTransformer);

        try {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.start();
            dispatcher.registerClientSocketChannelHandler(host, port, this);

            // The NIO Framework only starts daemon threads. If we do not sleep
            // here the application would drop out too early.
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            FrameworkTools.handleStackTrace(logger, ex);
        } catch (IOException ex) {
            FrameworkTools.handleStackTrace(logger, ex);
        }
    }

    /**
     * starts the Client
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        if (args.length != 2) {
//            System.out.println(
//                    "Usage: Client <server host> <server port>");
//            System.exit(1);
//        }
        new Client("localhost",8080);//args[0], Integer.parseInt(args[1]));
    }

    @Override
    public void inputClosed() {
        System.out.println("EchoServer closed the connection");
        System.exit(1);
    }

    @Override
    public void channelException(Exception exception) {
        System.out.println("Connection error " + exception);
        System.exit(1);
    }

    @Override
    public void resolveFailed() {
        System.out.println("Could not resolve \"" + host + "\"");
        System.exit(1);
    }

    @Override
    public void connectSucceeded() {
        new OutputHandler().start();
    }

    @Override
    public void connectFailed(IOException exception) {
        FrameworkTools.handleStackTrace(logger, exception);
        System.exit(1);
    }

    private class OutputHandler extends Thread {

        private final StringToByteBufferTransformer transformer;

        public OutputHandler() {
            setDaemon(false);

            // setup output transformation chain:
            // stringToByteBuffer -> writer
            transformer = new StringToByteBufferTransformer();
            transformer.setNextForwarder(writer);
        }

        @Override
        public void run() {
            // send all user input to echo server
            System.out.println("Client is running...");
            try {
                InputStreamReader streamReader =
                        new InputStreamReader(System.in);
                BufferedReader stdIn = new BufferedReader(streamReader);
                while (true) {
                    System.out.print("Your input: ");
                    String userInput = stdIn.readLine();
                    if (userInput.length() == 0) {
                        continue;
                    }
                    System.out.println("sending \"" + userInput + "\"");
                    transformer.forward(userInput);
                    // wait until we get an echo from the server...
                    lock.lock();
                    try {
                        inputArrived.await();
                    } catch (InterruptedException ex) {
                        FrameworkTools.handleStackTrace(logger, ex);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException ex) {
                FrameworkTools.handleStackTrace(logger, ex);
            }
        }
    }

    private class ClientTransformer
            extends AbstractForwarder<String, Void> {

        @Override
        public void forward(String input) throws IOException {
            // print out incoming string
            System.out.println("received \"" + input + "\"");

            // signal that input has arrived
            lock.lock();
            try {
                inputArrived.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}


