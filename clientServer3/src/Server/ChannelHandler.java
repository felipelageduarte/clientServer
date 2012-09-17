/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ch.unifr.nio.framework.AbstractChannelHandler;
import java.io.IOException;

/**
 *
 * @author felipelageduarte
 */
public class ChannelHandler extends AbstractChannelHandler {

    public ChannelHandler() {
        // set up I/O transformers
        ServerTransformer serverTransformer =  new ServerTransformer();
        reader.setNextForwarder(serverTransformer);
        serverTransformer.setNextForwarder(writer);
    }

    @Override
    public void inputClosed() {
        System.out.println("EchoClient closed the connection");
        try {
            handlerAdapter.closeChannel();
        } catch (IOException ex) {
            //FrameworkTools.handleStackTrace(logger, ex);
        }
    }

    @Override
    public void channelException(Exception exception) {
        System.out.println("Exception on channel: " + exception);
        try {
            handlerAdapter.closeChannel();
        } catch (IOException ex) {
            //FrameworkTools.handleStackTrace(logger, ex);
        }
    }

    
}
