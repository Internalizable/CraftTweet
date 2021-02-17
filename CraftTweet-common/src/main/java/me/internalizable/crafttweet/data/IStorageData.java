package me.internalizable.crafttweet.data;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterPlayer;
import twitter4j.Twitter;

public interface IStorageData {

    void readPlayer(TwitterPlayer twitterPlayer, Twitter twitterClient, ITwitterCache twitterCache);
    void insertPlayer(Twitter twitterClient, ITwitterCache twitterCache, TwitterPlayer twitterPlayer, String oauthp, String oauths);
}
