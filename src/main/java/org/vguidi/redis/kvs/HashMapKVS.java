package org.vguidi.redis.kvs;

import java.util.HashMap;

public class HashMapKVS implements IKvs {

    private HashMap<String, String> store;

    public HashMapKVS() {
        store = new HashMap<>();
    }

    @Override
    public String get(String key) {
        return store.get(key);
    }

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }
}
