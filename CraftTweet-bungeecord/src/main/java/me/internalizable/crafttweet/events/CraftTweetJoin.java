package me.internalizable.crafttweet.events;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.data.IStorageData;
import me.internalizable.crafttweet.player.TwitterPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CraftTweetJoin implements Listener {

    private ITwitterCache twitterCache;
    private IConfig config;
    private IStorageData storageData;

    public CraftTweetJoin(ITwitterCache twitterCache, IConfig config, IStorageData storageData) {
        this.twitterCache = twitterCache;
        this.config = config;
        this.storageData = storageData;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        TwitterPlayer searchedPlayer = twitterCache.getActivePlayer(event.getPlayer().getUniqueId());

        if(searchedPlayer == null) {
            TwitterPlayer twitterPlayer = new TwitterPlayer(event.getPlayer().getUniqueId(), config, twitterCache, storageData);
            twitterPlayer.init();
        }
    }

}
