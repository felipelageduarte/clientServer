///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
package Client;
//
//import Log.Log;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author felipelageduarte
// */
//public class Client2 {
//
//    private static int instanceCount = 0;
//    private NioClient client;
//
//    public Client2() {
//    }
//
//    public boolean start(String address, int port) {
//        try {
//            NioClient client = new NioClient(InetAddress.getByName(address), 80);
//            Thread t = new Thread(client);
//            t.setDaemon(true);
//            t.start();
//            RspHandler handler = new RspHandler();
//            client.send("GET / HTTP/1.0\r\n\r\n".getBytes(), handler);
//            handler.waitForResponse();
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(Client2.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        } catch (IOException ex) {
//            Log.fatal("Server couldn't start", ex);
//            return false;
//        }
//        return true;
//    }
//
//    public void shutdown() {
//        //lient.
//    }
//}

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

public class Client2 {

    public boolean start(String address, int port) {
        InetSocketAddress ISA = null;
        SocketChannel clientNIO = null;
        Selector selector = null;
        SelectionKey key = null;

        try {
            clientNIO = SocketChannel.open();
            clientNIO.configureBlocking(false);
            InetAddress addr = InetAddress.getByName(address);
            ISA = new InetSocketAddress(addr, port);
            clientNIO.connect(ISA);
            selector = Selector.open();
            SelectionKey clientKey = clientNIO.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        try {
            while (selector.select(1000) > 0) {
                Set keys = selector.selectedKeys();
                Iterator iter = keys.iterator();
                while (iter.hasNext()) {
                    key = (SelectionKey) iter.next();
                    iter.remove();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if ((key.isValid()) && (key.isConnectable())) {
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }
                        ByteBuffer serverBuf = null;
                        System.out.println("Connected...");
                        while (true) {
                            serverBuf = ByteBuffer.wrap(new String("Answer me dear server ...").getBytes());
                            channel.write(serverBuf);
                            serverBuf.clear();
                            ByteBuffer clientBuf = ByteBuffer.allocateDirect(1024);
                            clientBuf.clear();
                            channel.read(clientBuf);
                            clientBuf.flip();
                            Charset charset = Charset.forName("ISO-8859-1");
                            CharsetDecoder decoder = charset.newDecoder();
                            CharBuffer charBuffer = decoder.decode(clientBuf);
                            System.out.println(charBuffer.toString());
                            clientBuf.clear();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                key.channel().close();
                key.cancel();
            } catch (Exception ex) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }
}