package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.enums.NPCEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.task.MatchAnnouncementsTask;
import com.minecade.ac.task.MatchTimerTask;
import com.minecade.ac.world.ACWorld;
import com.minecade.engine.utils.EngineUtils;

public class ACMatch {
  
    private AssassinsCreedPlugin plugin;
    
    private int time;
    
    private int countdown;
    
    private int npcs;
    
    private ACScoreboard acScoreboard;
    
    private Map<String, ACPlayer> players;
    
    private List<ACPlayer> prisioners = new ArrayList<ACPlayer>();
    
    private MatchTimerTask timerTask;
    
    private MatchAnnouncementsTask announcementsTask;
    
    private MatchStatusEnum status = MatchStatusEnum.STOPPED;
    
    /**
     * Match status
     * @return status
     * @author Kvnamo
     */
    public MatchStatusEnum getStatus(){
        return this.status;
    }
    
    private ACWorld acWorld;

    /**
     * Get assassins creed world
     * @param world
     * @author Kvnamo
     */
    public ACWorld getACWorld(){
        return this.acWorld;
    }
    
    /**
     * Set assassins creed world
     * @param world
     * @author Kvnamo
     */
    public void setACWorld(ACWorld acWorld){
        this.acWorld = acWorld;
    }
    
    /**
     * ACMatch constructor
     * @param plugin
     * @param acWorld
     * @author Kvnamo
     */
    public ACMatch(AssassinsCreedPlugin plugin, ACWorld acWorld, int matchPlayers){
        this.plugin = plugin;
        this.acWorld = acWorld;
        
        // Initialize properties
        this.players =  new ConcurrentHashMap<String, ACPlayer>(matchPlayers);
    }
    
    /**
     * Init match 
     * @param match players
     * @author Kvnamo
     */
    public void init(List<ACPlayer> matchPlayers){
        
        this.time = this.plugin.getConfig().getInt("match.time");
        
        // Set match scoreboard
        if(this.acScoreboard == null)  this.acScoreboard = new ACScoreboard(this.plugin, false);
        this.acScoreboard.init();
        
        // Load players
        ACPlayer player;
        
        for (int i = 0; i < matchPlayers.size(); i++) {
            
            // Get player and put it in list
            player = matchPlayers.get(i);
            this.players.put(player.getBukkitPlayer().getName(), player);
            
            // Set assassin
            if(i == 0){
                
                // Start timer.
                if(this.timerTask != null) this.timerTask.cancel();
                this.timerTask = new MatchTimerTask(this, player.getBukkitPlayer(), 30);
                this.timerTask.runTaskTimer(this.plugin, 10, 20l);
                
                player.getBukkitPlayer().teleport(this.acWorld.getShipLocation());
                player.setCharacter(CharacterEnum.ASSASSIN);
                ACCharacter.assassin(player);
                
                // Setup assassin scoreboard lives
                this.acScoreboard.setAssassinLives(player.getLives());
                
                // Message
                player.getBukkitPlayer().sendMessage(String.format(
                    "%s%sYou are an Assassin. MISSION: Assassinate the 5 victims.", 
                    ChatColor.RED, ChatColor.BOLD));
            }
            // Set navy
            else{
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                player.getBukkitPlayer().teleport(this.acWorld.getNavyRoomLocation());
                
                 // Message
                player.getBukkitPlayer().sendMessage(String.format(    
                    "%sChoose one Royal Navy character to begin.", ChatColor.BLUE));
            }
            
            // Setup player scoreboard
            this.acScoreboard.assignCharacterTeam(player);
            player.getBukkitPlayer().setScoreboard(this.acScoreboard.getScoreboard());
        }
        
        // Load npc.
        NPCEnum npc;
        Location location;
        this.npcs = NPCEnum.values().length;

        for (int i = 0; i < this.npcs; i++) {
            npc = NPCEnum.values()[i];
            location = this.getACWorld().getNPCLocation(npc);
            
            // Spawn npc
            ACCharacter.zombie((Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE), npc);
        }
        
        // Set match scoreboard
        this.acScoreboard.setNavy(this.players.size() - 1);
        this.acScoreboard.setNPCs(this.npcs);
        this.acScoreboard.setPrisioners(0);
                    
        // Set match status
        this.status = MatchStatusEnum.READY;
    }
    
    /**
     * Start match
     * @author kvnamo
     */
    public void start(){
        
        // Start timer
        synchronized(this.players){
            for(ACPlayer player : this.players.values()){
                
                // Get assassin
                if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                    //Match timer
                    if(this.timerTask != null) this.timerTask.cancel();
                    this.timerTask = new MatchTimerTask(this, player.getBukkitPlayer(), this.time);
                    this.timerTask.runTaskTimer(plugin, 10, 20l);
                    break;
                }
            }
        }

        // Set match status
        this.status = MatchStatusEnum.RUNNING;
        
        // Announcements
        this.broadcastMessage(String.format("%sMatch started!", ChatColor.RED));
        
        if(this.announcementsTask != null) this.announcementsTask.cancel();
        this.announcementsTask = new MatchAnnouncementsTask(this.plugin, this);
        this.announcementsTask.runTaskTimer(this.plugin, 600l, 600l);
    }
    
    /**
     * Finish match
     * @author kvnamo
     */
    public void finish(){

        synchronized(this.players){
            for(ACPlayer player : this.players.values()){
                
                // Get winner and save in Database
                if(CharacterEnum.ASSASSIN.equals(player.getCharacter()) && player.getLives() > 0){
                    
                    // Set a win if the assassin killed every target
                    if(this.npcs == 0){
                        // Update Butter Coins in central DB
                        addButterCoins(player.getBukkitPlayer().getName(), 5);
                        player.getPlayerModel().setWins(player.getPlayerModel().getWins() + 1);
                    }
                    else player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
                }
                else{
                    // Set a win for the navy if at least one target (npc) is alive
                    if(this.npcs > 0){
                        // Update Butter Coins in central DB
                        addButterCoins(player.getBukkitPlayer().getName(), 5);
                        player.getPlayerModel().setWins(player.getPlayerModel().getWins() + 1);
                    }
                    else player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
                }
                
                // Update player in database
                player.getPlayerModel().setTimePlayed(player.getPlayerModel().getTimePlayed() + (this.time - this.countdown));
                this.plugin.getPersistence().updatePlayer(player.getPlayerModel());
                
                // Clear player
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                
                player.setLives(0);
                player.setCooling(false);
                player.setCharacter(null);
                player.setCurrentMatch(null);
            }
        }

        // Start finish match timer.
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                // Get game
                ACGame game = ACMatch.this.plugin.getGame();

                synchronized(ACMatch.this.players){
                    for(ACPlayer player : ACMatch.this.players.values()){
                        // Setup scoreboard
                        game.getACScoreboard().assignPlayerTeam(player);
                        
                        // Teleport to lobby and load defaults
                        player.getBukkitPlayer().setScoreboard(game.getACScoreboard().getScoreboard());
                        player.getBukkitPlayer().teleport(game.getLobbyLocation());
                        player.loadLobbyInventory();
                        
                        // Preinit match if
                        game.preInitNextMatch();
                    }
                }
                
                // Announce match winners in lobby
                ACMatch.this.plugin.getGame().broadcastMessage(String.format("%sThanks for playing! The %s wins!", 
                    ACMatch.this.npcs == 0 ? ChatColor.RED : ChatColor.BLUE, ACMatch.this.npcs == 0 ? "Assassin" : "Navy"));
                
                // Clear all
                ACMatch.this.players.clear();
                ACMatch.this.prisioners.clear();
                ACMatch.this.timerTask.cancel();
                ACMatch.this.announcementsTask.cancel();
                ACMatch.this.acScoreboard = null;
                
                // Set match status
                ACMatch.this.status = MatchStatusEnum.STOPPED;
            }
        }, 150L);
        
        // Announce finish
        this.broadcastMessage(String.format("%sMatch finished!", ChatColor.RED));
    }
    
    /**
     * On player quit
     * @param playerName
     * @author Kvnamo
     */
    public void playerQuit(final ACPlayer player) {
        
        // Remove from players list
        this.players.remove(player.getBukkitPlayer().getName());
        
        // Save player stats
        player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
        player.getPlayerModel().setTimePlayed(player.getPlayerModel().getTimePlayed() + this.time - this.countdown);
        this.plugin.getPersistence().updatePlayer(player.getPlayerModel());
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter()) || this.players.size() == 1) this.finish();
    }
    
    /**
     * On player death
     * @param player
     * @param event
     * @author kvnamo
     */
    public void playerDeath(final EntityDeathEvent event, final ACPlayer player) {
        
        player.setLives(player.getLives() - 1);
        
        // If the assassin died
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            
            if(player.getLives() > 0){
                // Update scoreboard
                this.acScoreboard.setAssassinLives(player.getLives());
            }
            else this.finish();  
        }
        
        // if it was a kill
        if(event.getEntity().getKiller() instanceof Player){
            
            ACPlayer killer = this.players.get(event.getEntity().getKiller().getName());
            
            if(CharacterEnum.ASSASSIN.equals(killer.getCharacter())){
                // Gains 2 levels when killing a player
                killer.getBukkitPlayer().setExp(killer.getBukkitPlayer().getExp() + 2);
                
                // Announce navy kill
                this.broadcastMessage(String.format("%sTown Crier: %s%s %sfrom Navy was sent to prision!", 
                    ChatColor.RED, ChatColor.BLUE, player.getBukkitPlayer().getName(), ChatColor.RED));
            }
            
            // Update butter coins
            addButterCoins(killer.getBukkitPlayer().getName(), 1);
        }
    }
    
    /**
     *  On npc death
     * @param event
     * @param zombie
     * @author Kvnamo
     */
    public void npcDeath(final EntityDeathEvent event, final Zombie zombie, final ACPlayer killer){
        
        // Update scoreboard
        this.acScoreboard.setNPCs(this.npcs--);
      
        // If all targets were killed, finish the match.
        if(this.npcs > 0){
            // Add 30 seconds to match
            this.timeLeft(this.countdown + 30);
            
            // Gains 2 levels when killing a player
            killer.getBukkitPlayer().setExp(killer.getBukkitPlayer().getExp() + 3);
            
            // Announce npc kill
            this.broadcastMessage(String.format("%sTown Crier: %s is death! %s 30 seconds added to the match!", 
                ChatColor.RED, zombie.getCustomName(), ChatColor.YELLOW));
        }
        else this.finish();
    }
    
    /**
     * On player respawn.
     * @param event
     * @author: kvnamo
     */
    public void playerRespawn(final PlayerRespawnEvent event, final ACPlayer player){
        
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            int lives = player.getLives();
            ACCharacter.assassin(player);
            player.setLives(lives);
            event.setRespawnLocation(this.acWorld.getShipLocation());
            return;
        }
        
        // Clear player inventory
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        
        // Add to prision
        player.setCharacter(null);
        this.prisioners.add(player);
        event.setRespawnLocation(this.acWorld.getKillBoxLocation());
        
        // Update scoreboard
        this.acScoreboard.setPrisioners(this.prisioners.size());
        this.acScoreboard.setNavy((this.players.size() - 1) - this.prisioners.size());
        
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
    
    /**
     * On player damaged
     * @param event
     * @param player
     * @author Kvnamo
     */
    public void playerDamage(final EntityDamageEvent event, final ACPlayer player) {
        
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
        if(CharacterEnum.ASSASSIN.equals(player.getCharacter())) return;
        
        if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player){
            // Get enemy
            final ACPlayer enemy = this.players.get(((Player)((EntityDamageByEntityEvent) event).getDamager()).getName());
            
            // The assassin can damaged anyone
            if(CharacterEnum.ASSASSIN.equals(enemy.getCharacter())) return;
        }
        
        event.setCancelled(true);
    }
    
    /**
     * Release prisioners from jail
     * @author Kvnamo
     */
    private void releasePrisioners(){
        
        synchronized (this.prisioners) {    
            for(ACPlayer player : this.prisioners){
                player.getBukkitPlayer().teleport(this.acWorld.getNavyRoomLocation());
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
        
        if(MatchStatusEnum.RUNNING.equals(this.status) && CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            
            // Pressure plates on diamond blocks throughout the map give the assassin 1 level of experience
            if(Material.DIAMOND_BLOCK.equals(block.getType())){
                player.getBukkitPlayer().setExp(player.getBukkitPlayer().getExp() + 1);
                block.setType(Material.SAND);
            }
        }
        else if(player.getCharacter() == null){
            this.characterSelection(player);
        }
    }
    
    /**
     * Character selection
     * @param player
     * @author Kvnamo
     */
    private void characterSelection(final ACPlayer player){

        Location location = player.getBukkitPlayer().getLocation().getBlock().getLocation();
        
        if(this.acWorld.getBodyguardLocation().equals(location)){
            player.getBukkitPlayer().teleport(this.acWorld.getNavyLocation());
            player.setCharacter(CharacterEnum.BODYGUARD);
            ACCharacter.bodyguard(player);
        }
        else if(this.acWorld.getMusketeerLocation().equals(location)){
            player.getBukkitPlayer().teleport(this.acWorld.getNavyLocation());
            player.setCharacter(CharacterEnum.MUSKETEER);
            ACCharacter.musketeer(player);
        }
        else if(this.acWorld.getSwordsmanLocation().equals(location)){
            player.getBukkitPlayer().teleport(this.acWorld.getNavyLocation());
            player.setCharacter(CharacterEnum.SWORDSMAN);
            ACCharacter.swordsman(player);
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
        
        if(this.countdown > 6) return;
        
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 3, -3);
        } 
    }

    /**
     * Hide player 
     * @param player
     * @author kvnamo
     */
    public void hidePlayer(Player bukkitPlayer, boolean hide){
        
        synchronized(this.players){
            for (ACPlayer player : this.players.values()) {
                if(hide) player.getBukkitPlayer().hidePlayer(bukkitPlayer);
                else player.getBukkitPlayer().showPlayer(bukkitPlayer);
            }
        }
    }
    
    /**
     * Broadcast lobby players message
     * @param format
     * @author Kvnamo
     */
    public void broadcastMessage(String message) {
        
        // Send message only to players in lobby
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().sendMessage(message);
        }
    }
    
    /**
     * Add butter coins in sky central db
     * @param playerName
     * @param butterCoins
     */
    private void addButterCoins(final String playerName, final int butterCoins){
        // Update Butter Coins in central DB
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ACMatch.this.plugin.getPersistence().addButterCoins(playerName, butterCoins);
            }
        });
    }
}
