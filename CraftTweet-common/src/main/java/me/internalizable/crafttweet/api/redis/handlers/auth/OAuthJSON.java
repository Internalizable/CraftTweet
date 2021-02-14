package me.internalizable.crafttweet.api.redis.handlers.auth;

import lombok.Getter;
import lombok.Setter;

public class OAuthJSON {

    @Getter @Setter
    private String uuid;

    @Getter @Setter
    private String oauth_token;

    @Getter @Setter
    private String oauth_token_secret;

    @Getter @Setter
    private long user_id;

    @Getter @Setter
    private String screen_name;

}
