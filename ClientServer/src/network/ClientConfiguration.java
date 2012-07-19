/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 *
 * @author felipelageduarte
 */
public class ClientConfiguration {

    private String password;
    private String serverAddress;
    private Integer serverPort;

    public ClientConfiguration(String serverAddress, Integer serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public ClientConfiguration(String password, String serverAddress, Integer serverPort) {
        this.password = password;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
