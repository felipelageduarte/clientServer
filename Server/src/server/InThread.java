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
public class InThread {
    
    private ObjectInputStream in;

    public InThread(ObjectInputStream in) {
        this.in = in;
    }
    
    public Object readObject() throws IOException, ClassNotFoundException{
        return in.readObject();
    }
    
}
