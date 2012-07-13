package Server;

import NIOServerFramework.ChannelFacade;
import NIOServerFramework.InputHandler;
import NIOServerFramework.InputQueue;
import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA. User: ron Date: Apr 8, 2006 Time: 5:49:36 PM
 */
public class Handler implements InputHandler {

    private final Protocol protocol;

    public Handler(Protocol protocol) {
        this.protocol = protocol;
    }

    // --------------------------------------------------------
    // Implementation of the InputHandler interface
    @Override
    public ByteBuffer nextMessage(ChannelFacade channelFacade) {
        InputQueue inputQueue = channelFacade.inputQueue();
        int nlPos = inputQueue.indexOf((byte) '\n');

        if (nlPos == -1) {
            return null;
        }

        if ((nlPos == 1) && (inputQueue.indexOf((byte) '\r') == 0)) {
            inputQueue.discardBytes(2);	// eat CR/NL by itself
            return null;
        }

        return (inputQueue.dequeueBytes(nlPos + 1));
    }

    @Override
    public void handleInput(ByteBuffer message, ChannelFacade channelFacade) {
        protocol.handleMessage(channelFacade, message);
    }

    @Override
    public void starting(ChannelFacade channelFacade) {
        System.out.println("NadaHandler: starting");
    }

    @Override
    public void started(ChannelFacade channelFacade) {
        protocol.newUser(channelFacade);
    }

    @Override
    public void stopping(ChannelFacade channelFacade) {
        System.out.println("NadaHandler: stopping");
    }

    @Override
    public void stopped(ChannelFacade channelFacade) {
        protocol.endUser(channelFacade);
    }
}
