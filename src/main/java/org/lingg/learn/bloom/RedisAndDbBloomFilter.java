package org.lingg.learn.bloom;

import redis.clients.jedis.Jedis;

import java.util.Objects;

public class RedisAndDbBloomFilter {
    private String nameSpace;
    private Jedis jedis;
    private int fixSize;

    public RedisAndDbBloomFilter(String nameSpace, Jedis jedis, int fixSize) {
        this.nameSpace = nameSpace;
        this.jedis = jedis;
        this.fixSize = fixSize;
    }

    private int getHash(String key) {
        return key.hashCode();
    }

    private void addToDb(int hash, String key) {
        //TODO 将记录保存至db
    }

    private boolean containsFromDb(int hash, String key) {
        //TODO 根据 hash key 查询数据库是否存在
        return false;
    }

    /**
     * 根据hash和fixSize 算出bitKey
     *
     * @param hash
     * @return
     */
    private String getBitKey(int hash) {
        int bitKeyIndex = hash / fixSize;
        String bitKey = nameSpace + bitKeyIndex;
        return bitKey;
    }

    /**
     * 判断给定的key是否存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        //TODO 如果是集群模式这里需要分布式锁，如果是单机这里需要线程锁
        Objects.requireNonNull(key, "the key must not be null");
        int hash = getHash(key);
        int offset = hash % fixSize;
        String bitKey = getBitKey(hash);
        Boolean result = jedis.getbit(bitKey, offset);
        if (result.booleanValue() && containsFromDb(hash, key)) {
            return true;
        }
        return false;
    }

    /**
     * 根据给定的key添加一个过滤记录
     *
     * @param key
     */
    public void addRecord(String key) {
        //TODO 如果是集群模式这里需要分布式锁，如果是单机这里需要线程锁
        int hash = getHash(key);
        int offset = hash % fixSize;
        String bitKey = getBitKey(hash);
        jedis.setbit(bitKey, offset, true);
        addToDb(hash, key);
    }
}
