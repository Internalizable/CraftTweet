package me.internalizable.crafttweet.utils;

import org.bukkit.Bukkit;

import java.util.UUID;

public class BukkitUtils implements IUtils {

    @Override
    public void sendPlayerMessage(UUID id, String message) {
        if(Bukkit.getPlayer(id) != null) {
            Bukkit.getPlayer(id).sendMessage(message);
        }
    }

}
