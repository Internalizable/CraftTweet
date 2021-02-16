package me.internalizable.crafttweet.api.redis.handlers.auth;

import me.internalizable.crafttweet.api.redis.bus.annotation.RedisHandler;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.config.TwitterMessages;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.utils.IUtils;
import twitter4j.auth.AccessToken;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OAuthReciever {

    private IConfig config;
    private ITwitterCache twitterCache;
    private IUtils utils;

    public OAuthReciever(IConfig iConfig, ITwitterCache twitterCache, IUtils iUtils) {
        this.config = iConfig;
        this.twitterCache = twitterCache;
        this.utils = iUtils;
    }

    @RedisHandler("twitter-oauth-callback")
    public void handleRequest(OAuthJSON recievedRequest) {
        TwitterPlayer twitterPlayer = twitterCache.getWaitingPlayer(recievedRequest.getUuid());

        if(twitterPlayer != null && config.isCallbackServer()) {
            CompletableFuture.runAsync(() -> {
                TwitterPlayer newPlayer = new TwitterPlayer(recievedRequest.getUuid(), config, twitterCache);
                newPlayer.getTwitterClient().setOAuthAccessToken(new AccessToken(recievedRequest.getOauth_token(), recievedRequest.getOauth_token_secret()));
                newPlayer.insertPlayer(recievedRequest.getOauth_token(), recievedRequest.getOauth_token_secret());

                utils.sendPlayerMessage(recievedRequest.getUuid(), TwitterMessages.SUCCESFUL_LINK.build());
            });
        }
    }

}
