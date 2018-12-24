package com.leecampbell.playerservice.Model;

public class Player{
    private final int id;
    private String screenName;

    public Player(int id){
        this.id = id;
    }

    public void load(PlayerState state){
        if(id!=state.playerId) throw new IllegalArgumentException("state is for different player");
        screenName = state.screenName;
    }

    public void register(RegisterPlayerCommand command){
        if(screenName!=null) throw new DomainInvarianceException("Player can only be registered once.");
        screenName = command.getScreenName();
    }

    public PlayerState getUncommitedState(){
        PlayerState state = new PlayerState();
        state.playerId = id;
        state.screenName = screenName;
        return state;
    }
}
