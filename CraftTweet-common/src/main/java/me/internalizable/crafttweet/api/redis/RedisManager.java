package me.internalizable.crafttweet.api.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.internalizable.crafttweet.api.redis.bus.RedisBus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class RedisManager {

    private final JedisPool jedisPool;
    private static Gson gson = new GsonBuilder().create();
    private RedisBus redisBus;

    public RedisManager(JedisPool jedisPool) {
        this.jedisPool = jedisPool;

        try(Jedis jedis = jedisPool.getResource()) {
            this.redisBus = new RedisBus(jedisPool);
        } catch(JedisException e) {
            //todo log
        }
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public RedisBus getRedisBus() {
        return redisBus;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        RedisManager.gson = gson;
    }

    public void clear() {
        redisBus.getListeners().clear();
        redisBus.getRegisteredChannels().clear();
    }

}