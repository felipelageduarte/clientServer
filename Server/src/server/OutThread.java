/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.ObjectOutputStream;

/**
 *
 * @author felipelageduarte
 */
public class OutThread {

    private ObjectOutputStream out;

    public OutThread(ObjectOutputStream out) {
        this.out = out;
    }
}
