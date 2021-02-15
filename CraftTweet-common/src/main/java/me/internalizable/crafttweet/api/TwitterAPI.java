package me.internalizable.crafttweet.api;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.queue.QueuedTweet;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.utils.StaticUtils;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.concurrent.CompletableFuture;

public class TwitterAPI {

    private TwitterPlayer twitterPlayer;
    private IConfig config;
    private ITwitterCache twitterCache;

    public TwitterAPI(TwitterPlayer twitterPlayer, IConfig config, ITwitterCache twitterCache) {
        this.twitterPlayer = twitterPlayer;
        this.config = config;
        this.twitterCache = twitterCache;
    }

    public CompletableFuture<String> getAuthenticationURL() {
        return CompletableFuture.supplyAsync(()-> {
            try {

                RequestToken requestToken;

                if(config.isCallback()) {
                    String callbackURL = config.getCallbackURL().replace("%uuid%", twitterPlayer.getData().getUUID().toString());
                    requestToken = twitterPlayer.getTwitterClient().getOAuthRequestToken(callbackURL);
                } else {
                    requestToken = twitterPlayer.getTwitterClient().getOAuthRequestToken();
                }


                twitterPlayer.getData().setToken_public(requestToken.getToken());
                twitterPlayer.getData().setToken_secret(requestToken.getTokenSecret());

                twitterCache.addWaitingPlayer(twitterPlayer);

                return requestToken.getAuthorizationURL();
            } catch (TwitterException exception) {
                exception.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<AccessToken> validateAuthenticationToken(String pin) {
        return CompletableFuture.supplyAsync(() -> {
            AccessToken accessToken = null;

            try {
                if (pin.length() > 0) {
                    accessToken = twitterPlayer.getTwitterClient().getOAuthAccessToken(new RequestToken(twitterPlayer.getData().getToken_public(), twitterPlayer.getData().getToken_secret()), pin);
                } else {
                    accessToken = twitterPlayer.getTwitterClient().getOAuthAccessToken();
                }

            } catch (TwitterException te) {
                if (te.getStatusCode() != 401) {
                    te.printStackTrace();
                }
            }

            return accessToken;
        });
    }

    public CompletableFuture<String> getScreenName() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return twitterPlayer.getTwitterClient().getScreenName();
            } catch (TwitterException exc) {
                exc.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<Integer> sendTweet(String tweet) {
        if(twitterCache.getPlayerLimit(twitterPlayer.getData().getUUID()) >= config.getTweetLimit()) {
            QueuedTweet queuedTweet = QueuedTweet.builder()
                    .twitterPlayer(twitterPlayer)
                    .tweetToQueue(tweet)
                    .build();

            twitterCache.addToWaitQueue(queuedTweet);

            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync( ()-> {
            try {
                Status status = twitterPlayer.getTwitterClient().updateStatus(tweet.replaceAll(":thumbs_up:", "\uD83D\uDC4D"));
                twitterCache.addToLimitCount(twitterPlayer, 1);

                return StaticUtils.handleRateLimit(status.getRateLimitStatus());
            } catch (TwitterException exception) {
                exception.printStackTrace();
            }

            return 0;
        });
    }
}
