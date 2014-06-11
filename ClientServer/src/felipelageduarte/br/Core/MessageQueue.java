/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package felipelageduarte.br.Core;

import felipelageduarte.br.Network.CommunicationType;
import felipelageduarte.br.Network.MessagePojo;
import java.util.LinkedList;

public class MessageQueue {
    protected final LinkedList<MessagePojo> queue = new LinkedList<MessagePojo>();
    
    public void newMessage(MessagePojo message) {
        synchronized (queue) {
            queue.offer(message);
        }
    }
    
    public void newMessage(Integer whoSending, CommunicationType reason, Object request) {
        newMessage(new MessagePojo(whoSending, reason, request));        
    }

    private MessagePojo getMessage() throws InterruptedException {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }
}
