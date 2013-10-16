package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
import com.minecade.engine.enums.PlayerTagEnum;
import com.minecade.engine.utils.EngineUtils;

public class ACGame {
    
    private static final String LOBBY = "lobby1";

    private final AssassinsCreedPlugin plugin;
    
    private int maxPlayers;

    private int vipPlayers;

    private int matchCountdown; 
    
    private int matchRequiredPlayers;  
    
    private List<ACMatch> matches;
    
    private Map<String, ACPlayer> players;
    
    private List<ACPlayer> nextMatchPlayers;
    
    private LobbyTimerTask timerTask;

    private Location lobby;
    
    /**
     * Get lobby location in game.
     * @return lobby location
     * @author Kvnamo
     */
    public Location getLobbyLocation(){
        return lobby;
    }
    
    private ACScoreboard acScoreboard;
    
    /**
     * return scoreboard
     * @return scoreboard
     * @author Kvnamo
     */
    public ACScoreboard getACScoreboard(){
        return this.acScoreboard;
    }
    
    /**
     * Player to start a next match
     * @return players needed to start
     * @author Kvnamo
     */
    private int getPlayersToStart(){
        
        int playersToStart = this.matchRequiredPlayers;
        
        // Send message only to players in lobby
        for (ACPlayer player : this.players.values()) {
            
            // Subtract players to start
            if(player.getCurrentMatch() == null) playersToStart--;
            
            // Check if we can start
            if(playersToStart == 0) break;
        }
        
        return playersToStart;
    }
    
    /**
     * ACGame constructor
     * @param plugin
     * @author kvnamo
     */
    public ACGame(AssassinsCreedPlugin plugin) {
        
        this.plugin = plugin;
        
        // Load properties
        this.maxPlayers = plugin.getConfig().getInt("server.max-players");
        this.vipPlayers = plugin.getConfig().getInt("server.max-vip-players");
        this.matchRequiredPlayers = plugin.getConfig().getInt("match.required-players");
        
        // Register scoreboard
        this.acScoreboard = new ACScoreboard(this.plugin, true);
        this.acScoreboard.init();
        
        // Initialize properties
        this.players =  new ConcurrentHashMap<String, ACPlayer>(this.vipPlayers);
        this.nextMatchPlayers = new ArrayList<ACPlayer>(this.matchRequiredPlayers);
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
        
        // Init lobby
        if (plugin.getConfig().getString("lobby.name").equalsIgnoreCase(world.getName())) {
            this.lobby = EngineUtils.locationFromConfig(this.plugin.getConfig(), world, "lobby.spawn");
            world.setSpawnLocation(this.lobby.getBlockX(), this.lobby.getBlockY(), this.lobby.getBlockZ());
        }
    }
    
    /**
     * Init the matches.
     * @author Kvnamo
     */
    public void initMatches() {
        
        // Init matches
        this.matches = new ArrayList<ACMatch>();
        this.matches.add(new ACMatch(this.plugin, this.plugin.getACWorld(0), this.matchRequiredPlayers));
        this.matches.add(new ACMatch(this.plugin, this.plugin.getACWorld(1), this.matchRequiredPlayers));
        this.matches.add(new ACMatch(this.plugin, this.plugin.getACWorld(2), this.matchRequiredPlayers));
        this.matches.add(new ACMatch(this.plugin, this.plugin.getACWorld(3), this.matchRequiredPlayers));
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
            
            // Assign player score board
            this.acScoreboard.assignPlayerTeam(player);
            bukkitPlayer.setScoreboard(this.acScoreboard.getScoreboard());
            
            // Add player to players collection
            this.players.put(bukkitPlayer.getName(), player);
            bukkitPlayer.teleport(this.lobby); 

            // Start a match if there are enough players
            this.preInitNextMatch();
        }
        // If the server is full disconnect the player.
        else EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
    }
    
    /**
     * Preinit next match
     * @author Kvnamo
     */
    public synchronized void preInitNextMatch(){
        
        if(this.getPlayersToStart() == 0){
        
            this.matchCountdown = plugin.getConfig().getInt("match.start-countdown");
            
            // Start game timer.
            if(this.timerTask != null) this.timerTask.cancel();
            this.timerTask = new LobbyTimerTask(this, this.matchCountdown);
            this.timerTask.runTaskTimer(plugin, 10, 20l);
    
            // Get available match 
            for (ACMatch match : this.matches) {
                if(MatchStatusEnum.STOPPED.equals(match.getStatus())){
    
                    // Select next match players
                    for (ACPlayer player : this.players.values()) {
                        
                        if(player.getCurrentMatch() == null){
                            player.setCurrentMatch(match);
                            this.nextMatchPlayers.add(player);
                            
                            // Announce next match players
                            player.getBukkitPlayer().sendMessage(String.format("%sYou are going to the next match on %s!", 
                                ChatColor.YELLOW, match.getACWorld().getName()));
                            
                            // if match players is reached break
                            if(this.nextMatchPlayers.size() == this.matchRequiredPlayers) break;
                        }
                    }
                }
                
                // if match players is reached break
                if(this.nextMatchPlayers.size() == this.matchRequiredPlayers) break;
            }
        }
        
        // Update scoreboard
        this.acScoreboard.setPlayersToStart(this.getPlayersToStart());
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
            this.nextMatchPlayers.clear();
            this.timeLeft(this.matchCountdown);
        }
    }
    
    /**
     * Force start match
     * @author Kvnamo
     */
    public String forceStartMatch() {
        
        if(this.nextMatchPlayers.size() > 0){
            return "You can't do this rigth now. A match is about to start.";
        }
        
        ACMatch availablematch = null;
        List<ACPlayer> nextMatchPlayers = new ArrayList<ACPlayer>();
        
        // Get available match 
        for (ACMatch match : this.matches) {
            if(MatchStatusEnum.STOPPED.equals(match.getStatus())){

                availablematch = match; 
                        
                // Select next match players
                for (ACPlayer player : this.players.values()) {
                    
                    if(player.getCurrentMatch() == null){
                        player.setCurrentMatch(match);
                        nextMatchPlayers.add(player);
                        
                        // Announce next match players
                        player.getBukkitPlayer().sendMessage(String.format("%sYou are going to the next match on %s!", 
                            ChatColor.YELLOW, match.getACWorld().getName()));
                        
                        // if match players is reached break
                        if(nextMatchPlayers.size() == this.matchRequiredPlayers) break;
                    }
                }
            }
            
            // if match players is reached break
            if(nextMatchPlayers.size() == this.matchRequiredPlayers) break;
        }
       
        // Check if there is a match available
        if(availablematch != null){
            this.acScoreboard.setPlayersToStart(this.getPlayersToStart());
            availablematch.init(nextMatchPlayers);
            return null;
        }
        
        return "There is no match available now for doing this. Please wait for one of them to finish."; 
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
            // Update scoreboard
            this.acScoreboard.setPlayersToStart(this.getPlayersToStart());
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
            
            this.acScoreboard.setPlayersToStart(this.getPlayersToStart());
            this.broadcastMessage(String.format("%s%s %squit the game.", ChatColor.RED, playerName, ChatColor.GRAY));
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
        else if (event.getEntity().getKiller() instanceof Player){
            
            final ACPlayer killer = this.players.get(((Player)event.getEntity().getKiller()).getName());
            final ACMatch match = killer.getCurrentMatch();
            
            // If the player is in the lobby do nothing
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) && 
                CharacterEnum.ASSASSIN.equals(killer.getCharacter())){
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
        else event.setRespawnLocation(this.lobby); 
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
                event.getEntity().teleport(this.lobby);
            }
            
            event.setCancelled(true);
        }
        else if(event.getEntity() instanceof Zombie && DamageCause.ENTITY_ATTACK.equals(event.getCause())){
            
            final ACPlayer damager = this.players.get(((Player)((EntityDamageByEntityEvent) event).getDamager()).getName());
            final ACMatch match = damager.getCurrentMatch();
            
            // If the player is in the lobby do nothing
            if (match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) && 
                CharacterEnum.ASSASSIN.equals(damager.getCharacter())){
                return;
            }
        }
        
        event.setCancelled(true);
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
        if (match != null){
            match.playerMove(event, player);
        }
    }
    
    /**
     * On player interact 
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
                    new InvisibilityTask(player).runTaskTimer(plugin, 10, 200l);
                    
                    player.getBukkitPlayer().sendMessage(String.format(
                            "%sYou are invisible.", ChatColor.AQUA));
                }
                else bukkitPlayer.sendMessage(String.format(
                    "%sYou are cooling down. Wait for use invisibility again.", ChatColor.AQUA));                   
            }
        }
        else if(Action.RIGHT_CLICK_BLOCK.equals(event.getAction())){
            
            final ACPlayer player = this.players.get(bukkitPlayer.getName());
            final ACMatch match = player.getCurrentMatch();
            
            if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) && 
                CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                ACShop.shop(this.plugin, player);
            } 
            else event.setCancelled(true);
        }
        else if(event.getClickedBlock() != null && !event.getPlayer().isOp()) event.setCancelled(true);
    }
    
    /**
     * on projectile hit 
     * @param event
     * @author Kvnamo
     */
    public void projectileHit(ProjectileHitEvent event) {
        
        Projectile projectile = event.getEntity();

        // If player throws an Arrow remove it from world
        if(EntityType.ARROW.equals(projectile.getType())){
            projectile.remove();
        }
    }
    
    /**
     * Chat message formatting
     * @param event
     * @author Kvnamo
     */
    public void chatMessage(AsyncPlayerChatEvent event) {
        
        final ACPlayer player = this.players.get(event.getPlayer().getName());

        // Last message.
        if(StringUtils.isNotBlank(player.getLastMessage()) && player.getLastMessage().equals(event.getMessage().toLowerCase())){
            event.getPlayer().sendMessage(String.format("%sPlease don't send the same message multiple times!", ChatColor.GRAY));
            event.setCancelled(true);
            return;
        }

        player.setLastMessage(event.getMessage().toLowerCase());
        PlayerTagEnum playerTag = PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount());
        event.setFormat(playerTag.getPrefix() + ChatColor.WHITE + "%s" + ChatColor.GRAY + ": %s");
        
        final ACMatch match = player.getCurrentMatch();
        
        if(match != null && !MatchStatusEnum.STOPPED.equals(match.getStatus())){
            // Send message only to players in current match
            match.broadcastMessage(String.format("%s: %s", player.getBukkitPlayer().getName(), event.getMessage()));
        }
        else this.broadcastMessage(String.format("%s: %s", player.getBukkitPlayer().getName(), event.getMessage()));
        
        event.setCancelled(true);
    }
    
    /**
     * Lobby time left to start a match
     * @param countdown
     * @author Kvnamo
     */
    public void timeLeft(int countdown) {
        
        this.matchCountdown = countdown;
        this.acScoreboard.setTimeLeft(countdown);
        
        if(this.matchCountdown > 6) return;
        
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
        
        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) &&
            CharacterEnum.ASSASSIN.equals(player.getCharacter())){
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
        
        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) &&
            CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            bukkitPlayer.teleport(match.getACWorld().getLowerShopLocation());
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
            if(player.getCurrentMatch() == null || MatchStatusEnum.STOPPED.equals(player.getCurrentMatch().getStatus())){
                player.getBukkitPlayer().sendMessage(message);
            }
        }
    }
}
