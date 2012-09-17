package Server;

import ch.unifr.nio.framework.AbstractAcceptor;
import ch.unifr.nio.framework.ChannelHandler;
import ch.unifr.nio.framework.Dispatcher;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class NIOServer extends AbstractAcceptor {

    public NIOServer(Dispatcher dispatcher, SocketAddress socketAddress) throws IOException {
        super(dispatcher, socketAddress);
    }

    @Override
    protected ChannelHandler getHandler(SocketChannel socketChannel) {
        return new ChannelHandler();
    }

}
