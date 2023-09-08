package org.vguidi.redis.kvs;

import java.util.HashMap;

public class HashMapKVS implements IKvs {

    private HashMap<Byte[], Byte[]> store;

    public HashMapKVS() {
        store = new HashMap<>();
    }

    @Override
    public Byte[] get(Byte[] key) {
        return store.get(key);
    }

    @Override
    public void put(Byte[] key, Byte[] value) {
        store.put(key, value);
    }
}
