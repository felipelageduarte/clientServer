/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientServer.igeom.usp.br.Protocol;

/**
 *
 * @author felipelageduarte
 */
public enum ClientServerState {
    CLIENT_RUNNING,
    CLIENT_STOPPED,
    CLIENT_CONNECTING,
    CLIENT_CONNECTED, 
    CLIENT_STOPPING,
    INCOMMING_DATA,
}
