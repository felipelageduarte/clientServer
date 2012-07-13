/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Log.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author felipelageduarte
 */
public class Client {

    private static int instanceCount = 0;
    private NioClient client;

    public Client() {
    }

    public boolean start(String address, int port) {
        try {
            NioClient client = new NioClient(InetAddress.getByName(address), 80);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
            RspHandler handler = new RspHandler();
            client.send("GET / HTTP/1.0\r\n\r\n".getBytes(), handler);
            handler.waitForResponse();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Log.fatal("Server couldn't start", ex);
            return false;
        }
        return true;
    }

    public void shutdown() {
        //lient.
    }
}
