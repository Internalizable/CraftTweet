package me.internalizable.crafttweet.utils;

import me.internalizable.crafttweet.CraftTweetBungeeCord;

import java.util.UUID;

public class BungeeUtils implements IUtils {

    private CraftTweetBungeeCord instance;

    public BungeeUtils(CraftTweetBungeeCord instance) {
        this.instance = instance;
    }

    @Override
    public void sendPlayerMessage(UUID id, String message) {
        if(instance.getProxy().getPlayer(id) != null)
            instance.getProxy().getPlayer(id).sendMessage(message);
    }

}
