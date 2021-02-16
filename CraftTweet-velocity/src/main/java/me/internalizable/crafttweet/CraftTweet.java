package me.internalizable.crafttweet;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "CraftTweet",
        name = "CraftTweet",
        version = "1.0-SNAPSHOT",
        description = "Links your twitter account with your minecraft account"
)
public class CraftTweet {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
