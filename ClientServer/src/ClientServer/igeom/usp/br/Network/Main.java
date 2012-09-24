/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientServer.igeom.usp.br.Network;

import ClientServer.igeom.usp.br.View.ClientServerView;

/**
 *
 * @author felipelageduarte
 */
public class Main {

    public static void main(String[] args) {        
        ClientServer clientServer = new ClientServer();
        new Thread(clientServer).start();
        ClientServerView view = new ClientServerView(clientServer);
        view.setVisible(true);       
    }
}
