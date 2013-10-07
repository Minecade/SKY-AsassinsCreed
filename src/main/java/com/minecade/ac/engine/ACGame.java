package com.minecade.ac.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.task.LobbyTimerTask;
import com.minecade.ac.world.ACWorld1;
import com.minecade.engine.utils.EngineUtils;

public class ACGame {
    
    private static final String LOBBY = "lobby1";

    private final AssassinsCreedPlugin plugin;
    
    private List<ACPlayer> nextMatchPlayers;
    
    private Map<String, ACPlayer> players;
    
    private ACScoreboard acScoreboard;
    
    private LobbyTimerTask timerTask;
    
    private int matchRequiredPlayers;
    
    private Location lobbyLocation;

    private List<ACMatch> matches;
    
    private int serverMatches;

    private Location lobby;
    
    private int maxPlayers;

    private int vipPlayers;

    private int countdown; 
    
    /**
     * ACGame constructor
     * @param plugin
     * @author kvnamo
     */
    public ACGame(AssassinsCreedPlugin plugin) {
        this.plugin = plugin;
        
        // Load properties
        this.serverMatches = plugin.getConfig().getInt("server.matches");
        this.maxPlayers = plugin.getConfig().getInt("server.max-players");
        this.vipPlayers = plugin.getConfig().getInt("server.max-vip-players");
        this.countdown = plugin.getConfig().getInt("lobby.start-countdown");
        this.matchRequiredPlayers = plugin.getConfig().getInt("match.required-players");
        
        // Register scoreboard
        this.acScoreboard = new ACScoreboard(this.plugin);
        this.acScoreboard.init();
        
        // Initialize properties
        this.players =  new ConcurrentHashMap<String, ACPlayer>(this.vipPlayers);
    }

    /**
     * Match world initialization
     * @param WorldInitEvent
     * @author: kvnamo
     */
    public void initWorld(WorldInitEvent event){
        
        World world = event.getWorld();

        // World can't be empty
        if (world == null){
            this.plugin.getServer().getLogger().severe("PMSMatch initWorld: world parameter is null");
            return;
        }  
        
        // if the map is the lobby
        if (plugin.getConfig().getString("lobby.name").equalsIgnoreCase(world.getName())) {
            this.lobby = EngineUtils.locationFromConfig(this.plugin.getConfig(), world, "lobby.spawn");
            world.setSpawnLocation(this.lobby.getBlockX(), this.lobby.getBlockY(), this.lobby.getBlockZ());
        }
    }
    
    /**
     * Call when player join the match
     * @param PlayerJoinEvent
     * @author kvnamo 
     */
    public void playerJoin(PlayerJoinEvent event) {
        
        final Player bukkitPlayer = event.getPlayer();

        // Player banned
        if(this.plugin.getPersistence().isPlayerBanned(bukkitPlayer.getName())){
            bukkitPlayer.kickPlayer(plugin.getConfig().getString("server.ban-message"));
            return;
        }  
        
        // Create player
        final ACPlayer player = new ACPlayer(this.plugin, bukkitPlayer);
        
        // Check if the server needs more players or if the player is VIP
        if(this.players.size() <= this.maxPlayers || (player.getMinecadeAccount().isVip() && this.players.size() <= this.vipPlayers)){
            // Load lobby inventory
            player.loadLobbyInventory();
            
            // Assign player scoreboard
            this.acScoreboard.assignTeam(player);
            bukkitPlayer.setScoreboard(this.acScoreboard.getScoreboard());
            
            // Add player to players collection
            this.players.put(bukkitPlayer.getName(), player);
            bukkitPlayer.teleport(this.lobbyLocation); 

            // Start a match if there are enough players
            if(this.players.size() >= this.matchRequiredPlayers){
                this.preInitNextMatch();
            }
            // Register scoreboard
            else this.acScoreboard.setPlayersToStart(this.matchRequiredPlayers - this.players.size());
        }
        // If the server is full disconnect the player.
        else EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
    }
    
    /**
     * Pre init next match
     * @author Kvnamo
     */
    public synchronized void preInitNextMatch(){
        
        // Start game timer.
        if(this.timerTask != null) this.timerTask.cancel();
        this.timerTask = new LobbyTimerTask(this, this.countdown);
        this.timerTask.runTaskTimer(plugin, 10, 20l);

        // Get available match
        ACMatch match = null;
        
        synchronized(this.matches){ 
            // Load all posible matches
            if(this.matches.size() < this.serverMatches){
                match = new ACMatch(this.plugin);
                match.setACWorld(new ACWorld1(this.plugin));
                this.matches.add(match);
            }
            
            match = null;
            
            for (ACMatch availableMatch : this.matches) {
                if(MatchStatusEnum.STOPPED.equals(match.getStatus())){
                    match = availableMatch;
                }
            }
        }
        
        // Check that there is a match available
        if(match != null){
            
            // Select next match players
            synchronized(this.players){
                for (ACPlayer player : this.players.values()) {
                    
                    if(player.getCurrentMatch() == null){
                        player.setCurrentMatch(match);
                        this.nextMatchPlayers.add(player);
                        
                        // Announce next match player
                        if(this.nextMatchPlayers.size() == this.matchRequiredPlayers) break;
                    }
                }
            }
            
            // Announce next match players
            this.broadcastMessage(
                String.format("%s%s %sare going to the next match on %s.", ChatColor.RED, 
                    this.nextMatchPlayers.toString(), ChatColor.DARK_GRAY, match.getACWorld().getName()));
        }
    }

    /**
     * Init next match
     * @author Kvnamo
     */
    public synchronized void initNextMatch(){

        ACMatch match = this.nextMatchPlayers.size() == this.matchRequiredPlayers ? 
            ((ACPlayer)this.nextMatchPlayers.get(0)).getCurrentMatch() : null;
        
        if(match != null){
            match.init(this.nextMatchPlayers);
        }
    }

    /**
     * On player quit
     * @param event
     * @author kvnamo
     */
    public void playerQuit(PlayerQuitEvent event){
        
        String playerName = event.getPlayer().getName();
        ACPlayer player = this.players.get(playerName);
        
        this.players.remove(playerName);
        
        // Get player match
        ACMatch match = player.getCurrentMatch();
        
        // The player is in the lobby
        if (match == null){
            this.broadcastMessage(String.format("%s%s %squit the game.", ChatColor.RED, playerName, ChatColor.GRAY));
        }
        else if(MatchStatusEnum.STOPPED.equals(match.getStatus())){
            this.nextMatchPlayers.remove(player);
            
            // Match is about to begin and we need a new player.
            synchronized(this.players){
                for(ACPlayer waitingPlayer : this.players.values()){
                    if(waitingPlayer.getCurrentMatch() == null){
                        this.nextMatchPlayers.add(waitingPlayer);
                        break;
                    }
                }
            }
            
            // If there is no player stop timer and wait.
            if(this.nextMatchPlayers.size() < this.matchRequiredPlayers){
                this.timerTask.cancel();
            }
            
            this.broadcastMessage(String.format("%s[%s] %squit the game.", ChatColor.RED, playerName, ChatColor.GRAY));
        }
        else match.playerQuit(player);
    }
    
    /**
     * On player death.
     * @param event
     * @author: kvnamo
     */
    public void playerDeath(PlayerDeathEvent event) {
        
        final ACPlayer player = this.players.get(event.getEntity().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            match.playerDeath(player);
        }
    }
    
    /**
     * On player respawn.
     * @param event
     * @author: kvnamo
     */
    public void playerRespawn(PlayerRespawnEvent event){
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            match.playerRespawn(player);
        }
    }
    
    /**
     * On the player moves
     * @param event
     * @author kvnamo
     */
    public void playerMove(PlayerMoveEvent event) {
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            match.playerMove(player, event);
        }
    }
    
    /**
     * On player super jump
     * @param event
     * @author Kvnamo
     */
    public void playerSuperJump(PlayerToggleFlightEvent event) {
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            match.playerSuperJump(player);
        }
    }
    
    /**
     * Go to top shop
     * @param bukkitPlayer
     * @author Kvnamo
     */
    public void goToTopShop(final Player bukkitPlayer){
        
        final ACPlayer player = this.players.get(bukkitPlayer.getName());
        final ACMatch match = player.getCurrentMatch();
        
        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            //bukkitPlayer.teleport();
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
    
    /**
     * Broadcast lobby players message
     * @param format
     * @author Kvnamo
     */
    private void broadcastMessage(String message) {
        
        // Send message only to players in lobby
        for (ACPlayer player : this.players.values()) {
            if(player.getCurrentMatch() == null || MatchStatusEnum.STOPPED.equals(player.getCurrentMatch().getStatus())){
                player.getBukkitPlayer().sendMessage(message);
            }
        }
    }
}
