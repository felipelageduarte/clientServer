/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ch.unifr.nio.framework.transform.AbstractForwarder;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author felipelageduarte
 */
public class ServerTransformer extends AbstractForwarder<ByteBuffer, ByteBuffer> {

    @Override
    public void forward(ByteBuffer input) throws IOException {
        // echo all data we get...
        System.out.println("echoing " + input.remaining() + " bytes");
        nextForwarder.forward(input);
    }
}
