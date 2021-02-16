package me.internalizable.crafttweet.config;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

public enum TwitterMessages {
    PREFIX("messages.prefix"),
    NO_PERMS("messages.noperms"),
    UNKNOWN_FORMAT("messages.unknownformat"),
    INFORMATION_TRUE("messages.general.isLinked"),
    INFORMATION_FALSE("messages.general.isNotLinked"),
    SUCCESFUL_LINK("messages.link.success"),
    LINKAGE_GENERATE_URL("messages.link.url"),
    ERROR_WRONG_TOKEN("messages.error.token"),
    ERROR_NO_REQUEST("messages.error.norequest"),
    ERROR_NOT_LINKED("messages.error.notlinked"),
    ERROR_ALREADY_LINKED("messages.error.alreadylinked");

    private String m;
    private Map<String, String> pH = new HashMap<>();

    TwitterMessages(String configSection) {
        this.m = configSection;
    }

    public void registerPlaceholder(String placeholder, String replaceWith) {
        pH.put(placeholder, replaceWith);
    }

    public String buildPrefix() {
        return IConfig.getConfigFile().getString(m);
    }

    public String build() {

        String replaced = PREFIX.buildPrefix() + IConfig.getConfigFile().getString(m);

        replaced.replaceAll("\n", "\n" + PREFIX.buildPrefix());

        for(Map.Entry<String, String> replace : pH.entrySet()) {
            replaced = replaced.replace(replace.getKey(), replace.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', replaced);
    }
}
