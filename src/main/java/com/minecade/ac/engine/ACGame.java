package com.minecade.ac.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.task.InvisibilityTask;
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

    private List<ACMatch> matches;
    
    private int serverMatches;

    private Location lobby;
    
    private int maxPlayers;

    private int vipPlayers;

    private int countdown; 
    
    private Location lobbyLocation;
    
    /**
     * return lobby location in game.
     * @return
     * @author Kvnamo
     */
    public Location getLobbyLocation(){
        return lobbyLocation;
    }
    
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
            this.plugin.getServer().getLogger().severe("initWorld: world parameter is null");
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
    public void playerJoin(final PlayerJoinEvent event) {
        
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
            // FIXME Load all posible matches
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
                        
                        // if match players is reached break
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
        
        if(match != null) match.init(this.nextMatchPlayers);
    }

    /**
     * On player quit
     * @param event
     * @author kvnamo
     */
    public void playerQuit(final PlayerQuitEvent event){
        
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
     * On entity death.
     * @param event
     * @author: kvnamo
     */
    public void entityDeath(final EntityDeathEvent event) {
        
        if(event.getEntity() instanceof Player){
        
            final ACPlayer player = this.players.get(((Player)event.getEntity()).getName());
            final ACMatch match = player.getCurrentMatch();
            
            // If the player is in the lobby do nothing
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
                match.playerDeath(event, player);
            }
        }
        else if (event.getEntity() instanceof Zombie && event.getEntity().getKiller() instanceof Player){
            
            final ACPlayer killer = this.players.get(((Player)event.getEntity().getKiller()).getName());
            final ACMatch match = killer.getCurrentMatch();
            
            // If the player is in the lobby do nothing
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
                match.npcDeath(event, (Zombie)event.getEntity(), killer);
            }
        }
    }
    
    /**
     * On player respawn.
     * @param event
     * @author: kvnamo
     */
    public void playerRespawn(final PlayerRespawnEvent event){
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && !MatchStatusEnum.STOPPED.equals(match.getStatus())){
            match.playerRespawn(event, player);
        }
        else event.setRespawnLocation(this.lobbyLocation); 
    }
    
    /**
     * On entity damage
     * @param event
     * @author Kvnamo
     */
    public void entityDamage(final EntityDamageEvent event) {
        
        // If the player was damaged
        if(event.getEntity() instanceof Player){
            
            final ACPlayer player = this.players.get(((Player)event.getEntity()).getName());
            final ACMatch match = player.getCurrentMatch();
            
            // If the player is in a match
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
                match.playerDamage(event, player);
                return;
            } 
            
            // If the player is in the lobby do nothing
            if(DamageCause.VOID.equals(event.getCause())){
                event.getEntity().teleport(this.lobbyLocation);
            }
            
            event.setCancelled(true);
        }
        else if (event.getEntity() instanceof Zombie && event.getEntity().getLastDamageCause() instanceof Player){
            final ACPlayer damager = this.players.get(((Player)event.getEntity().getLastDamageCause()).getName());
            final ACMatch match = damager.getCurrentMatch();
            
            // If the player is in the lobby do nothing
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
                match.npcDamage(event, damager);
            }
        }
    } 
    
    /**
     * On the player moves
     * @param event
     * @author kvnamo
     */
    public void playerMove(final PlayerMoveEvent event) {
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());
        final ACMatch match = player.getCurrentMatch();
        
        // If the player is in the lobby do nothing
        if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            match.playerMove(event, player);
        }
    }
    
    /**
     * On player interact event
     * @param event
     * @author Kvnamo
     */
    public void playerInteract(final PlayerInteractEvent event) {
        
        // Get item in hand
        Player bukkitPlayer = event.getPlayer();
        ItemStack itemInHand = event.getPlayer().getItemInHand();
        
        // Check item used with right click.
        if (Action.RIGHT_CLICK_AIR.equals(event.getAction())) {

            if(ACInventory.getLeaveCompass().getType().equals(itemInHand.getType())){
                EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
                return;
            }
            else if(ACInventory.getInvisibleEmerald().getType().equals(itemInHand.getType())){
                final ACPlayer player = this.players.get(bukkitPlayer.getName());
                final ACMatch match = player.getCurrentMatch();
                
                if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) && 
                    CharacterEnum.ASSASSIN.equals(player.getCharacter()) && !player.isCooling()){
                    
                    // Start invisibility.
                    new InvisibilityTask(player, 1).runTaskTimer(plugin, 10, 200l);
                }
            }
        }
        else if(Action.RIGHT_CLICK_BLOCK.equals(event.getAction())){
            final ACPlayer player = this.players.get(bukkitPlayer.getName());
            final ACMatch match = player.getCurrentMatch();
            
            if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) && 
                CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                ACShop.shop(bukkitPlayer);
            }
            else event.setCancelled(true);
        }
        else if(event.getClickedBlock() != null && !event.getPlayer().isOp()) event.setCancelled(true);
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
     * Go to top shop
     * @param bukkitPlayer
     * @author Kvnamo
     */
    public void goToTopShop(final Player bukkitPlayer){
        
        final ACPlayer player = this.players.get(bukkitPlayer.getName());
        final ACMatch match = player.getCurrentMatch();
        
        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            bukkitPlayer.teleport(match.getACWorld().getShipLocation());
        }
    }
    
    /**
     * Go to lower shop
     * @param bukkitPlayer
     * @author Kvnamo
     */
    public void goToLowerShop(final Player bukkitPlayer){
        
        final ACPlayer player = this.players.get(bukkitPlayer.getName());
        final ACMatch match = player.getCurrentMatch();
        
        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus())){
            bukkitPlayer.teleport(match.getACWorld().getLowerShopLocation());
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
