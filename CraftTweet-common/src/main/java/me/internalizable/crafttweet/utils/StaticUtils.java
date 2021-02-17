package me.internalizable.crafttweet.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.Setter;
import me.internalizable.crafttweet.cache.ITwitterCache;
import me.internalizable.crafttweet.player.TwitterData;
import me.internalizable.crafttweet.player.TwitterPlayer;
import me.internalizable.crafttweet.queue.QueuedTweet;
import twitter4j.RateLimitStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StaticUtils {

    @Getter
    private static List<TwitterPlayer> cache = new ArrayList<>();
    @Getter
    private static List<TwitterPlayer> waitingCache = new ArrayList<>();
    @Getter
    private static Queue<QueuedTweet> waitQueue = new LinkedList<>();
    @Getter
    private static LinkedList<QueuedTweet> runningQueue = new LinkedList<>();

    private static final Type DATA_TYPE = new TypeToken<List<TwitterData>>() {}.getType();

    @Getter
    private static List<TwitterData> flatData = new ArrayList<>();

    @Getter @Setter
    private static boolean isUpdating = false;

    public static void addToQueue(TwitterPlayer twitterPlayer, String tweet) {
        QueuedTweet queuedTweet = QueuedTweet.builder()
                                                .twitterPlayer(twitterPlayer)
                                                .tweetToQueue(tweet).build();

        runningQueue.add(queuedTweet);
    }

    public static void populateDataArray(String path) {
        Gson gson = new Gson();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(path));
            flatData = gson.fromJson(reader, DATA_TYPE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveDataArray() {

    }

    public static int handleRateLimit(RateLimitStatus rateLimitStatus) {

        if(rateLimitStatus == null)
            return 0;

        int remaining = rateLimitStatus.getRemaining();

        if (remaining == 0) {
            int resetTime = rateLimitStatus.getSecondsUntilReset() + 5;

            return resetTime > 0 ? resetTime * 1000 : 0;
        }

        return 0;
    }

}
