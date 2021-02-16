package me.internalizable.crafttweet.player;

import lombok.Getter;
import lombok.Setter;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
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

    private TwitterFactory twitterFactory;

    @Getter @Setter
    private Twitter twitterClient;

    public TwitterPlayer(UUID uuid, IConfig config, ITwitterCache twitterCache) {
        this.twitterCache = twitterCache;

        data = TwitterData.builder()
                .UUID(uuid)
                .oauth_p("")
                .oauth_s("")
                .twitter(false)
                .limitCount(0)
                .latestTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        twitterFactory = new TwitterFactory();
        twitterClient = twitterFactory.getInstance();
        twitterClient.setOAuthConsumer(config.getPublicKey(), config.getPrivateKey());
    }

    public void init() {
        PredefinedStmts selectionStmt = PredefinedStmts.SELECTION;
        selectionStmt.registerPlaceholder("%uuid%", data.getUUID().toString());

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement pst = MySQL.getDs().getConnection().prepareStatement(selectionStmt.getStatement());
                ResultSet rs = pst.executeQuery();

                while(rs.next()) {
                    data.setOauth_p(rs.getString("oauthp"));
                    data.setOauth_s(rs.getString("oauths"));

                    data.setTwitter(true);
                    twitterClient.setOAuthAccessToken(new AccessToken(rs.getString("oauthp"), rs.getString("oauths")));

                    twitterCache.addActivePlayer(this);

                    System.out.println("Added to cache");
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void insertPlayer(String oauthp, String oauths) {
        PredefinedStmts insertionStmt = PredefinedStmts.INSERTION;
        insertionStmt.registerPlaceholder("%uuid%", data.getUUID().toString());
        insertionStmt.registerPlaceholder("%oauthp%", oauthp);
        insertionStmt.registerPlaceholder("%oauths%", oauths);

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement pst = MySQL.getDs().getConnection().prepareStatement(insertionStmt.getStatement());
                pst.executeUpdate();

                data.setOauth_p(oauthp);
                data.setOauth_s(oauths);
                data.setTwitter(true);

                twitterClient.setOAuthAccessToken(new AccessToken(oauthp, oauths));

                twitterCache.addActivePlayer(this);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

    }

    public void deletePlayer() {
        PredefinedStmts deletionStmt = PredefinedStmts.DELETION;
        deletionStmt.registerPlaceholder("%uuid%", data.getUUID().toString());

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement pst = MySQL.getDs().getConnection().prepareStatement(deletionStmt.getStatement());
                pst.executeUpdate();

                data.setOauth_p("");
                data.setOauth_s("");
                data.setTwitter(false);

                twitterClient = null;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

}
