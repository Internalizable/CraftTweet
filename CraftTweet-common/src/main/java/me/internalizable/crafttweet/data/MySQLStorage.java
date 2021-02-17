package me.internalizable.crafttweet.data;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.sql.MySQL;
import me.internalizable.crafttweet.sql.prep.PredefinedStmts;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class MySQLStorage implements IStorageData {
    @Override
    public void readPlayer(TwitterPlayer twitterPlayer, Twitter twitterClient, ITwitterCache twitterCache) {
        PredefinedStmts selectionStmt = PredefinedStmts.SELECTION;
        selectionStmt.registerPlaceholder("%uuid%", twitterPlayer.getData().getUUID().toString());

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement pst = MySQL.getDs().getConnection().prepareStatement(selectionStmt.getStatement());
                ResultSet rs = pst.executeQuery();

                while(rs.next()) {
                    twitterPlayer.getData().setOauth_p(rs.getString("oauthp"));
                    twitterPlayer.getData().setOauth_s(rs.getString("oauths"));

                    twitterPlayer.getData().setTwitter(true);
                    twitterClient.setOAuthAccessToken(new AccessToken(rs.getString("oauthp"), rs.getString("oauths")));

                    twitterCache.addActivePlayer(twitterPlayer);
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void insertPlayer(Twitter twitterClient, ITwitterCache twitterCache, TwitterPlayer twitterPlayer, String oauthp, String oauths) {
        PredefinedStmts insertionStmt = PredefinedStmts.INSERTION;
        insertionStmt.registerPlaceholder("%uuid%", twitterPlayer.getData().getUUID().toString());
        insertionStmt.registerPlaceholder("%oauthp%", oauthp);
        insertionStmt.registerPlaceholder("%oauths%", oauths);

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement pst = MySQL.getDs().getConnection().prepareStatement(insertionStmt.getStatement());
                pst.executeUpdate();

                twitterPlayer.getData().setOauth_p(oauthp);
                twitterPlayer.getData().setOauth_s(oauths);
                twitterPlayer.getData().setTwitter(true);

                twitterClient.setOAuthAccessToken(new AccessToken(oauthp, oauths));

                twitterCache.addActivePlayer(twitterPlayer);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

}
