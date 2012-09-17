package Server;

import ch.unifr.nio.framework.*;
import ch.unifr.nio.framework.transform.AbstractForwarder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * This server just echos every data it receives.
 */
public class Server2 extends AbstractAcceptor {

    private final static Logger logger =
            Logger.getLogger(Server2.class.getName());
    /**
     * creates a new EchoServer
     * @param dispatcher the central NIO dispatcher
     * @param socketAddress the address where to listen to
     * @throws java.io.IOException if an I/O exception occurs
     */
    public Server2(Dispatcher dispatcher, SocketAddress socketAddress)
            throws IOException {
        super(dispatcher, socketAddress);
    }

    /**
     * starts the EchoServer
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        if (args.length != 1) {
//            System.out.println("Usage: EchoServer <port>");
//            System.exit(1);
//        }

        try {
            // start NIO Framework
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.start();

            // start EchoServer
            int port = 8080;// Integer.parseInt(args[0]);
            SocketAddress socketAddress = new InetSocketAddress(port);
            Server2 echoServer = new Server2(dispatcher, socketAddress);
            echoServer.start();
            System.out.println("EchoServer is running at port " + port + "...");

        } catch (Exception ex) {
            FrameworkTools.handleStackTrace(logger, ex);
        }
    }

    @Override
    protected ChannelHandler getHandler(SocketChannel socketChannel) {
        return new EchoChannelHandler();
    }

    private class EchoChannelHandler extends AbstractChannelHandler {

        public EchoChannelHandler() {
            // set up I/O transformers
            EchoServerTransformer echoServerTransformer =
                    new EchoServerTransformer();
            reader.setNextForwarder(echoServerTransformer);
            echoServerTransformer.setNextForwarder(writer);
        }

        @Override
        public void inputClosed() {
            System.out.println("EchoClient closed the connection");
            try {
                handlerAdapter.closeChannel();
            } catch (IOException ex) {
                FrameworkTools.handleStackTrace(logger, ex);
            }
        }

        @Override
        public void channelException(Exception exception) {
            System.out.println("Exception on channel: " + exception);
            try {
                handlerAdapter.closeChannel();
            } catch (IOException ex) {
                FrameworkTools.handleStackTrace(logger, ex);
            }
        }
    }

    private class EchoServerTransformer
            extends AbstractForwarder<ByteBuffer, ByteBuffer> {

        @Override
        public void forward(ByteBuffer input) throws IOException {
            // echo all data we get...
            System.out.println("echoing " + input.remaining() + " bytes");
            nextForwarder.forward(input);
        }
    }
}

