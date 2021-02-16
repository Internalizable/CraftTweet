package me.internalizable.crafttweet.redis;

import me.internalizable.crafttweet.api.redis.RedisManager;
import me.internalizable.crafttweet.api.redis.bus.payload.Payload;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.utils.StaticUtils;

import java.sql.Timestamp;

public class RedisServerCache extends ITwitterCache {

    private RedisManager redisManager;

    public RedisServerCache(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public void addWaitingPlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getWaitingCache().add(twitterPlayer);

        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    @Override
    public void addActivePlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getCache()::remove);
        StaticUtils.getCache().add(twitterPlayer);

        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    @Override
    public void addToLimitCount(TwitterPlayer twitterPlayer, int incrementCount) {
        twitterPlayer.getData().setLimitCount(twitterPlayer.getData().getLimitCount() + incrementCount);

        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    @Override
    public void addTimestamp(TwitterPlayer twitterPlayer, Timestamp timestamp) {
        twitterPlayer.getData().setLatestTimestamp(timestamp);

        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

}
