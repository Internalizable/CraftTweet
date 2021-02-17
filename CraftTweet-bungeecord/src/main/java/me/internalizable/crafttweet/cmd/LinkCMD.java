package me.internalizable.crafttweet.cmd;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import me.internalizable.crafttweet.CraftTweetBungeeCord;
import me.internalizable.crafttweet.api.TwitterAPI;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.config.TwitterMessages;
import me.internalizable.crafttweet.data.IStorageData;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.redis.RedisServerCache;
import me.internalizable.crafttweet.utils.StaticUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class LinkCMD extends Command {

    private CraftTweetBungeeCord instance;
    private IConfig config;
    private ITwitterCache twitterCache;

    private IStorageData storageData;

    public LinkCMD(CraftTweetBungeeCord instance, IConfig config, ITwitterCache twitterCache, IStorageData storageData) {
        super("link");
        this.instance = instance;
        this.config = config;
        this.twitterCache = twitterCache;
        this.storageData = storageData;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if(!player.hasPermission("link.use")) {
                player.sendMessage(TwitterMessages.NO_PERMS.build());
                return;
            }

            if(args.length == 0) {
                TwitterPlayer searchedPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                if(searchedPlayer != null) {

                    TwitterAPI twitterAPI = new TwitterAPI(searchedPlayer, config, twitterCache);

                    twitterAPI.getScreenName().thenAccept(name -> {
                        TwitterMessages information = TwitterMessages.INFORMATION_TRUE;
                        information.registerPlaceholder("%account%", name);
                        player.sendMessage(information.build());
                    });

                    return;
                }

                player.sendMessage(TwitterMessages.INFORMATION_FALSE.build());
                return;
            }

            if(args.length == 1) {

                if(args[0].equalsIgnoreCase("create")) {

                    TwitterPlayer twitterPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(twitterPlayer != null) {
                        player.sendMessage(TwitterMessages.ERROR_ALREADY_LINKED.build());
                        return;
                    }

                    TwitterPlayer requestPlayer = new TwitterPlayer(player.getUniqueId(), config, twitterCache, storageData);
                    TwitterAPI twitterAPI = new TwitterAPI(requestPlayer, config, twitterCache);

                    twitterAPI.getAuthenticationURL().thenAccept(url -> {
                        TwitterMessages requestMessage = TwitterMessages.LINKAGE_GENERATE_URL;

                        requestMessage.registerPlaceholder("%url%", url);
                        requestMessage.registerPlaceholder("%player%", player.getName());

                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(requestMessage.build()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

                        player.sendMessage(textComponent);
                    });

                    return;
                }
            }

            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("confirm")) {
                    String pin = args[1];

                    if(config.isCallback()) {
                        //todo send player message that he can't confirm because there's no pin
                        return;
                    }

                    TwitterPlayer runningPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(runningPlayer != null) {
                        player.sendMessage(TwitterMessages.ERROR_ALREADY_LINKED.build());
                        return;
                    }

                    TwitterPlayer waitingPlayer = twitterCache.getWaitingPlayer(player.getUniqueId());

                    if(waitingPlayer == null) {
                        player.sendMessage(TwitterMessages.ERROR_NO_REQUEST.build());
                        return;
                    }

                    TwitterAPI twitterAPI = new TwitterAPI(waitingPlayer, config, twitterCache);

                    twitterAPI.validateAuthenticationToken(pin).thenAccept(accessToken -> {
                        if(accessToken == null) {
                            player.sendMessage(TwitterMessages.ERROR_WRONG_TOKEN.build());
                        } else {
                            waitingPlayer.insertPlayer(accessToken.getToken(), accessToken.getTokenSecret());

                            player.sendMessage(TwitterMessages.SUCCESFUL_LINK.build());
                        }

                    });

                    return;
                }
            }

            player.sendMessage(TwitterMessages.UNKNOWN_FORMAT.build());
            return;
        }

        if(args.length >= 3) {
            if(args[0].equalsIgnoreCase("tweet")) {

                System.out.print("tweet");

                String playerName = args[1];
                String tweetMessage = getArgs(args, 2);

                if(config.isRedisBungee()) {

                    UUID id = RedisBungee.getApi().getUuidFromName(playerName);

                    if(RedisBungee.getApi().isPlayerOnline(id)) {
                        TwitterPlayer twitterPlayer = twitterCache.getActivePlayer(id);

                        if(twitterPlayer != null) {
                            StaticUtils.addToQueue(twitterPlayer, tweetMessage);
                        }
                    }

                    return;
                }

                if(instance.getProxy().getPlayer(playerName) != null) {
                    ProxiedPlayer player = instance.getProxy().getPlayer(playerName);

                    TwitterPlayer twitterPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(twitterPlayer != null) {
                        StaticUtils.addToQueue(twitterPlayer, tweetMessage);

                        System.out.println("queued");
                    }
                }
            }
        }

        return;
    }


    public String getArgs(String[] args, int num) {
        StringBuilder sb = new StringBuilder();
        for(int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

}
