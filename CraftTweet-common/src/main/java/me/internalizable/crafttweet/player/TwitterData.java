package me.internalizable.crafttweet.player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import java.sql.Timestamp;

@Builder
@Getter
@Setter

public class TwitterData {
    private String UUID;

    private String oauth_p;

    private String oauth_s;

    private boolean twitter;

    private TwitterFactory twitterFactory;

    private Twitter twitterClient;

    private RequestToken requestToken;

    private int limitCount;

    private Timestamp latestTimestamp;
}
