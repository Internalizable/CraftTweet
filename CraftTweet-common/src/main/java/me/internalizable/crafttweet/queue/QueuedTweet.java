package me.internalizable.crafttweet.queue;

import lombok.Builder;
import lombok.Getter;
import me.internalizable.crafttweet.player.TwitterPlayer;

@Getter
@Builder
public class QueuedTweet {
    private TwitterPlayer twitterPlayer;
    private String tweetToQueue;
}
