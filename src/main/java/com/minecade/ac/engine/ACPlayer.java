package com.minecade.ac.engine;

import org.bukkit.entity.Player;

import com.minecade.ac.data.ACPersistence;
import com.minecade.ac.data.PlayerModel;
import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.data.MinecadeAccount;

public class ACPlayer{

    private Player bukkitPlayer;
    private PlayerModel playerModel;
    private MinecadeAccount minecadeAccount;
    private CharacterEnum character;
    private int lives;
    private boolean cooling;
    private boolean inJail;
    private boolean emeraldImproved;
    protected String lastMessage;
    
    public ACPlayer (MinecadeAccount account, Player bukkitPlayer){
        this.bukkitPlayer = bukkitPlayer;
        this.minecadeAccount = account;
        this.emeraldImproved = false;
        this.inJail = false;
        this.cooling = false;
    }
    
    public ACPlayer(){
        
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public PlayerModel getPlayerModel() {
        if(playerModel == null){
            playerModel = ((ACPersistence)AssassinsCreedPlugin.getInstance().getPersistence()).getPlayer(bukkitPlayer.getName());
        }
        return playerModel;
    }
    

   public PlayerModel getRefreshPlayerModel() {
       return playerModel = ((ACPersistence)AssassinsCreedPlugin.getInstance().getPersistence()).getPlayer(bukkitPlayer.getName());
   }

    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }

    public MinecadeAccount getMinecadeAccount() {
        return this.minecadeAccount;
    }

    public void setMinecadeAccount(MinecadeAccount minecadeAccount) {
        this.minecadeAccount = minecadeAccount;
    }

    public CharacterEnum getCharacter(){
        return this.character;
    }
    
    public void setCharacter(CharacterEnum character){
        this.character = character;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int livesleft) {
        this.lives = livesleft;
    }

    public boolean isCooling(){
        return this.cooling;
    }

    public void setCooling(boolean cooling){
        this.cooling = cooling;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * @return the currentMatchName
     */
    public String getCurrentMatchName() {
        return this.bukkitPlayer.getWorld().getName();
    }

//    /**
//     * @param currentMatchName the currentMatchName to set
//     */
//    public void setCurrentMatchName(String currentMatchName) {
//        this.currentMatchName = currentMatchName;
//    }

    /**
     * @return the inJail
     */
    public boolean isInJail() {
        return inJail;
    }

    /**
     * @param inJail the inJail to set
     */
    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

    /**
     * @return the emeraldImproved
     */
    public boolean isEmeraldImproved() {
        return emeraldImproved;
    }

    /**
     * @param emeraldImproved the emeraldImproved to set
     */
    public void setEmeraldImproved(boolean emeraldImproved) {
        this.emeraldImproved = emeraldImproved;
    }
}
