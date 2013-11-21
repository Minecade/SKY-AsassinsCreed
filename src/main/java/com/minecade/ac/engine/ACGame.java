package com.minecade.ac.engine;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.minecade.engine.MinecadeWorld;
import com.minecade.engine.data.MinecadeAccount;
import com.minecade.engine.enums.PlayerTagEnum;
import com.minecade.engine.utils.EngineUtils;
import com.minecade.engine.utils.GhostManager;
import com.minecade.ac.data.PlayerModel;
import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.enums.ServerStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.world.ACWorld;
import com.minecade.ac.world.ACWorld1;
import com.minecade.ac.world.ACWorld2;
import com.minecade.ac.world.ACWorld3;
import com.minecade.ac.world.ACWorld4;

/**
 * @author kvnamo
 */
public class ACGame{

    /**
     * Lobby
     */
    private static final String LOBBY = "lobby1";

    private final AssassinsCreedPlugin plugin;

    private Map<String, ACPlayer> players;
    private Map<String, ACMatch> matches;
    private Location lobbySpawnLocation;
    private ACScoreboard acScoreboard;
    private int requiredPlayersToMatch;
    private int maxPlayers;
    private int vipPlayers;
    private ServerStatusEnum serverStatus;
    private Set<Class> worlds;
    private GhostManager ghostManager;
    private ACMatch currentMatch;

    public ACGame(final AssassinsCreedPlugin plugin) {

        this.plugin = plugin;
        
        // Load config properties
        this.maxPlayers = plugin.getConfig().getInt("server.max-players");
        this.vipPlayers = plugin.getConfig().getInt("server.vip-players");
        this.requiredPlayersToMatch = plugin.getConfig().getInt("match.required-players");

        this.players = new ConcurrentHashMap<String, ACPlayer>();

        // Initialize scoreboard
        this.acScoreboard = new ACScoreboard(this.plugin);

        // Initialize ghost manager
        this.ghostManager = new GhostManager(this.plugin, this.acScoreboard.getScoreboard());

        this.serverStatus = ServerStatusEnum.WAITING_FOR_PLAYERS;

        this.worlds = new HashSet<>();
        worlds.add(ACWorld.class);
        worlds.add(ACWorld1.class);
        worlds.add(ACWorld2.class);
        worlds.add(ACWorld3.class);
        worlds.add(ACWorld4.class);

        // maximun number of matchs will be the number of worlds
        this.matches = new ConcurrentHashMap<String, ACMatch>(this.worlds.size());
        
    }
    
    public void init(){
        this.initMatches();
        this.setNextMatch();
    }

    /**
     * Match world initialization
     * 
     * @param WorldInitEvent
     * @author: kvnamo
     */
    public void initWorld(WorldInitEvent event) {

        World world = event.getWorld();

        // World can't be empty
        if (world == null) {
            this.plugin.getServer().getLogger().severe("GBGame initWorld: world parameter is null.");
            return;
        }

        // Init lobby
        if (plugin.getConfig().getString("match.lobby-world-name").equalsIgnoreCase(world.getName())) {
            lobbySpawnLocation = EngineUtils.locationFromConfig(this.plugin.getConfig(), world, "lobby.spawn");
            world.setSpawnLocation(lobbySpawnLocation.getBlockX(), lobbySpawnLocation.getBlockY(), lobbySpawnLocation.getBlockZ());
        }
    }
    public synchronized void playerJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        this.playerJoin(bukkitPlayer);
    }
    
    public synchronized void playerJoin(final Player bukkitPlayer) {

        // Player banned
        if (this.plugin.getPersistence().isPlayerBanned(bukkitPlayer.getName())) {
            bukkitPlayer.kickPlayer(plugin.getConfig().getString("match.ban-message"));
            return;
        }
        if(this.plugin.getServer().getOnlinePlayers().length >= this.maxPlayers){
            this.serverStatus = ServerStatusEnum.FULL;
            plugin.getPersistence().updateServerStatus(ServerStatusEnum.FULL, ServerStatusEnum.FULL.name());
        }
        //check if some match is already free.
        if(this.serverStatus == ServerStatusEnum.FULL){
            this.setNextMatch();
        }
        // Create player
        MinecadeAccount account = plugin.getPersistence().getMinecadeAccount(bukkitPlayer.getName());
        final ACPlayer player = new ACPlayer(account, bukkitPlayer);

        switch (this.serverStatus) {
        case WAITING_FOR_PLAYERS:

            EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
            this.loadLobbyInventory(player);
            
            ghostManager.setGhost(player.getBukkitPlayer(), true);

            // Check if the server needs more players or if the player is VIP
            if(plugin.getServer().getOnlinePlayers().length < this.maxPlayers){
                
                this.players.put(bukkitPlayer.getName(), player);

                // Update scoreboard
                //this.acScoreboard.assignTeam(player);  // TODO if I enable this the players cannot see because they are on != teams
                bukkitPlayer.setScoreboard(this.acScoreboard.getScoreboard());
                this.acScoreboard.setLobbyPlayers(this.requiredPlayersToMatch - this.players.size());

                // Teleport to lobby location by default
                bukkitPlayer.teleport(this.lobbySpawnLocation);

                startMatch(false, this.currentMatch);
                return;
            } else {
                Bukkit.getLogger().severe(String.format("Disconect linea 181"));
                EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
            }
            break;
        case OFFLINE:
            // If the server is ofline disconnect the player.
            EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
            break;
        case FULL:
            if((player.getMinecadeAccount().isVip() || plugin.getPersistence().isPlayerStaff(player.getBukkitPlayer())) 
                    && (plugin.getServer().getOnlinePlayers().length < this.maxPlayers + this.vipPlayers)){
                
                this.loadLobbyInventory(player);
                //this player just can be an spectator
                this.players.put(bukkitPlayer.getName(), player);
                // Teleport to lobby location by default
                bukkitPlayer.teleport(this.lobbySpawnLocation);
                return;
            }
            // If the server is full disconnect the non-vip player.
            Bukkit.getLogger().severe(String.format("Disconect linea 202"));
            EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
            break;
        }
    }
    public String forceStartMatch(){
        if(this.serverStatus == ServerStatusEnum.WAITING_FOR_PLAYERS){
            this.startMatch(true, this.currentMatch);
            return null;
        }
        return "Server must be in Waiting For Players status to execute this command.";
    }

    /**
     * Start match
     * 
     * @author kvnamo
     */
    public void startMatch(boolean forced, ACMatch match) {

        if(match == null)
            return;
        // Check for required players.
        if(!forced){
            if (this.players.size() < this.requiredPlayersToMatch)
                return;
        }
        
        String playersMatch = "";

        // Send players to match.
        synchronized (this.players) {
            for (Iterator<ACPlayer> iterator = this.players.values().iterator(); iterator.hasNext();) {
                // Get and remove player from lobby list
                ACPlayer player = iterator.next();
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                iterator.remove();

                this.acScoreboard.unassignTeam(player);

                // add player to match player list
                match.playerJoin(player);
                //player.setCurrentMatchName(match.getMatchName());
                
                // remove invisibility
                ghostManager.setGhost(player.getBukkitPlayer(), false);

                playersMatch = StringUtils.isBlank(playersMatch) ? player.getBukkitPlayer().getName() : playersMatch + ", "
                        + player.getBukkitPlayer().getName();
            }
            
            this.broadcastMessageToGroup(
                    match.getPlayers().values(),
                    String.format("%s%s%s %sare going to the next match on %s.", ChatColor.RED, ChatColor.BOLD, playersMatch, ChatColor.DARK_GRAY,
                            match.getMatchName()));

            //match.getAcScoreboard().setMatchPlayers(match.getPlayers().size());
            
            match.init();
        }
    }

    /**
     * When player exits or gets kicked out of the match
     * 
     * @param PlayerQuitEvent
     *            .
     * @author kvnamo
     */
    public synchronized void playerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        ACPlayer player = this.players.get(playerName);
        // The player is in a match
        if (player == null) {
            player = getPlayerFromMatch(playerName);
            if(player != null){
                ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                if(match != null){
                    event.setQuitMessage(null);
                    match.playerQuit(player);
                    return;
                }
            } else {
                Bukkit.getLogger().severe(String.format("This player is not in match playerLists or spectatorList neither in lobby playerList: %s", playerName));
            }
            //maybe some players of a starting match was redirected to the game.
            this.acScoreboard.setLobbyPlayers(this.requiredPlayersToMatch - this.players.size());
            this.startMatch(false, this.currentMatch);
            return;
        }

        // The player is in the lobby
        this.players.remove(playerName);
        
        ghostManager.removePlayer(player.getBukkitPlayer());

        // Update scoreboard
        this.acScoreboard.setLobbyPlayers(this.requiredPlayersToMatch - this.players.size());
        this.acScoreboard.unassignTeam(player);
        
        if(this.plugin.getServer().getOnlinePlayers().length < this.maxPlayers) {
            plugin.getPersistence().updateServerStatus(ServerStatusEnum.WAITING_FOR_PLAYERS, this.currentMatch.getMatchName());
        }
    }

    public void entityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            switch (event.getCause()) {
            case VOID:
                String playerName = ((Player) event.getEntity()).getName();
                ACPlayer player = this.players.get(playerName);

                // cancel event when player is in lobby
                if (player != null) {
                    player.getBukkitPlayer().teleport(lobbySpawnLocation);
                    event.setCancelled(true);
                } else {
                    // The player is in a match
                    player = this.getPlayerFromMatch(playerName);
                    if(player != null){
                        ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                        if(match != null){
                            match.entityDamagedByVoid(player);
                            event.setCancelled(true);
                        }
                    }
                    return;
                }
                break;
            case FALL:
                event.setCancelled(true);
                break;
            default:
                break;
            }
        }
    }

    public void entityDamagedByEntity(EntityDamageByEntityEvent event) {

        // victim was a player
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            ACPlayer victimPlayer = this.getPlayerFromMatch(playerName);

            // Player is in the lobby
            if (this.players.get(playerName) != null && victimPlayer == null) {
                // all damages in the lobby must be cancelled
                event.setCancelled(true);
                return;
            }
            ACMatch match = this.getMatchFromPlayer(playerName);
            if (match != null) {
                match.entityDamageByEntityEvent(event);
            }
        }
        // damager was a player
        if (event.getDamager() instanceof Player) {
            String damagerName = ((Player) event.getDamager()).getName();
            ACPlayer damagerPlayer = this.getPlayerFromMatch(damagerName);

            // Player is in the lobby
            if (this.players.get(damagerName) != null && damagerPlayer == null) {
                // all damages in the lobby must be cancelled
                event.setCancelled(true);
                return;
            }

            if (null != damagerPlayer) {
                ACMatch match = this.getMatchFromPlayer(damagerPlayer.getBukkitPlayer().getName());
                if (match != null) {
                    // victim was a zombie or any entity
                    match.entityDamagedByDamagerPlayer(event, damagerPlayer);
                }
            }
        }
        //damager was a arrow
        if(event.getDamager() instanceof Arrow){
            if(event.getEntity() instanceof Player){
                String playerName = ((Player) event.getEntity()).getName();
                ACPlayer victimPlayer = this.getPlayerFromMatch(playerName);
                // Player is in the lobby
                if (this.players.get(playerName) != null && victimPlayer == null) {
                    // all damages in the lobby must be cancelled
                    event.setCancelled(true);
                    return;
                }
                ACMatch match = this.getMatchFromPlayer(playerName);
                if (match != null) {
                    match.entityDamagedByOtherEntity(victimPlayer, event);
                }
            }
            if(event.getEntity() instanceof Zombie){
                event.setCancelled(true);
            }
        }
    }

    public void playerDeath(PlayerDeathEvent event) {

        String playerName = ((Player) event.getEntity()).getName();
        ACPlayer player = this.players.get(playerName);
        // The player is in a match
        if (player == null) {
            player = getPlayerFromMatch(playerName);
            if(player != null){
                ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                if(match != null){
                    match.playerDeath(event, player);
                }
            }
            return;
        }
    }

    public void playerRespawn(final PlayerRespawnEvent event) {

        String playerName = ((Player) event.getPlayer()).getName();
        ACPlayer player = this.players.get(playerName);
        // The player is in a match
        if (player == null) {
            player = getPlayerFromMatch(playerName);
            if(player != null){
                ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                if(match != null){
                    match.playerRespawn(event, player);
                }
            }
            return;
        }
    }

    /**
     * On player move
     * 
     * @param event
     * @author kvnamo
     */
    public void playerMove(PlayerMoveEvent event) {

        String playerName = ((Player) event.getPlayer()).getName();
        ACPlayer player = this.players.get(playerName);

        // The player is in a match
        if (player == null) {
            player = getPlayerFromMatch(playerName);
            if(player != null){
                ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                if(match != null){
                    match.playerMove(event, player);
                }
            }
            return;
        }
    }
    
    public void entityTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    public void entityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {

            String playerName = ((Player) event.getEntity().getKiller()).getName();
            ACPlayer player = this.players.get(playerName);
            // The player is in a match
            if (player == null) {
                player = getPlayerFromMatch(playerName);
                if(player != null){
                    ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                    if(match != null){
                        match.entityDeath(event, player);
                    }
                }
                return;
            }
        }
    }

    /**
     * On entity combust
     * 
     * @param event
     * @author kvnamo
     */
    public void entityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Zombie)
            event.setCancelled(true);
    }


    public void creatureSpawn(CreatureSpawnEvent event) {

        if (event.getEntity() instanceof Zombie) {
            if (event.getSpawnReason() == SpawnReason.CUSTOM)
                return;
        }
        event.setCancelled(true);
    }

    private ACPlayer getPlayerFromMatch(String playerName) {
        ACPlayer player = null;
        synchronized (this.matches) {
            for (ACMatch match : this.matches.values()) {
                // Get match player
                player = match.getPlayers().get(playerName);
                if (player != null) {
                    //player.setCurrentMatchName(match.getMatchName());
                    return player;
                }
                player = match.getSpectators().get(playerName);
                if (player != null) {
                    //player.setCurrentMatchName(match.getMatchName());
                    return player;
                }
            }
        }
        return null;
    }

    private ACMatch getMatchFromPlayer(String playerName) {
        synchronized (this.matches) {
            for (ACMatch match : this.matches.values()) {
                if (match.getPlayers().containsKey(playerName) || match.getSpectators().containsKey(playerName))
                    return match;
            }
        }
        return null;
    }

    /**
     * Get next match
     * 
     * @return next match
     * @author kvnamo
     */
    public void setNextMatch() {
        // Get available match
        ACMatch nextMatch = null;

        synchronized (this.matches) {
            Bukkit.getLogger().severe(String.format("Map of matchs has %s matchs stored: ", this.matches.size()));
            for (ACMatch match : this.matches.values()) {
                Bukkit.getLogger().severe(String.format("The match stored %s has status %s ", match.getMatchName(), match.getStatus()));
                if (MatchStatusEnum.STOPPED.equals(match.getStatus()) && nextMatch == null) {
                    nextMatch = match;
                    this.currentMatch =  nextMatch;
                    nextMatch.setStatus(MatchStatusEnum.STARTING_MATCH);
                    Bukkit.getLogger().severe(String.format("The match stored %s has status %s will be the next match", match.getMatchName(), match.getStatus()));
                }
            }
        }
        if (nextMatch == null){
            Bukkit.getLogger().severe(String.format("Server Full, all matchs are running"));
            this.serverStatus = ServerStatusEnum.FULL;
            this.plugin.getPersistence().updateServerStatus(ServerStatusEnum.FULL, ServerStatusEnum.FULL.name());
            this.currentMatch = null;
            return;
        }
        this.serverStatus = ServerStatusEnum.WAITING_FOR_PLAYERS;
        this.plugin.getPersistence().createOrUpdateServer(this.currentMatch.getMatchName());
    }

    public void rightClick(PlayerInteractEvent event) {
        if (event.getPlayer() instanceof Player) {

            String playerName = ((Player) event.getPlayer()).getName();
            ACPlayer player = this.players.get(playerName);

            // The player is in a match
            if (player == null) {
                player = getPlayerFromMatch(playerName);
                if(player != null){
                    ACMatch match = this.getMatchFromPlayer(player.getBukkitPlayer().getName());
                    if(match != null){
                        match.rightClick(event);
                    }
                }
                return;
            } else {
                ItemStack itemInHand = player.getBukkitPlayer().getItemInHand();
                if (itemInHand != null) {
                    if (ACInventory.getLeaveCompass().getType().equals(itemInHand.getType())){
                        EngineUtils.disconnect(player.getBukkitPlayer(), LOBBY, null);
                    }
                }
            }
        }
    }
    
    public void projectileHit(ProjectileHitEvent event) {
        
        final Projectile projectile = event.getEntity();

        // If player throws an Arrow remove it from world
        if(EntityType.ARROW.equals(projectile.getType())){
            projectile.remove();
        }
    }
    
    public void entityShootBowEvent(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(null != player){
                ACPlayer acPlayer = this.players.get(player.getName());
                // The player is in a match
                if (acPlayer == null) {
                    acPlayer = getPlayerFromMatch(player.getName());
                    if(acPlayer != null){
                        ACMatch match = this.getMatchFromPlayer(acPlayer.getBukkitPlayer().getName());
                        if(match != null){
                            match.playerShootBow(event, acPlayer);
                        }
                    }
                    return;
                }
            }
        }
    }
    
    public void inventoryOpenEvent(InventoryOpenEvent event){
        if(event.getPlayer() instanceof Player){
            Player bukkitPlayer =  (Player)event.getPlayer();
            
            ACPlayer player = this.players.get(bukkitPlayer.getName());

            // The player is in a match
            if (player == null) {

                player = getPlayerFromMatch(bukkitPlayer.getName());
                if(player == null){
                    event.setCancelled(true);
                    return;
                }
            }
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
        if(bukkitPlayer != null){
            if(event.getPlayer() instanceof Player){
                Player player = event.getPlayer();
                if(null != player){
                    ACPlayer acPlayer = this.players.get(player.getName());
                    // The player is in a match
                    if (acPlayer == null) {
                        acPlayer = getPlayerFromMatch(player.getName());
                        if(acPlayer != null){
                            ACMatch match = this.getMatchFromPlayer(acPlayer.getBukkitPlayer().getName());
                            if(match != null){
                                match.onPlayerInteract(event, acPlayer);
                            }
                            return;
                        }
                        event.setCancelled(true);
                    } else {
                        //player is in lobby
                        ItemStack itemInHand = event.getPlayer().getItemInHand();
                        if(ACInventory.getLeaveCompass().getType().equals(itemInHand.getType())){
                            EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void chatMessage(AsyncPlayerChatEvent event){
        if (event.getPlayer() instanceof Player) {
            
            ACPlayer player = this.players.get(event.getPlayer().getName());
            //player in in AC lobby
            if(this.players.get(event.getPlayer().getName()) != null){
                player = this.players.get(event.getPlayer().getName());
                // Last message.
                if (StringUtils.isNotBlank(player.getLastMessage()) && player.getLastMessage().equals(event.getMessage().toLowerCase())) {
                    event.getPlayer().sendMessage(String.format("%sPlease don't send the same message multiple times!", ChatColor.GRAY));
                    event.setCancelled(true);
                }
                
                player.setLastMessage(event.getMessage().toLowerCase());
                PlayerTagEnum playerTag = PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount());
                event.setFormat(playerTag.getPrefix() + ChatColor.WHITE + "%s" + ChatColor.GRAY + ": %s");
                return;
            } else {
                ACPlayer acPlayer = this.getPlayerFromMatch(event.getPlayer().getName());
                // The player is in a match
                if (acPlayer != null) {
                    ACMatch match = this.getMatchFromPlayer(acPlayer.getBukkitPlayer().getName());
                    if(match != null){
                        match.chatMessage(event);
                    }
                    return;
                }
            }
            event.setCancelled(true);
        }
    }
    
    public void goToLowerShop(final Player bukkitPlayer){
        
//        final ACPlayer player = this.players.get(bukkitPlayer.getName());
//        final ACMatch match = player.getCurrentMatch();
//        
//        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) &&
//            CharacterEnum.ASSASSIN.equals(player.getCharacter())){
//            bukkitPlayer.teleport(((ACWorld) match.getMinecadeWorld()).getLowerShopLocation());
//        }
    }
    
    public void goToTopShop(final Player bukkitPlayer){
        
//        final ACPlayer player = this.players.get(bukkitPlayer.getName());
//        final ACMatch match = player.getCurrentMatch();
//        
//        if(match != null && MatchStatusEnum.RUNNING.equals(match.getStatus()) &&
//            CharacterEnum.ASSASSIN.equals(player.getCharacter())){
//            bukkitPlayer.teleport(((ACWorld) match.getMinecadeWorld()).getTopShopLocation());
//        }
    }

    /**
     * Loads the player stats
     * 
     * @author jdgil
     */
    private ItemStack getPlayerStats(ACPlayer player) {

        ItemStack stats = ACInventory.getStatsBook(player);
        BookMeta statsMeta = (BookMeta) stats.getItemMeta();

        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        PlayerModel model = player.getPlayerModel();
        String kdr = (double) model.getLosses() == 0 ? "0" : decimalFormat.format((double) model.getKills()
                / (double) model.getLosses());
        String timePlayed = decimalFormat.format((double) model.getTimePlayed() / (double) 86400);

        statsMeta
                .setPages(String
                        .format("%s%s%s STATS! \n\n\n%s %sWins: %s%s\n %sKills: %s%s\n %sDeaths: %s%s\n %sLooses: %s%s\n %sButter Coins: %s%s\n %sKDR: %s%s\n %sTime played: %s%s days.",
                                ChatColor.BOLD, ChatColor.RED, player.getBukkitPlayer().getName().toUpperCase(), ChatColor.DARK_GRAY, ChatColor.BOLD,
                                ChatColor.DARK_GRAY, model.getWins(), ChatColor.BOLD, ChatColor.DARK_GRAY,
                                model.getKills(), ChatColor.BOLD, ChatColor.DARK_GRAY, model.getDeaths(), ChatColor.BOLD,
                                ChatColor.DARK_GRAY, model.getLosses(), ChatColor.BOLD, ChatColor.DARK_GRAY, player.getMinecadeAccount()
                                        .getButterCoins(), ChatColor.BOLD, ChatColor.DARK_GRAY, kdr, ChatColor.BOLD, ChatColor.DARK_GRAY, timePlayed));
        stats.setItemMeta(statsMeta);

        return stats;
    }
    
    /**
     * Load player inventory
     * @author Kvnamo
     */
    public void loadLobbyInventory(final ACPlayer player) {
        
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            
            @Override
            public void run() {
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getInstructionsBook());
                player.getBukkitPlayer().getInventory().addItem(ACGame.this.getPlayerStats(player));
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getLeaveCompass());
            }
        });
    }

    /**
     * Broadcast message to every player in match
     * 
     * @param message
     * @author kvnamo
     */
    private void broadcastMessage(String message) {
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().sendMessage(message);
        }
    }
    
    public void initMatches() {
        // Init matches
        for(Class<MinecadeWorld> world : this.worlds){
            this.matches.put(world.getSimpleName(), new ACMatch(this.plugin, world.getSimpleName(), world));
        }
    }

    /**
     * Broadcast message to group of players in game
     * 
     * @param message
     * @author jdgil
     */
    private void broadcastMessageToGroup(Collection<ACPlayer> players, String message) {
        for (ACPlayer player : players) {
            player.getBukkitPlayer().sendMessage(message);
        }
    }

    /**
     * @return the players
     */
    public Map<String, ACPlayer> getPlayers() {
        return players;
    }

    /**
     * @return the matches
     */
    public Map<String, ACMatch> getMatches() {
        return matches;
    }

    /**
     * @return the ghostManager
     */
    public GhostManager getGhostManager() {
        return ghostManager;
    }

    /**
     * @return the lobbySpawnLocation
     */
    public Location getLobbySpawnLocation() {
        return lobbySpawnLocation;
    }

    /**
     * @return the acScoreboard
     */
    public ACScoreboard getACScoreboard() {
        return acScoreboard;
    }

    /**
     * @return the requiredPlayersToMatch
     */
    public int getRequiredPlayersToMatch() {
        return requiredPlayersToMatch;
    }

    /**
     * @return the currentMatch
     */
    public ACMatch getCurrentMatch() {
        return currentMatch;
    }

    /**
     * @return the serverStatus
     */
    public ServerStatusEnum getServerStatus() {
        return serverStatus;
    }

}

