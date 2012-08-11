/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientServer.igeom.usp.br.core;

/**
 *
 * @author felipelageduarte
 */
public class ClientConfiguration {

    private String password;
    private String nickName;
    private String serverAddress;
    private Integer serverPort;

    public ClientConfiguration(String serverAddress, Integer serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nickName = "";
    }

    public ClientConfiguration(String password, String nickName, String serverAddress, Integer serverPort) {
        this.password = password;        
        this.nickName = nickName;        
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
