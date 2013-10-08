package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
                player.getBukkitPlayer().teleport(this.acWorld.getShipLocation());
                player.setCharacter(CharacterEnum.ASSASSIN);
                ACCharacter.setupPlayer(player);
            }
            // Set navy
            else player.getBukkitPlayer().teleport(this.acWorld.getNavyLocation());
            
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
        synchronized(this.players){
            for(ACPlayer player : this.players.values()){
                // TODO: Get winner and save in Database
                
                // Clear player
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                
                player.setLives(0);
                player.setCooling(false);
                player.setCharacter(null);
                player.setCurrentMatch(null);
            }
        }
        
        // Clear collections and task
        this.players.clear();
        this.prisioners.clear();
        this.timerTask.cancel();
    }
    
    /**
     * On player quit
     * @param playerName
     * @author Kvnamo
     */
    public void playerQuit(final ACPlayer playerName) {
        
        // Remove from players list
        ACPlayer player = this.players.get(playerName.getBukkitPlayer().getName());
        this.players.remove(playerName.getBukkitPlayer().getName());
        
        // Save player stats
        player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
        player.getPlayerModel().setTimePlayed(player.getPlayerModel().getTimePlayed() + this.time - this.countdown);
        this.plugin.getPersistence().updatePlayer(player.getPlayerModel());
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())) this.finish();
    }
    
    /**
     * On entity death
     * @param player
     * @param event
     * @author kvnamo
     */
    public void entityDeath(final EntityDeathEvent event, final ACPlayer player) {
        
        player.setLives(player.getLives() - 1);

        if(!CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            
            if(event.getEntity().getKiller() instanceof Player){
                
                ACPlayer assassin = this.players.get(event.getEntity().getKiller().getName());
            
                if(CharacterEnum.ASSASSIN.equals(assassin.getCharacter())){
                    // Gains 2 levels when killing a player
                    assassin.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 2);
                }
            }
        }
    }
    
    /**
     * On player respawn.
     * @param event
     * @author: kvnamo
     */
    public void playerRespawn(final PlayerRespawnEvent event, final ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            // Get current assassin lives
            int lives = player.getLives(); 
            
            if(player.getLives() > 0){
                ACCharacter.setupPlayer(player);
                player.setLives(lives);
                event.setRespawnLocation(this.acWorld.getShipLocation());
            }
            else this.finish();  
        }
        else{
            // Clear player inventory
            EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
            
            // Add to prision
            this.prisioners.add(player);
            event.setRespawnLocation(this.acWorld.getKillBoxLocation());
            
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
     * On entity damaged
     * @param event
     * @param player
     * @author Kvnamo
     */
    public void entityDamage(final EntityDamageEvent event, final ACPlayer player) {
        
        // Falling will not damage
        if(DamageCause.FALL.equals(event.getCause())){
            event.setCancelled(true);
            return;
        }
        else if(DamageCause.VOID.equals(event.getCause())){
            player.getBukkitPlayer().setHealth(0.0D);
            event.setCancelled(true);
            return;
        }
        
        // The assassin can get damaged by anyone
        if(CharacterEnum.ASSASSIN.equals(player)) return;
        
        // Get enemy
        final ACPlayer enemy = this.players.get(((Player)((EntityDamageByEntityEvent) event).getDamager()).getName());
        
        // The assassin can damaged anyone
        if(CharacterEnum.ASSASSIN.equals(enemy)) return;
        
    }
    
    /**
     * Release prisioners from jail
     * @author Kvnamo
     */
    private void releasePrisioners(){
        
        synchronized (this.prisioners) {    
            for(ACPlayer player : this.prisioners){
                player.getBukkitPlayer().teleport(this.acWorld.getNavyLocation());
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
    public void playerMove(final PlayerMoveEvent event, final ACPlayer player) {
        
        final Block block = event.getTo().getBlock();
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            
            if(MatchStatusEnum.RUNNING.equals(this.status)){
                // Pressure plates on diamond blocks throughout the map give the assassin 1 level of experience
                if(Material.DIAMOND_BLOCK.equals(block.getType())){
                    player.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 1);
                    block.setType(Material.SAND);
                }
            }
        }
        else this.characterSelection(player);
    }
    
    /**
     * Character selection
     * @param player
     * @author Kvnamo
     */
    private void characterSelection(final ACPlayer player){
        
        if(this.acWorld.getBodyguardLocation().equals(player.getBukkitPlayer().getLocation())){
            player.setCharacter(CharacterEnum.BODYGUARD);
            ACCharacter.setupPlayer(player);
        }
        else if(this.acWorld.getMusketeerLocation().equals(player.getBukkitPlayer().getLocation())){
            player.setCharacter(CharacterEnum.MUSKETEER);
            ACCharacter.setupPlayer(player);
        }
        else if(this.acWorld.getSwordsmanLocation().equals(player.getBukkitPlayer().getLocation())){
            player.setCharacter(CharacterEnum.SWORDSMAN);
            ACCharacter.setupPlayer(player);
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
