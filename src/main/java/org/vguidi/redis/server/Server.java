package org.vguidi.redis.server;

import org.vguidi.redis.kvs.HashMapKVS;
import org.vguidi.redis.kvs.IKvs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private ServerSocketChannel serverSocket;
    private Selector selector;
    private IKvs store;
    int port;

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        this.store = new HashMapKVS();
    }

    public void serve() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            processAcceptedEvents(keyIterator);
        }
    }

    private void processAcceptedEvents(Iterator<SelectionKey> keyIterator) throws IOException {
        while(keyIterator.hasNext()) {
            SelectionKey key = (SelectionKey) keyIterator.next();
            keyIterator.remove();
            if (key.isAcceptable()) {
                handleAccept(key);
            }
            if(key.isReadable()) {
                try {
                    handleRead(key);
                }
                catch (IOException e) {
                    System.out.println("Lost connection to client.");
                    key.cancel();
                }
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sChannel = ssChannel.accept();
        sChannel.configureBlocking(false);
        sChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel destSocket = (SocketChannel) key.channel();
        String msg = readFromSocket(destSocket);
        String ans = handleCommand(msg);
        writeToSocket(ans, destSocket);
    }

    private String handleCommand(String msg) {
        String[] parts = msg.replaceAll("\n", "").split(" ");
        if (parts.length < 2 || parts.length > 3 ||  (!"GET".equals(parts[0]) && !"SET".equals(parts[0]))) {
            return "unrecognized command\n";
        }
        if ("GET".equals(parts[0])) {
            String key = parts[1];
            String val = store.get(key);
            return val == null ? "Key not found\n" : val + "\n";
        }
        else {
            String key = parts[1];
            String val = parts[2];
            store.put(key, val);
            return "OK\n";
        }
    }

    private String readFromSocket(SocketChannel sChannel) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(1024);
        int readBytes = sChannel.read(buff);
        if (readBytes > 0) {
            buff.flip();
            ByteBuffer copyBuffer = ByteBuffer.allocate(readBytes);
            copyBuffer.put(buff);
            return new String(copyBuffer.array());
        }
        return "No message content received";
    }

    private void writeToSocket(String msg, SocketChannel destSocket) throws IOException {
        ByteBuffer buff = ByteBuffer.wrap(msg.getBytes());
        destSocket.write(buff);
    }
}
