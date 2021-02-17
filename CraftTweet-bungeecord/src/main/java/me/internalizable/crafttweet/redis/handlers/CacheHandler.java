package me.internalizable.crafttweet.redis.handlers;

import me.internalizable.crafttweet.CraftTweetBungeeCord;
import me.internalizable.crafttweet.api.redis.bus.annotation.RedisHandler;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.config.TwitterMessages;
import me.internalizable.crafttweet.data.IStorageData;
import me.internalizable.crafttweet.player.TwitterData;
import me.internalizable.crafttweet.player.TwitterPlayer;
import twitter4j.auth.AccessToken;

public class CacheHandler {

    private CraftTweetBungeeCord instance;
    private ITwitterCache localCache;
    private IConfig config;
    private IStorageData storageData;

    public CacheHandler(CraftTweetBungeeCord instance, IConfig config, ITwitterCache localCache, IStorageData storageData) {
        this.instance = instance;
        this.config = config;
        this.localCache = localCache;
        this.storageData = storageData;
    }

    @RedisHandler("twitter-cache-handler")
    public void handleRequest(TwitterData twitterData) {
        TwitterPlayer waitingPlayer = localCache.getWaitingPlayer(twitterData.getUUID());

        TwitterPlayer twitterPlayer = new TwitterPlayer(twitterData.getUUID(), config, localCache, storageData);
        twitterPlayer.setData(twitterData);

        if(waitingPlayer != null && twitterData.isTwitter()) {
            if(instance.getProxy().getPlayer(twitterData.getUUID()) != null) {
                instance.getProxy().getPlayer(twitterData.getUUID()).sendMessage(TwitterMessages.SUCCESFUL_LINK.build());
            }
        }

        if(twitterData.isTwitter()) {
            localCache.addActivePlayer(twitterPlayer);
            twitterPlayer.getTwitterClient().setOAuthAccessToken(new AccessToken(twitterData.getOauth_p(), twitterData.getOauth_s()));
        }
        else
            localCache.addWaitingPlayer(twitterPlayer);
    }

}
