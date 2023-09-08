package org.vguidi.redis.parser;

import org.vguidi.redis.commands.ICommand;
import org.vguidi.redis.kvs.IKvs;

public class RedisParser {
    private IKvs kvs;

    public void Parser(IKvs store) {
        this.kvs = store;
    }
    public ICommand parse(byte[] data) {
        return null;
    }
}
