package me.internalizable.crafttweet.events;

import me.internalizable.crafttweet.CraftTweetSpigot;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.player.TwitterPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class CraftTweetJoin implements Listener {

    private ITwitterCache twitterCache;
    private IConfig config;
    private CraftTweetSpigot instance;

    public CraftTweetJoin(CraftTweetSpigot instance, IConfig config, ITwitterCache twitterCache) {
        this.instance = instance;
        this.twitterCache = twitterCache;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        TwitterPlayer searchedPlayer = twitterCache.getActivePlayer(event.getUniqueId());

        if(searchedPlayer == null) {
            TwitterPlayer twitterPlayer = new TwitterPlayer(event.getUniqueId(), config, twitterCache);
            twitterPlayer.init();
        }
    }
}
