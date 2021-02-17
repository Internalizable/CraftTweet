package me.internalizable.crafttweet.cache;

import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.queue.QueuedTweet;
import me.internalizable.crafttweet.utils.StaticUtils;

import java.sql.Timestamp;
import java.util.*;

public class ITwitterCache {

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

    public void addTimestamp(TwitterPlayer twitterPlayer, Timestamp timestamp) {
        twitterPlayer.getData().setLatestTimestamp(timestamp);
    }

    public void addWaitingPlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getWaitingCache().add(twitterPlayer);
    }

    public void addActivePlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getCache()::remove);
        StaticUtils.getCache().add(twitterPlayer);
    }

    public void addToLimitCount(TwitterPlayer twitterPlayer, int incrementCount) {
        twitterPlayer.getData().setLimitCount(twitterPlayer.getData().getLimitCount() + incrementCount);
    }

}
