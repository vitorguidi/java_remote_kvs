package org.vguidi.redis.kvs;

public interface IKvs {
    public String get(String key);
    public void put(String key, String value);
}
