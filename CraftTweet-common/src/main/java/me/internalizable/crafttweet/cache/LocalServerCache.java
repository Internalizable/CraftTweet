package me.internalizable.crafttweet.cache;

import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.utils.StaticUtils;

import java.sql.Timestamp;

public class LocalServerCache extends ITwitterCache {

    @Override
    public void addWaitingPlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getWaitingCache().add(twitterPlayer);
    }

    @Override
    public void addActivePlayer(TwitterPlayer twitterPlayer) {
        StaticUtils.getWaitingCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getWaitingCache()::remove);
        StaticUtils.getCache().stream().filter(player -> player.getData().getUUID().equals(twitterPlayer.getData().getUUID())).findAny().ifPresent(StaticUtils.getCache()::remove);
        StaticUtils.getCache().add(twitterPlayer);
    }

    @Override
    public void addToLimitCount(TwitterPlayer twitterPlayer, int incrementCount) {
        twitterPlayer.getData().setLimitCount(twitterPlayer.getData().getLimitCount() + incrementCount);
    }

    @Override
    public void addTimestamp(TwitterPlayer twitterPlayer, Timestamp timestamp) {
        twitterPlayer.getData().setLatestTimestamp(timestamp);
    }

}
