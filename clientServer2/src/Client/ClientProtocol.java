package Client;

import NIOFramework.ChannelFacade;
import NIOFramework.InputHandler;
import NIOFramework.InputHandlerFactory;
import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA. User: ron Date: Mar 19, 2007 Time: 3:22:29 PM
 *
 * Modified by Felipe Duarte. email: felipelageduarte at gmail dot com Date: Jul
 * 12, 2012
 */
public class ClientProtocol implements InputHandlerFactory {

    public ClientProtocol() {
    }

    @Override
    public InputHandler newHandler() throws IllegalAccessException, InstantiationException {
       return new ClientHandler(this);
    }

    void handleMessage(ChannelFacade channelFacade, ByteBuffer message) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
