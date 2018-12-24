package com.leecampbell.playerservice.Model;

public class RegisterPlayerCommand {
    private final int playerId;
    private final String screenName;

    public RegisterPlayerCommand(int playerId, String screenName){
        if( screenName==null || screenName.isEmpty()){
            throw new IllegalArgumentException("screenName can not be null or empty.");
        }
        this.playerId = playerId;
        this.screenName = screenName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getScreenName() {
        return screenName;
    }
}
