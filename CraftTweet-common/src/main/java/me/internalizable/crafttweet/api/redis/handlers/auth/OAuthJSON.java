package me.internalizable.crafttweet.api.redis.handlers.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class OAuthJSON {

    @Getter @Setter
    private UUID uuid;

    @Getter @Setter
    private String oauth_token;

    @Getter @Setter
    private String oauth_token_secret;

    @Getter @Setter
    private long user_id;

    @Getter @Setter
    private String screen_name;

}
