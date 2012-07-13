package network;

import java.io.Serializable;

public class Communication implements Serializable {

    CommunicationType reason; // reason for the contact
    Object obj;

    public Communication(CommunicationType reason, Object obj) {
        this.reason = reason;
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public CommunicationType getReason() {
        return reason;
    }

    public void setReason(CommunicationType reason) {
        this.reason = reason;
    }
}
