package me.internalizable.crafttweet.player;

import lombok.Getter;
import lombok.Setter;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.data.IStorageData;
import me.internalizable.crafttweet.sql.MySQL;
import me.internalizable.crafttweet.sql.prep.PredefinedStmts;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TwitterPlayer {

    @Getter @Setter
    private TwitterData data;

    private ITwitterCache twitterCache;
    private IStorageData storageData;

    private TwitterFactory twitterFactory;

    @Getter @Setter
    private Twitter twitterClient;

    public TwitterPlayer(UUID uuid, IConfig config, ITwitterCache twitterCache, IStorageData storageData) {
        this.twitterCache = twitterCache;

        data = TwitterData.builder()
                .UUID(uuid)
                .oauth_p("")
                .oauth_s("")
                .twitter(false)
                .limitCount(0)
                .latestTimestamp(new Timestamp(System.currentTimeMillis()))
                .requestingServerUpdate("")
                .build();

        twitterFactory = new TwitterFactory();
        twitterClient = twitterFactory.getInstance();
        twitterClient.setOAuthConsumer(config.getPublicKey(), config.getPrivateKey());
    }

    public void init() {
        storageData.readPlayer(this, twitterClient, twitterCache);
    }

    public void insertPlayer(String oauthp, String oauths) { storageData.insertPlayer(twitterClient, twitterCache, this, oauthp, oauths); }

}
