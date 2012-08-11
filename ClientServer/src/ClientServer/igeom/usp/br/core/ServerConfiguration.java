package ClientServer.igeom.usp.br.core;

public class ServerConfiguration{

    private Boolean passwordRequired;
    private String password;
    private Boolean enableClientEdition;
    private int port;
    private Boolean confirmConnection;

    public ServerConfiguration(int port) {
        this.password = "";
        this.passwordRequired = false;
        this.enableClientEdition = false;
        this.confirmConnection = false;
        this.port = port;
    }

    public ServerConfiguration(Boolean passwordRequired, String password, Boolean enableClientEdition, int port, Boolean confirmConnection) {
        this.passwordRequired = passwordRequired;
        this.password = password;
        this.enableClientEdition = enableClientEdition;
        this.port = port;
        this.confirmConnection = confirmConnection;
    }

    public ServerConfiguration(String password, int port) {
        this.password = password;
        this.passwordRequired = true;
        this.enableClientEdition = false;
        this.confirmConnection = false;
        this.port = port;
    }

    public Boolean getEnableClientEdition() {
        return enableClientEdition;
    }

    public void setEnableClientEdition(Boolean enableClientEdition) {
        this.enableClientEdition = enableClientEdition;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        if (password.isEmpty()) {
            this.passwordRequired = false;
        } else {
            this.passwordRequired = true;
        }
    }

    public boolean isPasswordRequired() {
        return passwordRequired;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConfirmConnection() {
        return confirmConnection;
    }

    public void setConfirmConnection(Boolean confirmConnection) {
        this.confirmConnection = confirmConnection;
    }
}
