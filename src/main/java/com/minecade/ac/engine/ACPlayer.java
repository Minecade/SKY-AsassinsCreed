package com.minecade.ac.engine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minecade.ac.data.PlayerModel;
import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.data.MinecadeAccount;
import com.minecade.engine.utils.EngineUtils;

public class ACPlayer{

    private Player bukkitPlayer;

    /**
     * Gets the bukkitPlayer
     * @return bukkitPlayer
     * @author kunamo
     */
    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    /**
     * Sets the bukkitPlayer
     * @author kunamo
     */
    public void setBukkitPlayer(Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    private PlayerModel playerModel;

    /**
     * Gets the playerModel
     * @return playerModel
     * @author kunamo
     */
    public PlayerModel getPlayerModel() {
        return this.playerModel;
    }

    /**
     * Sets the playerModel
     * @author kunamo
     */
    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }
    
    private MinecadeAccount minecadeAccount;

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
    
    private ACMatch currentMatch;
    
    /**
     * Get current match
     * @return currentMatch
     * @author kvnamo
     */
    public ACMatch getCurrentMatch(){
        return this.currentMatch;
    }
    
    /**
     * Set current match
     * @param currentMatch
     * @author kvnamo
     */
    public void setCurrentMatch(ACMatch currentMatch){
        this.currentMatch = currentMatch;
    }

    private CharacterEnum character;
    
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
    
    private int lives;

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
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    private int invisibilityTime;
    
    /**
     * Is invisibility time
     * @return invisibility time
     * @author Kvnamo
     */
    public int getInvisibilityTime(){
        return this.invisibilityTime;
    }
    
    /**
     * Set invisibility time
     * @param invisibility time
     * @author Kvnamo
     */
    public void setInvisibilityTime(int invisibilityTime){
        this.invisibilityTime = invisibilityTime;
    }
    
    private boolean cooling;
    
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

    protected String lastMessage;

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
     * Load player inventory
     * @author Kvnamo
     */
    public void loadLobbyInventory(AssassinsCreedPlugin plugin) {
        
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            
            @Override
            public void run() {
                EngineUtils.clearBukkitPlayer(ACPlayer.this.bukkitPlayer);
                ACPlayer.this.bukkitPlayer.getInventory().addItem(ACInventory.getInstructionsBook());
                ACPlayer.this.bukkitPlayer.getInventory().addItem(ACInventory.getStatsBook(ACPlayer.this));
                ACPlayer.this.bukkitPlayer.getInventory().addItem(ACInventory.getLeaveCompass());
            }
        });
    }
}
