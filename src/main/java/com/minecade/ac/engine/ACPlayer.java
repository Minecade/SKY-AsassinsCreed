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
    protected String lastMessage;
    private String currentMatchName;
    
    public ACPlayer (MinecadeAccount account, Player bukkitPlayer){
        this.bukkitPlayer = bukkitPlayer;
        this.minecadeAccount = account;
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
    /**
     * Sets the playerModel
     * @author kunamo
     */
    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }
    /**
     * Gets the minecadeAccount
     * @return minecadeAccount
     * @author kunamo
     */
    public MinecadeAccount getMinecadeAccount() {
        return this.minecadeAccount;
    }

    /**
     * Sets the minecadeAccount
     * @author kunamo
     */
    public void setMinecadeAccount(MinecadeAccount minecadeAccount) {
        this.minecadeAccount = minecadeAccount;
    }
    /**
     * Get character
     * @return character
     * @author kvnamo
     */
    public CharacterEnum getCharacter(){
        return this.character;
    }
    
    /**
     * Set character
     * @param character
     * @author kvnamo
     */
    public void setCharacter(CharacterEnum character){
        this.character = character;
    }
    /**
     * Gets the lives
     * @return lives
     * @author kunamo
     */
    public int getLives() {
        return this.lives;
    }
    /**
     * Sets the lives
     * @author kunamo
     */
    public void setLives(int livesleft) {
        this.lives = livesleft;
    }

    /**
     * Is cooling
     * @return cooling
     * @author Kvnamo
     */
    public boolean isCooling(){
        return this.cooling;
    }
    
    /**
     * Set cooling
     * @param cooling
     * @author Kvnamo
     */
    public void setCooling(boolean cooling){
        this.cooling = cooling;
    }
    /**
     * Gets the lastMessage
     * @return lastMessage
     * @author kunamo
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Sets the lastMessage
     * @author kunamo
     */
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
}
