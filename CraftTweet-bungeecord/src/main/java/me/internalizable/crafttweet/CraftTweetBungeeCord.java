package me.internalizable.crafttweet;

import me.internalizable.crafttweet.api.redis.RedisManager;
import me.internalizable.crafttweet.api.redis.handlers.auth.OAuthReciever;
import me.internalizable.crafttweet.cache.LocalServerCache;
import me.internalizable.crafttweet.cmd.LinkCMD;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.events.CraftTweetJoin;
import me.internalizable.crafttweet.queue.RunningQueueManager;
import me.internalizable.crafttweet.queue.WaitingQueue;
import me.internalizable.crafttweet.redis.RedisServerCache;
import me.internalizable.crafttweet.redis.handlers.CacheHandler;
import me.internalizable.crafttweet.sql.MySQL;
import me.internalizable.crafttweet.utils.BungeeUtils;
import me.internalizable.crafttweet.utils.StaticUtils;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import sun.misc.Cache;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public final class CraftTweetBungeeCord extends Plugin {

    private int resetTime;

    @Override
    public void onEnable() {
        IConfig config = new IConfig();

        try {
            config.init(getDataFolder());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if(config.getPublicKey().equalsIgnoreCase("somePublicKey") || config.getPrivateKey().equalsIgnoreCase("somePrivateKey")) {
            System.out.println("Fatal OAuth error, please configure the file.");
        }

        MySQL sqlInstance = new MySQL(config);
        sqlInstance.init();

        LocalServerCache localCache = new LocalServerCache();
        BungeeUtils bungeeUtils = new BungeeUtils(this);

        RunningQueueManager queueManager = null;
        RedisServerCache redisServerCache = null;

        if(config.isCallback()) {
            JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
            RedisManager redisManager = new RedisManager(jedisPool);

            if(config.isRedisBungee()) {
                redisServerCache = new RedisServerCache(redisManager);

                redisManager.getRedisBus().registerListener(new OAuthReciever(config, redisServerCache, bungeeUtils));
                redisManager.getRedisBus().registerListener(new CacheHandler(this, config, localCache));

                queueManager = new RunningQueueManager(config, redisServerCache);

                getProxy().getPluginManager().registerListener(this, new CraftTweetJoin(redisServerCache, config));
                getProxy().getPluginManager().registerCommand(this, new LinkCMD(this, config, redisServerCache));
            } else {
                redisManager.getRedisBus().registerListener(new OAuthReciever(config, localCache, bungeeUtils));

                queueManager = new RunningQueueManager(config, localCache);

                getProxy().getPluginManager().registerListener(this, new CraftTweetJoin(localCache, config));
                getProxy().getPluginManager().registerCommand(this, new LinkCMD(this, config, localCache));
            }

            redisManager.getRedisBus().init();

        } else if(config.isRedisBungee()) {
            JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
            RedisManager redisManager = new RedisManager(jedisPool);

            redisServerCache = new RedisServerCache(redisManager);

            redisManager.getRedisBus().registerListener(new CacheHandler(this, config, localCache));
            redisManager.getRedisBus().init();

            queueManager = new RunningQueueManager(config, redisServerCache);

            getProxy().getPluginManager().registerListener(this, new CraftTweetJoin(redisServerCache, config));
            getProxy().getPluginManager().registerCommand(this, new LinkCMD(this, config, redisServerCache));
        } else {
            queueManager = new RunningQueueManager(config, localCache);
            getProxy().getPluginManager().registerListener(this, new CraftTweetJoin(localCache, config));
            getProxy().getPluginManager().registerCommand(this, new LinkCMD(this, config, localCache));
        }


        RedisServerCache finalRedisServerCache = redisServerCache;
        RunningQueueManager finalQueueManager = queueManager;

        resetTime = (int) TimeUnit.HOURS.toSeconds(1);

        getProxy().getScheduler().schedule(this, () -> {
            resetTime--;

            if(resetTime <= 0) {
                resetTime = (int) TimeUnit.HOURS.toSeconds(1);
                StaticUtils.getCache().forEach(twitterPlayer -> twitterPlayer.getData().setLimitCount(0));

                WaitingQueue queueRuntime;

                if(config.isRedisBungee())
                    queueRuntime = new WaitingQueue(finalRedisServerCache);
                else
                    queueRuntime = new WaitingQueue(localCache);

                queueRuntime.moveQueue();
            }

            if(!StaticUtils.isUpdating()) {
                localCache.setUpdateStatus(true);
                finalQueueManager.readQueue();
            }

        },0L, 1L, TimeUnit.SECONDS);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
