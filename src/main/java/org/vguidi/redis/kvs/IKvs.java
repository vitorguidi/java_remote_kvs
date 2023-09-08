package org.vguidi.redis.kvs;

interface IKvs {
    public Byte[] get(Byte[] key);
    public void put(Byte[] key, Byte[] value);
}
