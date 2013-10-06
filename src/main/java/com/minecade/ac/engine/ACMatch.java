package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.engine.MinecadeWorld;
import com.minecade.engine.utils.EngineUtils;


public class ACMatch {
  
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
        
        // Set match status
        this.status = MatchStatusEnum.READY;
        
        // Load match players
        for(ACPlayer player : players){
            // TODO: set player characteristics.
            this.players.put(player.getBukkitPlayer().getName(), player);
        }
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
                //player.getBukkitPlayer().teleport();
            }
            else this.finish();  
        }
        else{
            // Clear player inventory
            EngineUtils.clearBukkitPlayer(this.player.getBukkitPlayer());
            
            // Add to prision
            this.prisioners.add(player);
            //player.getBukkitPlayer().teleport();
            
            if(this.prisioners.size() == 1){
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable{
                    @Override
                    public void run() {
                        this.releasePrisioners();
                    }
                }, 400L);
            }
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

            if(Material.DIAMOND_BLOCK.equals(block.getType())){
                player.getBukkitPlayer().setXP(player.getBukkitPlayer().getXP() + 1);
                block.setType(Material.SAND);
            }
        }
        
        if(!Material.AIR.equals(block.getRelative(BlockFace.DOWN).getType())){
            this.player.setInAir(false);
        }
    }
    
    
    /**
     * On player super jump
     * @param player
     * @author kvnamo
     */
    public void playerSuperJump(final ACPlayer player) {
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter()) && !player.getInAir()){ 
            player.setInAir(true);
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.IRONGOLEM_THROW, 3, -3);
            player.getBukkitPlayer().setVelocity(player.getBukkitPlayer().getLocation().getDirection().multiply(0.15).setY(0.5));
        }
    }
    
    /**
     * Release prisioners from jail
     * @author Kvnamo
     */
    public void releasePrisioners(){
        
        synchronized (this.prisioners) {    
            for(ACPlayer player : this.prisioners){
              //player.getBukkitPlayer().teleport();
            }
            
            this.prisioners.clear();
        }
    }
    
    /**
     * Gains 2 levels when killing a player
     * @author kvnamo 
     */
    private void playerKill(ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            this.bukkitPlayer.setExp(super.bukkitPlayer.getExp() + 2);
        }
    }
    
    /**
     * Gains 3 levels when killing an NPC
     * @author kvnamo 
     */
    private void npcKill(){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            this.bukkitPlayer.setExp(super.bukkitPlayer.getExp() + 3);
        }
    }

    private void timeLeft(){
        
        this.countdown--;
        
        
    }
}
