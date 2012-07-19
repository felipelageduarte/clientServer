package network;

public class ServerConfiguration {

    private Boolean passwordRequired;
    private String password;
    private Boolean enableClientEdition;
    private int port;

    public ServerConfiguration(int port) {
        this.password = "";
        this.passwordRequired = false;
        this.enableClientEdition = false;
        this.port = port;
    }

    public ServerConfiguration(String password, Boolean enableClientEdition, int port) {
        this.password = password;
        this.passwordRequired = true;
        this.enableClientEdition = enableClientEdition;
        this.port = port;
    }

    public ServerConfiguration(String password, int port) {
        this.password = password;
        this.passwordRequired = true;
        this.enableClientEdition = false;
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
}
