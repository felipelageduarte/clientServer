package Server;

import Log.Log;
import NIOServerFramework.ChannelFacade;
import NIOServerFramework.InputHandler;
import NIOServerFramework.InputHandlerFactory;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. 
 * User: ron 
 * Date: Mar 19, 2007 
 * Time: 3:22:29 PM
 * 
 * Modified by Felipe Duarte. 
 * email: felipelageduarte at gmail dot com 
 * Date: Jul 12, 2012
 */
public class Protocol implements InputHandlerFactory {

    Map<ChannelFacade, User> users;

    public Protocol() {
        this.users = Collections.synchronizedMap(new HashMap<ChannelFacade, User>());
    }

    // --------------------------------------------------
    // Implementation of InputHandlerFactory interface
    @Override
    public InputHandler newHandler() throws IllegalAccessException, InstantiationException {
        return new Handler(this);
    }

    // --------------------------------------------------
    void newUser(ChannelFacade facade) {
        User user = new User(facade);

        users.put(facade, user);
        user.send(ByteBuffer.wrap((user.getNickName() + "\n").getBytes()));
    }

    void endUser(ChannelFacade facade) {
        users.remove(facade);
    }

    public void handleMessage(ChannelFacade facade, ByteBuffer message) {
        broadcast(users.get(facade), message);
    }

    private void broadcast(User sender, ByteBuffer message) {
        synchronized (users) {
            for (User user : users.values()) {
                if (user != sender) {
                    sender.sendTo(user, message);
                }
            }
        }
    }

    // ----------------------------------------------------
    private static class User {

        private final ChannelFacade facade;
        private String nickName;
        private ByteBuffer prefix = null;
        private static int counter = 1;

        public User(ChannelFacade facade) {
            this.facade = facade;
            setNickName("nick-" + counter++);
            Log.debug("new user: " + nickName);
        }

        public void send(ByteBuffer message) {
            facade.outputQueue().enqueue(message.asReadOnlyBuffer());
        }

        public void sendTo(User recipient, ByteBuffer message) {
            recipient.send(prefix);
            recipient.send(message);
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;

            String prefixStr = "[" + nickName + "] ";

            prefix = ByteBuffer.wrap(prefixStr.getBytes());
        }
    }
}
