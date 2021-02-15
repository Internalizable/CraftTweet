package me.internalizable.crafttweet.player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Getter
@Setter

public class TwitterData {
    private UUID UUID;

    private String oauth_p;

    private String oauth_s;

    private boolean twitter;

    private int limitCount;

    private Timestamp latestTimestamp;

    private String token_public;

    private String token_secret;
}
