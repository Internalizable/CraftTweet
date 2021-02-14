package me.internalizable.crafttweet;

import me.internalizable.crafttweet.api.redis.RedisManager;
import me.internalizable.crafttweet.api.redis.handlers.auth.OAuthReciever;
import me.internalizable.crafttweet.cache.LocalServerCache;
import me.internalizable.crafttweet.cmd.LinkCommand;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.events.CraftTweetJoin;
import me.internalizable.crafttweet.queue.RunningQueueManager;
import me.internalizable.crafttweet.queue.WaitingQueue;
import me.internalizable.crafttweet.sql.MySQL;
import me.internalizable.crafttweet.utils.BukkitUtils;
import me.internalizable.crafttweet.utils.StaticUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

public final class CraftTweetSpigot extends JavaPlugin {

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
            Bukkit.getPluginManager().disablePlugin(this);
        }

        MySQL sqlInstance = new MySQL(config);
        sqlInstance.init();

        LocalServerCache twitterCache = new LocalServerCache();
        BukkitUtils bukkitUtils = new BukkitUtils();

        Bukkit.getPluginManager().registerEvents(new CraftTweetJoin(this, config, twitterCache), this);

        getCommand("link").setExecutor(new LinkCommand(this, config, twitterCache));

        resetTime = (int) TimeUnit.HOURS.toSeconds(1);

        RunningQueueManager queueManager = new RunningQueueManager(config, twitterCache);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            resetTime--;

            if(resetTime <= 0) {
                resetTime = (int) TimeUnit.MINUTES.toSeconds(1);
                StaticUtils.getCache().forEach(twitterPlayer -> twitterPlayer.getData().setLimitCount(0));

                WaitingQueue queueRuntime = new WaitingQueue(twitterCache);
                queueRuntime.moveQueue();
            }

            if(!StaticUtils.isUpdating()) {
                twitterCache.setUpdateStatus(true);
                queueManager.readQueue();
            }


        },0L,20L);

        /**
         * Check if callback urls are enabled.
         */

        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

        RedisManager redisManager = new RedisManager(jedisPool);
        redisManager.getRedisBus().registerListener(new OAuthReciever(config, twitterCache, bukkitUtils));
        redisManager.getRedisBus().init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
