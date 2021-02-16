package me.internalizable.crafttweet.queue;

import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.utils.StaticUtils;

public class WaitingQueue {

    private ITwitterCache twitterCache;

    public WaitingQueue(ITwitterCache twitterCache) {
        this.twitterCache = twitterCache;
    }

    public void moveQueue() {
        QueuedTweet currentTweet = StaticUtils.getWaitQueue().poll();

        while(currentTweet != null) {
            StaticUtils.addToQueue(currentTweet.getTwitterPlayer(), currentTweet.getTweetToQueue());
            currentTweet = StaticUtils.getWaitQueue().poll();
        }
    }

}
