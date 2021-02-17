package me.internalizable.crafttweet.config;

import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

public class IConfig {

    @Getter
    public static YamlFile configFile;

    @Getter
    private String publicKey;

    @Getter
    private String privateKey;

    @Getter
    private boolean redisBungee;

    @Getter
    private boolean callback;

    @Getter
    private String callbackURL;

    @Getter
    private int tweetLimit;

    @Getter
    private boolean callbackServer;

    @Getter
    private boolean SQL;

    @Getter
    private String SQLHost;

    @Getter
    private String SQLDatabase;

    @Getter
    private int SQLPort;

    @Getter
    private String SQLUsername;

    @Getter
    private String SQLPassword;

    public void init(File filePath) throws Exception {

        configFile = new YamlFile(filePath.getPath() + "/config.yml");

        if (!getConfigFile().exists()) {
            System.out.println("New file has been created: " + getConfigFile().getFilePath() + "\n");
            getConfigFile().createNewFile(true);

            getConfigFile().set("twitter.pkey", "somePublicKey");
            getConfigFile().set("twitter.skey", "somePrivateKey");
            getConfigFile().set("twitter.useRedisBungee", false);
            getConfigFile().set("twitter.callback.state", false);
            getConfigFile().set("twitter.callback.url", "127.0.0.1:8000/link/?uuid=%uuid%");
            getConfigFile().set("twitter.callback.main-server", true);
            getConfigFile().set("twitter.limit", 5);
            getConfigFile().set("twitter.useSQL", true);
            getConfigFile().set("mysql.host", "localhost");
            getConfigFile().set("mysql.port", "3306");
            getConfigFile().set("mysql.database", "twitter");
            getConfigFile().set("mysql.username", "root");
            getConfigFile().set("messages.prefix", "&bTwitterLink &8|");
            getConfigFile().set("messages.noperms", "&cYou do not have the right to execute this command!");
            getConfigFile().set("messages.unknownformat", "&cUnknown format! Use /link to start.");
            getConfigFile().set("messages.general.isLinked", "&7Your current Minecraft account is &alinked &7to the Twitter account &a@%account%&7!");
            getConfigFile().set("messages.general.isNotLinked", "&7Your current Minecraft account is &cnot linked &7to any Twitter account!\\n&7Use &a/link create &7to start the linking process.");
            getConfigFile().set("messages.link.success", "&aYou have succesfully linked your twitter account with your Minecraft account!");
            getConfigFile().set("messages.link.url", "&7Please click &a&lhere to authenticate your twitter account with your Minecraft account\n&7Use your &aPIN &7using /link confirm &aPIN &7to initiate the link.");
            getConfigFile().set("messages.error.token", "&cThe giving token is wrong, please try again!");
            getConfigFile().set("messages.error.norequest", "&cYou have to iniate a linkage request before confirming!");
            getConfigFile().set("messages.error.notlinked", "&cYou do not have a twitter account linked!");
            getConfigFile().set("messages.error.alreadylinked", "&You already have a twitter account linked!");

            saveConfig();
        } else {
            System.out.println(getConfigFile().getFilePath() + " already exists, loading configurations...\n");
        }

        getConfigFile().load();
        loadConfiguration();
    }

    private void loadConfiguration() {
        publicKey = configFile.getString("twitter.pkey");
        privateKey = configFile.getString("twitter.skey");

        redisBungee = configFile.getBoolean("twitter.useRedisBungee");

        callback = configFile.getBoolean("twitter.callback.state");
        callbackURL = configFile.getString("twitter.callback.url");
        callbackServer = configFile.getBoolean("twitter.callback.main-server");
        tweetLimit = configFile.getInt("twitter.limit");

        SQL = configFile.getBoolean("twitter.useSQL");
        SQLHost = configFile.getString("mysql.host");
        SQLPort = configFile.getInt("mysql.port");
        SQLDatabase = configFile.getString("mysql.database");
        SQLUsername = configFile.getString("mysql.username");
        SQLPassword = configFile.getString("mysql.password");
    }

    public void saveConfig() {
        try {
            getConfigFile().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
