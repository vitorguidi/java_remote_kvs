package org.vguidi.redis;

import org.vguidi.redis.server.Server;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8081);
        server.serve();
    }
}