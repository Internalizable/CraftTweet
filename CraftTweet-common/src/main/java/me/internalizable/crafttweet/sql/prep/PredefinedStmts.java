package me.internalizable.crafttweet.sql.prep;

import java.util.HashMap;
import java.util.Map;

public enum PredefinedStmts {

    CREATION("CREATE TABLE IF NOT EXISTS twikeys (uuid VARCHAR(40), oauthp VARCHAR(80), oauths VARCHAR(80));"),
    INSERTION("INSERT INTO twikeys (uuid, oauthp, oauths) VALUES ('%uuid%', '%oauthp%', '%oauths%');"),
    SELECTION("SELECT * FROM twikeys WHERE uuid=\"%uuid%\";"),
    DELETION("DELETE FROM twikeys where uuid=\"%uuid%\";");

    public String m;

    public Map<String, String> pH = new HashMap<>();

    PredefinedStmts(String statement) {
        this.m = statement;
    }

    public void registerPlaceholder(String placeholder, String replaceWith) {
        pH.put(placeholder, replaceWith);
    }

    public String getStatement() {
        String replaced = "" + m;

        for(Map.Entry<String, String> replace : pH.entrySet()) {
            replaced = replaced.replace(replace.getKey(), replace.getValue());
        }

        return replaced;
    }

}
