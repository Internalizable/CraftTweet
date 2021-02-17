package me.internalizable.crafttweet.redis;

import me.internalizable.crafttweet.api.redis.RedisManager;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterPlayer;

import java.sql.Timestamp;

public class RedisServerCache extends ITwitterCache {

    private RedisManager redisManager;

    public RedisServerCache(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void addWaitingPlayer(TwitterPlayer twitterPlayer) {
        super.addWaitingPlayer(twitterPlayer);
        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    public void addActivePlayer(TwitterPlayer twitterPlayer) {
        super.addActivePlayer(twitterPlayer);
        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    public void addToLimitCount(TwitterPlayer twitterPlayer, int incrementCount) {
        super.addToLimitCount(twitterPlayer, incrementCount);
        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

    public void addTimestamp(TwitterPlayer twitterPlayer, Timestamp timestamp) {
        super.addTimestamp(twitterPlayer, timestamp);
        redisManager.getRedisBus().publishPayload("twitter-cache-handler", twitterPlayer.getData());
    }

}
