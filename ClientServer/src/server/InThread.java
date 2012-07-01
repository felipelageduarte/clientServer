/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author felipelageduarte
 */
public class InThread implements Runnable{
    
    private ObjectInputStream in;

    public InThread(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        
    }
    
}
