package me.internalizable.crafttweet.data;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterData;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.utils.StaticUtils;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

import java.util.Optional;

public class FlatDataStorage implements IStorageData {

    @Override
    public void readPlayer(TwitterPlayer twitterPlayer, Twitter twitterClient, ITwitterCache twitterCache) {
        Optional<TwitterData> flatData = StaticUtils.getFlatData().stream().filter(storageData -> storageData.getUUID().equals(twitterPlayer.getData().getUUID())).findAny();

        if(flatData.isPresent()) {
            twitterPlayer.getData().setOauth_p(flatData.get().getOauth_p());
            twitterPlayer.getData().setOauth_s(flatData.get().getOauth_s());

            twitterPlayer.getData().setTwitter(true);
            twitterClient.setOAuthAccessToken(new AccessToken(flatData.get().getOauth_p(), flatData.get().getOauth_s()));

            twitterCache.addActivePlayer(twitterPlayer);
        }
    }

    @Override
    public void insertPlayer(Twitter twitterClient, ITwitterCache twitterCache, TwitterPlayer twitterPlayer, String oauthp, String oauths) {
        StaticUtils.getFlatData().add(twitterPlayer.getData());
        twitterClient.setOAuthAccessToken(new AccessToken(oauthp, oauths));
        twitterCache.addActivePlayer(twitterPlayer);
    }

}
