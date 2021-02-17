package me.internalizable.crafttweet;

import com.google.common.io.Files;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import me.internalizable.crafttweet.api.redis.RedisManager;
import me.internalizable.crafttweet.api.redis.handlers.auth.OAuthReciever;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.cmd.LinkCMD;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.data.FlatDataStorage;
import me.internalizable.crafttweet.data.IStorageData;
import me.internalizable.crafttweet.data.MySQLStorage;
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class CraftTweetBungeeCord extends Plugin {

    private int resetTime;

    private IConfig config;

    @Override
    public void onEnable() {
        config = new IConfig();

        try {
            config.init(getDataFolder());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if(config.getPublicKey().equalsIgnoreCase("somePublicKey") || config.getPrivateKey().equalsIgnoreCase("somePrivateKey")) {
            System.out.println("Fatal OAuth error, please configure the file.");
        }

        ITwitterCache localCache = new ITwitterCache();
        BungeeUtils bungeeUtils = new BungeeUtils(this);

        IStorageData storageData;

        if(config.isSQL()) {
            storageData = new MySQLStorage();

            MySQL sqlInstance = new MySQL(config);
            sqlInstance.init();
        } else {
            storageData = new FlatDataStorage();

            File jsonFile = new File(getDataFolder() + "/data.json");

            try {
                jsonFile.createNewFile();
                StaticUtils.populateDataArray(getDataFolder() + "/data.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(config.isCallback() || config.isRedisBungee()) {
            JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
            RedisManager redisManager = new RedisManager(jedisPool);

            if(config.isRedisBungee()) {
                localCache = new RedisServerCache(redisManager);

                if(config.isCallback())
                    redisManager.getRedisBus().registerListener(new OAuthReciever(config, localCache, bungeeUtils, storageData));

                redisManager.getRedisBus().registerListener(new CacheHandler(this, config, localCache, storageData));
            } else {
                redisManager.getRedisBus().registerListener(new OAuthReciever(config, localCache, bungeeUtils, storageData));
            }

            redisManager.getRedisBus().init();
        }

        RunningQueueManager queueManager = new RunningQueueManager(config, localCache);
        getProxy().getPluginManager().registerListener(this, new CraftTweetJoin(localCache, config, storageData));
        getProxy().getPluginManager().registerCommand(this, new LinkCMD(this, config, localCache, storageData));

        resetTime = (int) TimeUnit.HOURS.toSeconds(1);

        ITwitterCache finalLocalCache = localCache;

        getProxy().getScheduler().schedule(this, () -> {
            resetTime--;

            if(resetTime <= 0) {
                resetTime = (int) TimeUnit.HOURS.toSeconds(1);
                StaticUtils.getCache().forEach(twitterPlayer -> twitterPlayer.getData().setLimitCount(0));

                WaitingQueue queueRuntime;

                queueRuntime = new WaitingQueue(finalLocalCache);

                queueRuntime.moveQueue();
            }

            if(!StaticUtils.isUpdating()) {
                finalLocalCache.setUpdateStatus(true);
                queueManager.readQueue();
            }

        },0L, 1L, TimeUnit.SECONDS);

    }

    @Override
    public void onDisable() {
        if(!config.isSQL())
            StaticUtils.saveDataArray();
    }
}
