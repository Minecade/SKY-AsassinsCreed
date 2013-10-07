package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.task.MatchTimerTask;
import com.minecade.ac.world.ACWorld1;
import com.minecade.engine.utils.EngineUtils;


public class ACMatch {
  
    private AssassinsCreedPlugin plugin;
    
    private int time;
    
    private int countdown;
    
    private MatchTimerTask timerTask;
    
    private ACScoreboard acScoreboard;
    
    private Map<String, ACPlayer> players;
    //private MatchTimerTask timerTask;
    //private ReleasePrisionersTask releasePrisionersTask;
    private List<ACPlayer> prisioners = new ArrayList<ACPlayer>();
    
    private MatchStatusEnum status = MatchStatusEnum.STOPPED;
    
    /**
     * Match status
     * @return status
     * @author Kvnamo
     */
    public MatchStatusEnum getStatus(){
        return this.status;
    }
    
    private ACWorld1 acWorld;
    
    /**
     * Get assassins creed world
     * @param world
     * @author Kvnamo
     */
    public ACWorld1 getACWorld(){
        return this.acWorld;
    }
    
    /**
     * Set assassins creed world
     * @param world
     * @author Kvnamo
     */
    public void setACWorld(ACWorld1 acWorld){
        this.acWorld = acWorld;
    }
    
    /**
     * ACMatch constructor
     * @param plugin
     * @param time
     * @author Kvnamo
     */
    public ACMatch(AssassinsCreedPlugin plugin){
        this.plugin = plugin;
        this.time = plugin.getConfig().getInt("match.time");
    }
    
    /**
     * Init match 
     * @param players
     * @author Kvnamo
     */
    public void init(List<ACPlayer> players){
        
        // Start game timer.
        if(this.timerTask != null) this.timerTask.cancel();
        this.timerTask = new MatchTimerTask(this, 10);
        this.timerTask.runTaskTimer(plugin, 10, 20l);
        
        // Set player characteristics.
        ACPlayer player;
        
        for (int i = 0; i < players.size(); i++) {
            // Get player
            player = players.get(i);
            
            // Set assassin
            if(i == 0){
                player.setCharacter(CharacterEnum.ASSASSIN);
                ACCharacter.setupPlayer(player);
                //TODO: player.getBukkitPlayer().teleport(destination);
            }
            // Set navy
            //TODO: else player.getBukkitPlayer().teleport(destination);
            
            this.players.put(player.getBukkitPlayer().getName(), player);
        }

        // Set match status
        this.status = MatchStatusEnum.READY;
    }
    
    /**
     * Start match
     * @author kvnamo
     */
    public void start(){
        
        //Match timer
        if(this.timerTask != null) this.timerTask.cancel();
        this.timerTask = new MatchTimerTask(this, 10);
        this.timerTask.runTaskTimer(plugin, 10, 20l);
        
        // Set match status
        this.status = MatchStatusEnum.RUNNING;
    }
    
    /**
     * Finish match
     * @author kvnamo
     */
    private void finish(){

        // Remove all 
        this.players.clear();
        this.prisioners.clear();
        this.timerTask.cancel();
    }
    
    /**
     * On player quit
     * @param playerName
     * @author Kvnamo
     */
    public void playerQuit(ACPlayer playerName) {
        
        // Remove from players list
        ACPlayer player = this.players.get(playerName.getBukkitPlayer().getName());
        this.players.remove(playerName.getBukkitPlayer().getName());
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            // Save player stats
            player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
            player.getPlayerModel().setTimePlayed(player.getPlayerModel().getTimePlayed() + this.time - this.countdown);
            this.plugin.getPersistence().updatePlayer(player.getPlayerModel());

            this.finish();
        }
        else{
            // TODO: penalize the navy
        }
    }
    
    /**
     * When the player dies
     * @param player
     * @param event
     * @author kvnamo
     */
    public void playerDeath(final ACPlayer player) {
        player.setLives(player.getLives() - 1);
    }
    
    /**
     * On player respawn.
     * @param event
     * @author: kvnamo
     */
    public void playerRespawn(final ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            // Get current assassin lives
            int lives = player.getLives(); 
            
            if(player.getLives() > 0){
                ACCharacter.setupPlayer(player);
                player.setLives(lives);
                //TODO:player.getBukkitPlayer().teleport(this.);
            }
            else this.finish();  
        }
        else{
            // Clear player inventory
            EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
            
            // Add to prision
            this.prisioners.add(player);
            //TODO:player.getBukkitPlayer().teleport();
            
            if(this.prisioners.size() == 1){
                // Every 20 seconds everyone currently in the room gets teleported to the class select room 
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        ACMatch.this.releasePrisioners();
                    }
                }, 400L);
            }
        }
    }
    
    /**
     * Release prisioners from jail
     * @author Kvnamo
     */
    private void releasePrisioners(){
        
        synchronized (this.prisioners) {    
            for(ACPlayer player : this.prisioners){
              //player.getBukkitPlayer().teleport();
            }
            
            this.prisioners.clear();
        }
    }
    
    /**
     * On the player moves
     * @param player
     * @param event
     * @author kvnamo
     */
    public void playerMove(final ACPlayer player, PlayerMoveEvent event) {
        
        final Block block = event.getTo().getBlock();
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            // Pressure plates on diamond blocks throughout the map give the assassin 1 level of experience
            if(Material.DIAMOND_BLOCK.equals(block.getType())){
                player.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 1);
                block.setType(Material.SAND);
            }
        }
        
        if(!Material.AIR.equals(block.getRelative(BlockFace.DOWN).getType())){
            player.setInAir(false);
        }
    }
     
    /**
     * On player super jump
     * @param player
     * @author kvnamo
     */
    public void playerSuperJump(final ACPlayer player) {
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter()) && !player.isInAir()){ 
            
            player.setInAir(true);
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.IRONGOLEM_THROW, 3, -3);
            player.getBukkitPlayer().setVelocity(player.getBukkitPlayer().getLocation().getDirection().multiply(0.15).setY(0.5));
        }
    }
    
    /**
     * Gains 2 levels when killing a player
     * @param player
     * @author kvnamo 
     */
    private void playerKill(final ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            player.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 2);
        }
    }
    
    /**
     * Gains 3 levels when killing an NPC
     * @param player
     * @author kvnamo 
     */
    private void npcKill(final ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            player.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 3);
        }
    }

    /**
     * Show player 
     * @param player
     * @author kvnamo
     */
    public void showPlayer(Player bukkitPlayer){
        
        synchronized(this.players){
            for (ACPlayer player : this.players.values()) {
                player.getBukkitPlayer().showPlayer(bukkitPlayer);
            }
        }
    }
    
    /**
     * Hide player
     * @param players
     * @param spectator
     * @author kvnamo
     */
    public void hidePlayer(Player bukkitPlayer){
        
        synchronized (this.players) {
            for (ACPlayer player : this.players.values()) {
                player.getBukkitPlayer().hidePlayer(bukkitPlayer);
            }
        }
    }
    
    /**
     * Lobby time left to start a match
     * @param countdown
     * @author Kvnamo
     */
    public void timeLeft(int countdown) {
        
        this.countdown = countdown;
        this.acScoreboard.setTimeLeft(countdown);
        
        if(this.countdown < 6) return;
        
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 3, -3);
        } 
    }
}
