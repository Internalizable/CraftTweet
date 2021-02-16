package me.internalizable.crafttweet.cmd;

import me.internalizable.crafttweet.CraftTweetSpigot;
import me.internalizable.crafttweet.api.TwitterAPI;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.config.TwitterMessages;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.queue.WaitingQueue;
import me.internalizable.crafttweet.utils.StaticUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand implements CommandExecutor {

    private CraftTweetSpigot instance;
    private IConfig config;
    private ITwitterCache twitterCache;

    public LinkCommand(CraftTweetSpigot instance, IConfig config, ITwitterCache twitterCache) {
        this.instance = instance;
        this.config = config;
        this.twitterCache = twitterCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission("link.use")) {
                player.sendMessage(TwitterMessages.NO_PERMS.build());
                return true;
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

                    return true;
                }

                player.sendMessage(TwitterMessages.INFORMATION_FALSE.build());
                return true;
            }

            if(args.length == 1) {

                if(args[0].equalsIgnoreCase("create")) {

                    TwitterPlayer twitterPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(twitterPlayer != null) {
                        player.sendMessage(TwitterMessages.ERROR_ALREADY_LINKED.build());
                        return true;
                    }

                    TwitterPlayer requestPlayer = new TwitterPlayer(player.getUniqueId(), config, twitterCache);
                    TwitterAPI twitterAPI = new TwitterAPI(requestPlayer, config, twitterCache);

                    twitterAPI.getAuthenticationURL().thenAccept(url -> {
                        TwitterMessages requestMessage = TwitterMessages.LINKAGE_GENERATE_URL;

                        requestMessage.registerPlaceholder("%url%", url);
                        requestMessage.registerPlaceholder("%player%", player.getName());

                        player.sendMessage(requestMessage.build());
                    });

                    return true;
                }
            }

            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("confirm")) {
                    String pin = args[1];

                    if(config.isCallback()) {
                        //todo send player message that he can't confirm because there's no pin
                        return true;
                    }

                    TwitterPlayer runningPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(runningPlayer != null) {
                        player.sendMessage(TwitterMessages.ERROR_ALREADY_LINKED.build());
                        return true;
                    }

                    TwitterPlayer waitingPlayer = twitterCache.getWaitingPlayer(player.getUniqueId());

                    if(waitingPlayer == null) {
                        player.sendMessage(TwitterMessages.ERROR_NO_REQUEST.build());
                        return true;
                    }

                    TwitterAPI twitterAPI = new TwitterAPI(waitingPlayer, config, twitterCache);

                    twitterAPI.validateAuthenticationToken(pin).thenAccept(accessToken -> {
                        if(accessToken == null) {
                            player.sendMessage(TwitterMessages.ERROR_WRONG_TOKEN.build());
                        } else {
                            waitingPlayer.insertPlayer(accessToken.getToken(), accessToken.getTokenSecret());

                            twitterCache.addActivePlayer(waitingPlayer);

                            player.sendMessage(TwitterMessages.SUCCESFUL_LINK.build());
                        }

                    });

                    return true;
                }
            }

            player.sendMessage(TwitterMessages.UNKNOWN_FORMAT.build());
            return true;
        }

        if(args.length >= 3) {
            if(args[0].equalsIgnoreCase("tweet")) {
                String playerName = args[1];
                String tweetMessage = getArgs(args, 2);

                if(Bukkit.getPlayer(playerName) != null) {
                    Player player = Bukkit.getPlayer(playerName);

                    TwitterPlayer twitterPlayer = twitterCache.getActivePlayer(player.getUniqueId());

                    if(twitterPlayer != null) {
                        StaticUtils.addToQueue(twitterPlayer, tweetMessage);
                    }
                }
            }
        }

        return true;
    }


    public String getArgs(String[] args, int num) {
        StringBuilder sb = new StringBuilder();
        for(int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

}
