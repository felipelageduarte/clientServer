/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package felipelageduarte.br.Network;

import felipelageduarte.br.View.ClientServerView;

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
