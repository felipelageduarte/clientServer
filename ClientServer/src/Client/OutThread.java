/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.ObjectOutputStream;

/**
 *
 * @author felipelageduarte
 */
public class OutThread implements Runnable{

    private ObjectOutputStream out;

    public OutThread(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        
    }
}
