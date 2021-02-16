package me.internalizable.crafttweet.queue;

import me.internalizable.crafttweet.api.TwitterAPI;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.config.IConfig;

import java.sql.Timestamp;

public class RunningQueueManager {

    private int currentIndex = 0;

    private ITwitterCache twitterCache;
    private IConfig config;

    public RunningQueueManager(IConfig config, ITwitterCache twitterCache) {
        this.config = config;
        this.twitterCache = twitterCache;
    }

    public void readQueue() {
        if(currentIndex < twitterCache.getTweetAmount()) {
            QueuedTweet queuedTweet = twitterCache.getQueuedTweet(currentIndex);

            Timestamp cacheTimestamp = twitterCache.getTimestamp(queuedTweet.getTwitterPlayer().getData().getUUID());

            if(cacheTimestamp != null) {
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

                if(currentTimestamp.before(cacheTimestamp) && !currentTimestamp.equals(cacheTimestamp)) {
                    ++currentIndex;
                    readQueue();
                    return;
                }
            }

            TwitterAPI twitterAPI = new TwitterAPI(queuedTweet.getTwitterPlayer(), config, twitterCache);

            twitterAPI.sendTweet(queuedTweet.getTweetToQueue()).thenAccept(waitTime -> {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis() + waitTime);
                twitterCache.addTimestamp(queuedTweet.getTwitterPlayer(), timestamp);
                twitterCache.removeQueuedTweet(currentIndex);
            }).thenRun(this::readQueue);

        } else {
            twitterCache.setUpdateStatus(false);
            currentIndex = 0;
        }
    }



}
