package me.internalizable.crafttweet.cache;

import lombok.Getter;
import lombok.Setter;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.queue.QueuedTweet;
import me.internalizable.crafttweet.utils.StaticUtils;

import java.sql.Timestamp;
import java.util.*;

public abstract class ITwitterCache {

    public TwitterPlayer getActivePlayer(UUID id) {
        return StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(id)).findAny().orElse(null);
    }

    public TwitterPlayer getWaitingPlayer(UUID id) {
        return StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(id)).findAny().orElse(null);
    }

    public void addToWaitQueue(QueuedTweet queuedTweet) {
        StaticUtils.getWaitQueue().add(queuedTweet);
    }

    public int getPlayerLimit(UUID id) {
        return StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(id)).findAny().map(player -> player.getData().getLimitCount()).orElse(0);
    }

    public int getTweetAmount() { return StaticUtils.getRunningQueue().size(); }

    public QueuedTweet getQueuedTweet(int index) { return StaticUtils.getRunningQueue().get(index); }

    public void removeQueuedTweet(int index) { StaticUtils.getRunningQueue().remove(index); }

    public Timestamp getTimestamp(UUID id) {
        return StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(id)).findAny().map(player -> player.getData().getLatestTimestamp()).orElse(null);
    }

    public void setUpdateStatus(boolean updateStatus) {
        StaticUtils.setUpdating(updateStatus);
    }

    /**
     * Must be synced with Redis servers.
     * @param twitterPlayer
     * @param timestamp
     */

    public abstract void addTimestamp(TwitterPlayer twitterPlayer, Timestamp timestamp);
    public abstract void addWaitingPlayer(TwitterPlayer twitterPlayer);
    public abstract void addActivePlayer(TwitterPlayer twitterPlayer);
    public abstract void addToLimitCount(TwitterPlayer twitterPlayer, int incrementCount);

}
