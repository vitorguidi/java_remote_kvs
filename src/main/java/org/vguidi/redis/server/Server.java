package org.vguidi.redis.server;

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
    int port;

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
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
        writeToSocket(msg, destSocket);
    }

    private String readFromSocket(SocketChannel sChannel) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(1024);
        int readBytes = sChannel.read(buff);
        if (readBytes > 0) {
            buff.flip();
            return new String(buff.array());
        }
        return "No message content received";
    }

    private void writeToSocket(String msg, SocketChannel destSocket) throws IOException {
        ByteBuffer buff = ByteBuffer.wrap(msg.getBytes());
        destSocket.write(buff);
    }
}
