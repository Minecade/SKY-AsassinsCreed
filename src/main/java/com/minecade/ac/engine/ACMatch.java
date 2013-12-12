package com.minecade.ac.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.minecade.ac.data.PlayerModel;
import com.minecade.ac.engine.ACInventory.ShopItemEnum;
import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.enums.ServerStatusEnum;
import com.minecade.ac.enums.NPCEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.ac.task.MatchTimerTask;
import com.minecade.ac.world.ACBaseWorld;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;
import com.minecade.engine.enums.PlayerTagEnum;
import com.minecade.engine.settings.SettingsEnum;
import com.minecade.engine.settings.SettingsManager;
import com.minecade.engine.task.FireworksTask;
import com.minecade.engine.utils.EngineUtils;

public class ACMatch {
  
    private AssassinsCreedPlugin plugin;
    
    private List<Zombie> npcs;
    private int time;
    private int timeLeft;
    private int readyCountdown;
    private MatchTimerTask timerTask;
    private ACScoreboard acScoreboard;
    private String matchName;
    private Map<String, ACPlayer> players; 
    private Map<String, ACPlayer> spectators;
    private static final String LOBBY = "lobby1";
    private static int ASSASSIN_LIVES = 3;
    private static final String ASSASSIN_INVENTORY_TITLE = "Assassin Shop";
    private int requiredPlayersToMatch;
    private ACBaseWorld world;
    private MatchStatusEnum status;
    
    public ACMatch(AssassinsCreedPlugin plugin, String matchName, Class<MinecadeWorld> classWorld){
        this.plugin = plugin;
        this.matchName = matchName;
        this.npcs = new ArrayList<>();
        
        // Get config properties
        this.time = this.plugin.getConfig().getInt("match.time");
        
        this.readyCountdown = plugin.getConfig().getInt("match.ready-countdown");
        
        // Initialize properties
        this.players =  new ConcurrentHashMap<String, ACPlayer>();
        this.spectators = new ConcurrentHashMap<String, ACPlayer>();
        this.requiredPlayersToMatch = plugin.getConfig().getInt("match.required-players");
        this.status = MatchStatusEnum.STOPPED;
        
        this.acScoreboard = new ACScoreboard(this.plugin);
        // load world
        try {
            MinecadeWorld minecadeWorld = classWorld.getConstructor(MinecadePlugin.class).newInstance(this.plugin);
            this.world = (ACBaseWorld) minecadeWorld;
            Bukkit.getLogger().info(String.format("World was created sucessfully: %s", classWorld.getName()));
        } catch (Exception e) {
            Bukkit.getLogger().severe(String.format("Unable to create the world"));
            e.printStackTrace();
        }
    }
    
    public synchronized boolean playerJoin(ACPlayer player){
        if (player == null) {
            Bukkit.getLogger().severe(String.format("Player is null in match.playerJoin"));
            return false;
        }
        switch(this.status){
        case STARTING_MATCH:
            synchronized (this.players) {
                if(this.requiredPlayersToMatch > this.players.size()){
                    if (this.players.containsKey(player.getBukkitPlayer().getName())) {
                        player.getBukkitPlayer().sendMessage(String.format("Player %s is already in this match", player.getBukkitPlayer().getName()));
                        return false;
                    }
                    
                    this.players.put(player.getBukkitPlayer().getName(), player);
                    EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                    
                    this.acScoreboard.assignPlayerTeam(player);
                    //this.acScoreboard.setMatchPlayers(this.players.size());
                    player.getBukkitPlayer().setScoreboard(this.getAcScoreboard().getScoreboard());
                    return true;
                } else {
                    return false;
                }
            }
        case READY_TO_START:
            return false;
        case RUNNING:
            //add spectators
            if(player.getMinecadeAccount().isVip() || plugin.getPersistence().isPlayerStaff(player.getBukkitPlayer())){
                this.addSpectatorToMatch(player, this.world.getAssassinSpawn());
            } else {
                Bukkit.getLogger().severe(String.format("Disconect linea match 144"));
                this.teleportToLobby(player.getBukkitPlayer(), plugin.getConfig().getString("server.full-message"));
            }
            return false;
        case STOPPING:
        case STOPPED:
            Bukkit.getLogger().severe(String.format("Disconect linea match 149"));
            this.teleportToLobby(player.getBukkitPlayer(), null);
            return false;
        default:
            return false;
        }
    }
    
    public void onPlayerInteract(PlayerInteractEvent event, final ACPlayer player){
        
        switch(this.status){
        case STARTING_MATCH:
            break;
        case READY_TO_START:
            break;
        case STOPPING:
        case STOPPED:
            break;
        case RUNNING:
            if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.STONE_PLATE){
                if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                    // Pressure plates on diamond blocks throughout the map give the assassin 2 level of experience
                    Material materialStandPlayer = this.world.getWorld().getBlockAt(event.getClickedBlock().getLocation().getBlockX(), 
                            event.getClickedBlock().getLocation().getBlockY()-1, 
                            event.getClickedBlock().getLocation().getBlockZ()).getType();
                    if(materialStandPlayer.equals(Material.DIAMOND_BLOCK)){
                        Block block = event.getClickedBlock();
                        player.getBukkitPlayer().setLevel(player.getBukkitPlayer().getLevel() + 2);
                        block.setType(Material.AIR);
                        player.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%sYour experience has been %s%sincreased%s%s in %s2%s level!!", 
                                ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY, 
                                ChatColor.RED, ChatColor.BOLD, ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.AQUA, ChatColor.DARK_GRAY));
                    }
                }
                return;
            }
            if(player.getBukkitPlayer().getItemInHand() != null){
                ItemStack item = player.getBukkitPlayer().getItemInHand();
                //Emerald invisibility
                if(ACInventory.getInvisibleEmerald().getType().equals(item.getType())){
                    
                    if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                        if(!player.isCooling()){
                            // Start invisibility.
                            player.setCooling(true);
                            player.getBukkitPlayer().getInventory().setArmorContents(null);
                            player.getBukkitPlayer().setItemInHand(null);
                            int duration = 10;
                            if(player.isEmeraldImproved()){
                                duration = ShopItemEnum.IMPROVE_EMERALD.getDuration();
                            }
                            player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * duration, 1));
                            player.getBukkitPlayer().sendMessage(String.format("%sYou are invisible for the next %s%s%s seconds.", 
                                    ChatColor.DARK_GRAY, ChatColor.AQUA, duration, ChatColor.DARK_GRAY));
                            broadcastMessageToNavys(String.format("%sWatch out: %sAssassin %sis invisible for the next %s%s%s seconds!", 
                                    ChatColor.GOLD, ChatColor.RED, ChatColor.DARK_GRAY, ChatColor.AQUA, duration, ChatColor.DARK_GRAY));
                            //put the armor when invisibility has gone
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    player.getBukkitPlayer().getInventory().setArmorContents(ACInventory.getAssassinArmor());
                                    player.getBukkitPlayer().setItemInHand(ACInventory.getInvisibleEmerald());
                                }
                            }, 20 * duration);
                            //remove cooling down property after 30 seconds
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if(player.getCurrentMatchName() != null && player.getCurrentMatchName().equals(ACMatch.this.getMatchName()) && 
                                            ACMatch.this.getStatus().equals(MatchStatusEnum.RUNNING)){
                                        player.setCooling(false);
                                        player.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%sYou can use your %semerald %sagain", 
                                                ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY, 
                                                ChatColor.AQUA, ChatColor.DARK_GRAY));
                                    }
                                }
                            }, 20 * 30);
                        } else {
                            player.getBukkitPlayer().sendMessage(String.format("%sYou are still cooling down. Wait for use invisibility again.", ChatColor.AQUA));
                        }
                    }
                }
                //Smoke Bomb
                if(ACInventory.getSmokeBomb().getType().equals(item.getType())){
                    if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                        final int slot = player.getBukkitPlayer().getInventory().getHeldItemSlot();
                        //set the bomb in the same place 1 sec after it was thrown
                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if(!player.getBukkitPlayer().getInventory().contains(ACInventory.getSmokeBomb().getType())){
                                    player.getBukkitPlayer().getInventory().setItem(slot, ACInventory.getSmokeBomb());
                                }
                            }
                        }, 20);
                    }
                }
                //Shop
                if(ACInventory.getShopIcon().getType().equals(item.getType())){
                    if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                        final int slot = player.getBukkitPlayer().getInventory().getHeldItemSlot();
                        Inventory assassinShopInventory = plugin.getServer().createInventory(null, 9, ASSASSIN_INVENTORY_TITLE);
                        assassinShopInventory.setContents(ACInventory.getAssassinShopInventory(player));
                        player.getBukkitPlayer().openInventory(assassinShopInventory);
                        //set the bomb in the same place 1 sec after it was thrown
                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if(!player.getBukkitPlayer().getInventory().contains(ACInventory.getShopIcon().getType())){
                                    player.getBukkitPlayer().getInventory().setItem(slot, ACInventory.getShopIcon());
                                }
                            }
                        }, 10);
                    }
                    event.setCancelled(true);
                }
                //Place finder
                if(ACInventory.getPlaceFinder().getType().equals(item.getType())){
                    if(!this.spectators.containsKey(player.getBukkitPlayer().getName())){
                        ACPlayer assassin = this.getAssassin();
                        if(assassin != null && assassin.getBukkitPlayer().getName().equalsIgnoreCase(player.getBukkitPlayer().getName())){
                            Zombie target = this.findTargetsInMatch(assassin);
                            if(target == null){
                                assassin.getBukkitPlayer().sendMessage(
                                        String.format("%sYou don't have any target in the %smatch", ChatColor.DARK_GRAY, ChatColor.RED));
                                return;
                            } else {
                                assassin.getBukkitPlayer().sendMessage(
                                        String.format("%sThe Place finder is now pointing to the nearest %sTarget[%s%s%s]", ChatColor.DARK_GRAY, ChatColor.RED, 
                                                ChatColor.YELLOW, target.getCustomName(), ChatColor.RED));
                                return;
                            }
                        } else {
                            //compass was used by navy
                        }
                    } else {
                        //compass was used by spectator
                        Bukkit.getLogger().severe(String.format("Disconect linea 285"));
                        EngineUtils.disconnect(player.getBukkitPlayer(), LOBBY, null);
                    }
                    Bukkit.getLogger().severe(String.format("Compass Place Finder was used for a navy"));
                    return;
                }
            }
            if(Action.RIGHT_CLICK_BLOCK.equals(event.getAction())){
                if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                    //ACShop.shop(this.plugin, player);
                }
                else {
                    //event.setCancelled(true);
                }
            }
            break;
        default:
            break;
        }
    }
    
    public void inventoryClick(InventoryClickEvent event, final ACPlayer player){
        //Improve emerald
        if(event.getCurrentItem() != null && ACInventory.getImproveEmerald(player).getType().equals(event.getCurrentItem().getType())){
            if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                if(player.isEmeraldImproved()){
                    player.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%sYou already %sbuyed %sthis %sitem",  
                            ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.RED, 
                            ChatColor.DARK_GRAY, ChatColor.YELLOW));
                    player.getBukkitPlayer().closeInventory();
                } else {
                    if(player.getBukkitPlayer().getLevel() >= ShopItemEnum.IMPROVE_EMERALD.getCost()){
                        player.getBukkitPlayer().setLevel(player.getBukkitPlayer().getLevel() - ShopItemEnum.IMPROVE_EMERALD.getCost());
                        player.setEmeraldImproved(true);
                        player.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%sYour %sEmerald %swas improved, its effect will take %s%s%s seconds ",  
                                ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.RED, 
                                ChatColor.DARK_GRAY, ChatColor.AQUA, ShopItemEnum.IMPROVE_EMERALD.getDuration(), ChatColor.DARK_GRAY));
                        player.getBukkitPlayer().closeInventory();
                    } else {
                        int leftLevels = ShopItemEnum.IMPROVE_EMERALD.getCost() - player.getBukkitPlayer().getLevel();
                        player.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%sYou need %s%s%s %sexperience %slevels more to buy this item",  
                                ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.AQUA, leftLevels, 
                                ChatColor.RESET, ChatColor.RED, ChatColor.DARK_GRAY));
                        player.getBukkitPlayer().closeInventory();
                    }
                }
            }
        }
    }

    public void entityDamagedByVoid(ACPlayer player) {
        // cancel all kind of damage if player is not in a match
        switch (this.status) {
        case RUNNING:
            player.getBukkitPlayer().teleport(this.world.getNavyRandomSpawn());
            break;
        case STOPPING:
        case STOPPED:
        case STARTING_MATCH:
            player.getBukkitPlayer().teleport(plugin.getGame().getLobbySpawnLocation());
            break;
        case READY_TO_START:
            break;
        }
    }
    
    private void addSpectatorToMatch(ACPlayer player, Location respawnLocation){
        this.spectators.remove(player.getBukkitPlayer().getName());
        this.spectators.put(player.getBukkitPlayer().getName(), player);
        
        player.getBukkitPlayer().setAllowFlight(true);
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        this.hidePlayer(player.getBukkitPlayer());
        player.getBukkitPlayer().setCanPickupItems(false);
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getLeaveCompass());
        player.getBukkitPlayer().sendMessage(String.format("%sWelcome to %smatch %s%s%s", ChatColor.DARK_GRAY, ChatColor.RED, ChatColor.YELLOW, ChatColor.BOLD, this.matchName));
        String players = null;
        for (ACPlayer playerInMatch : this.players.values()) {
            players = StringUtils.isBlank(players) ? playerInMatch.getBukkitPlayer().getName() : players + ", " + playerInMatch.getBukkitPlayer().getName();
        }
        
        player.getBukkitPlayer().sendMessage(String.format("%sPlayers in this %smatch: %s%s", ChatColor.DARK_GRAY, ChatColor.RED, ChatColor.YELLOW, players));
        player.getBukkitPlayer().sendMessage(String.format("%s%sYou are now spectating the match!", ChatColor.DARK_PURPLE, ChatColor.BOLD));
        
        player.getBukkitPlayer().setScoreboard(this.getAcScoreboard().getScoreboard());
        //if spectator is added respawn location must be null, respawn event make the teleport to respawn point
        if(respawnLocation != null)
            player.getBukkitPlayer().teleport(respawnLocation);
        
        this.plugin.getServer().getLogger().severe(String.format("adding spectator to match: %s", player.getBukkitPlayer().getName()));
    }
    
    /**
     * Init match 
     */
    public void init(){
        
      //set next match to GBgame
        this.plugin.getGame().setNextMatch();
        
        // Start game timer.
        if(timerTask != null){
            timerTask.cancel();
        }
        this.timerTask = new MatchTimerTask(this, 10);
        this.timerTask.runTaskTimer(plugin, 1l, 20l);
        
        // Set match scoreboard
        if(this.acScoreboard == null)  this.acScoreboard = new ACScoreboard(this.plugin);
    }
    
    private ACPlayer selectAssassin(Collection<ACPlayer> players){
        if (players.size() <= 0){
            return null;
        }
        synchronized(this.players){
            Player assassinPlayer = plugin.getPassManager().selectPlayer(castListToPlayers(players));
            if(assassinPlayer != null){
                ACPlayer assassinACPlayer = this.players.get(assassinPlayer.getName());
                if(assassinACPlayer != null){
                    assassinACPlayer.setCharacter(CharacterEnum.ASSASSIN);
                    assassinACPlayer.setLives(ASSASSIN_LIVES);
                    return assassinACPlayer;
                }
            }
            Collection<ACPlayer> tempPlayers = new ArrayList<ACPlayer>(players);
            ACPlayer assassin = (ACPlayer) tempPlayers.toArray()[plugin.getRandom().nextInt(tempPlayers.size())];
            assassin.setCharacter(CharacterEnum.ASSASSIN);
            assassin.setLives(ASSASSIN_LIVES);
            return assassin;
        }
    }
    
    private Collection<Player> castListToPlayers(Collection<ACPlayer> players){
        Collection<Player> bukkitPlayers = new ArrayList<>();
        for(ACPlayer acPlayer : players){
            bukkitPlayers.add(acPlayer.getBukkitPlayer());
        }
        return bukkitPlayers;
    }
    
    public void prepareMatch(){
        
        //start this match
        this.status = MatchStatusEnum.READY_TO_START;
        
        final ACPlayer assassin = this.selectAssassin(this.players.values());
        final ArrayList<ACPlayer> navys = new ArrayList<>();
        
        if(assassin == null){
            Bukkit.getLogger().severe(String.format(
                    "ACMatch line 296: Something was wrong with selecting assassin, match must end, Players in match: %s and Match.status: %s", this.players.size(), this.status));
            //this.verifyGameover();
        }
        
        synchronized(this.players){
            for(ACPlayer player : this.players.values()){
                player.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                if(player.getBukkitPlayer().getName().equalsIgnoreCase(assassin.getBukkitPlayer().getName())){
                    //player is the assassin
                    player.getBukkitPlayer().teleport(this.world.getNavyRoomLocation());
                    player.setCharacter(CharacterEnum.ASSASSIN);
                    // Message
                    player.getBukkitPlayer().sendMessage(String.format(
                        "[%s%s%s]%s You will be the %sAssassin.", 
                        ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.RED, ChatColor.BOLD));
                    player.getBukkitPlayer().sendMessage(String.format(
                            "[%s%s%s]%s MISSION: Assassinate the %s%s5%s%s victims.",
                            ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, 
                            ChatColor.RED, ChatColor.YELLOW, ChatColor.BOLD, ChatColor.RESET, ChatColor.RED));
                    player.getBukkitPlayer().sendMessage(String.format(
                            "[%s%s%s] %s%sWatch out!%s %sthey have %s%s5%s%s navys watching them!",
                            ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RESET, 
                            ChatColor.GOLD, ChatColor.BOLD, ChatColor.RESET, ChatColor.RED, ChatColor.YELLOW, ChatColor.BOLD, ChatColor.RESET, ChatColor.RED));
                } else {
                    //player is navy
                    player.getBukkitPlayer().teleport(this.world.getNavyRoomLocation());
                     // Message
                    player.getBukkitPlayer().sendMessage(String.format(
                        "[%s%s%s]%s %sChoose%s%s one Royal Navy character.", ChatColor.YELLOW, player.getBukkitPlayer().getName(), 
                        ChatColor.RESET, ChatColor.RED, ChatColor.BOLD, ChatColor.RESET, ChatColor.RED));
                    navys.add(player);
                }
            }
            
            
            //teleport spectators to the match pre-lobby room
            for (ACPlayer spectator : this.spectators.values()) {
                spectator.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                spectator.getBukkitPlayer().teleport(this.world.getNavyRoomLocation());
            }
            this.broadcastMessage(String.format("%sMatch will begin in %s%s%s seconds", ChatColor.DARK_GRAY, ChatColor.RED, this.readyCountdown,
                    ChatColor.DARK_GRAY));
            this.broadcastMessageToSpectators(String.format("%sMatch will begin in %s%s%s seconds", ChatColor.DARK_GRAY, ChatColor.RED, this.readyCountdown,
                    ChatColor.DARK_GRAY));
            // Setup assassin scoreboard lives
            //ACMatch.this.acScoreboard.setAssassinLives(assassin.getLives());
            // non-critical scoreboard code, put it inside a task so if it fails, it
            // won't stop critical code.
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // set up scoreboard for the assassin and navys
                    ACMatch.this.acScoreboard.assignCharacterNavyTeam(navys);
                    ACMatch.this.acScoreboard.assignAssassinTeam(assassin);
                }
            });
        }
        // Start game timer.
        this.timeLeft(this.readyCountdown);
    }
    
    public void start(){
        this.status = MatchStatusEnum.RUNNING;
        this.timeLeft(this.time);
        // Start timer
        synchronized(this.players){
            for(ACPlayer player : this.players.values()){
                EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                // Get assassin
                if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                    ACCharacter.assassin(player);
                    player.getBukkitPlayer().teleport(this.world.getAssassinSpawn());
                    // Announcements
                    player.getBukkitPlayer().sendMessage(
                            String.format("%sYou are a %sAssassin, go to kill all the %s%s5%s %sobjetives!", ChatColor.DARK_GRAY, ChatColor.RED, 
                                    ChatColor.YELLOW, ChatColor.BOLD, ChatColor.RESET, ChatColor.DARK_GRAY));
                } else {
                    this.setupNavyType(player);
                    player.getBukkitPlayer().teleport(this.world.getNavyRandomSpawn());
                    player.getBukkitPlayer().sendMessage(
                            String.format("%sYou are a %s%sNavy%s [%s%s%s%s]", ChatColor.RED, ChatColor.BLUE, ChatColor.BOLD,
                                    ChatColor.RESET, ChatColor.YELLOW, ChatColor.BOLD, player.getCharacter().name(), ChatColor.RESET));
                    player.getBukkitPlayer().sendMessage(String.format("%s%sMISSION%s%s: protect all the %s%s5%s%s victims and kill the assassin!", 
                            ChatColor.YELLOW, ChatColor.BOLD, ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.YELLOW, ChatColor.BOLD, ChatColor.RESET, ChatColor.DARK_GRAY));
                }
            }
            
            //teleport spectators to the match world
            for (ACPlayer spectator : this.spectators.values()) {
                spectator.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                spectator.getBukkitPlayer().teleport(this.world.getNavyRoomLocation());
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                // Load npc.
                NPCEnum npc;
                Location location;
                int length = NPCEnum.values().length;
                
                for (int i = 0; i < length; i++) {
                    npc = NPCEnum.values()[i];
                    location = ACMatch.this.world.getNPCLocation(npc);
                    // Spawn npc
                    Zombie zombie = (Zombie) ACMatch.this.world.getWorld().spawnEntity(location, EntityType.ZOMBIE);
                    ACMatch.this.npcs.add(zombie);
                    ACCharacter.zombie(ACMatch.this.plugin, zombie, npc);
                }
            }
        }, 40l);
    }
    
    private void setupNavyType(ACPlayer navy) {

        if (navy.getCharacter() == null) {
            int selected;
            CharacterEnum[] types;
            selected = (int) Math.round((Math.random() * (CharacterEnum.navyValues().size() - 1)));
            types = CharacterEnum.navyValues().toArray(new CharacterEnum[CharacterEnum.navyValues().size()]);

            CharacterEnum type = types[selected];
            navy.setCharacter(type);
        }
        
        switch (navy.getCharacter()) {
        case BODYGUARD:
            ACCharacter.bodyguard(navy);
            break;
        case MUSKETEER:
            ACCharacter.musketeer(navy);
            break;
        case SWORDSMAN:
            ACCharacter.swordsman(navy);
            break;
        default:
            break;
        }
    }
    
    public void verifyGameover(){
        
        synchronized (this.players) {
            ACPlayer assassin = this.getAssassin();
            if((this.timeLeft <= 0) || assassin == null || (assassin != null && assassin.getLives() <= 0)){
                //navy team win
                this.status = MatchStatusEnum.STOPPING;
                this.timeLeft(10);
                this.timerTask.setCountdown(this.timeLeft);
                Bukkit.getLogger().severe(String.format("VerifyGameOver linea 576, players: %s", this.players.size()));
                String winners = null, winnerName = null;
                for (ACPlayer navy : this.players.values()) {
                    if(!CharacterEnum.ASSASSIN.equals(navy.getCharacter())){
                        winnerName = this.savePlayerWinner(navy, 3);
                        // Get winners
                        winners = StringUtils.isBlank(winners) ? winnerName : winners + ", " + winnerName;
                    } else {
                        if ((this.timeLeft <= 0))
                            navy.getBukkitPlayer().sendMessage(String.format("%sTime is out, You Lost the game!", ChatColor.RED));
                        else
                            navy.getBukkitPlayer().sendMessage(String.format("%sYou Lost the game!", ChatColor.RED));
                    }
                }
                this.broadcastMessage(String.format("%sThanks for playing! Winners: %s%s%s", ChatColor.RED, ChatColor.BOLD, ChatColor.YELLOW,
                        winners == null ? "None" : winners));
            } else {
                if(this.getNPCValid() <= 0 && this.status.equals(MatchStatusEnum.RUNNING)){
                    //assassin win
                    this.status = MatchStatusEnum.STOPPING;
                    this.timeLeft(10);
                    this.timerTask.setCountdown(this.timeLeft);
                    if(assassin != null){
                        this.savePlayerWinner(assassin, 3);
                        assassin.getBukkitPlayer().sendMessage(String.format("%sThanks for playing: %s%s%s, %s%sYou are the Winner!!!", ChatColor.RED, 
                                ChatColor.YELLOW, ChatColor.BOLD, assassin.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.RED));
                        this.broadcastMessageToNavys(String.format("%sThanks for playing, %sAssassin%s[%s%s%s%s] %sis the Winner!!!", ChatColor.DARK_GRAY, 
                                ChatColor.RED, ChatColor.RESET, ChatColor.YELLOW, ChatColor.BOLD, assassin.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.DARK_GRAY));
                    }
                }
            }
        }
    }
    
    private String savePlayerWinner(ACPlayer player, int butterCoins) {
        String winner = null;
        
        // Update Butter coins
        this.addButterCoins(player.getBukkitPlayer(), 5);
        
        // Save player stats
        //FIXME CODIGO ASINCRONO PARA ESTE GETTER
        PlayerModel model = player.getPlayerModel();
        model.setWins(model.getWins() + 1);
        model.setTimePlayed(model.getTimePlayed() + this.time - this.timeLeft);
        this.updatePlayer(player);
        // Get winner name
        winner = StringUtils.isBlank(winner) ? player.getBukkitPlayer().getName() : winner + ", " + player.getBukkitPlayer().getName();

        // Throw fireworks for winner
        new FireworksTask(player.getBukkitPlayer(), 10).runTaskTimer(this.plugin, 1l, 20l);

        return winner;
    }
    
     private ACPlayer getAssassin(){
         for(ACPlayer player : this.players.values()){
             if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                 return player;
             }
         }
         return null;
     }
     
     private int getAssassinLives(){
         for(ACPlayer player : this.players.values()){
             if(CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                 return player.getLives();
             }
         }
         return 0;
     }
    
    public void finish(boolean returnToLobby){

        synchronized (this.players) {
            
            // Send players and spectators to the main lobby or game lobby
            for (ACPlayer player : this.players.values()) {
                if (returnToLobby) {
                    Bukkit.getLogger().severe(String.format("Disconect match linea 645"));
                    EngineUtils.disconnect(player.getBukkitPlayer().getPlayer(), LOBBY, null);
                } else {
                    EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                    player.setCharacter(null);
                    //player.setCurrentMatchName(null);
                    player.setCooling(false);
                    player.setInJail(false);
                    player.setEmeraldImproved(false);
                    player.getBukkitPlayer().setLevel(0);
                    player.getBukkitPlayer().setExp(0);
                    this.acScoreboard.unassignTeam(player);
                }
            }
            
            for(ACPlayer player : this.spectators.values()){
                this.setPlayerRulesToSpectator(player);
            }
            //Send players and spectators to the GB lobby
            final Collection<ACPlayer> tempPlayers = new ArrayList<ACPlayer >(this.players.values());
            final Collection<ACPlayer> tempSpectators =  new ArrayList<ACPlayer >(this.spectators.values());
            
            this.players.clear();
            this.spectators.clear();
            for(Entity npcs : this.npcs){
                npcs.remove();
            }
            this.npcs.clear();
            this.status = MatchStatusEnum.STOPPED;
            this.timeLeft = this.time;
            this.timerTask = null;
            // Initialize scoreboard
            this.acScoreboard = new ACScoreboard(this.plugin);
            
            //clean the world and delete all old entities
            this.world.clear();
            
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    //send players and spectators to lobby after the match object is stopped and ready for a new match
                    for(ACPlayer lobbyPlayer : tempPlayers) {
                        if (lobbyPlayer.getBukkitPlayer().isValid()) {
                            // optional code
                            plugin.getGame().getACScoreboard().assignPlayerTeam(lobbyPlayer);
                            lobbyPlayer.getBukkitPlayer().setScoreboard(plugin.getGame().getACScoreboard().getScoreboard());
                            plugin.getGame().playerJoin(lobbyPlayer.getBukkitPlayer());
                        }
                    }
                    
                    for(ACPlayer spectator : tempSpectators) {
                        if (spectator.getBukkitPlayer().isValid()) {
                            // optional code
                            plugin.getGame().getACScoreboard().assignPlayerTeam(spectator);
                            spectator.getBukkitPlayer().setScoreboard(plugin.getGame().getACScoreboard().getScoreboard());
                            plugin.getGame().playerJoin(spectator.getBukkitPlayer());
                        }
                    }
                    if(ServerStatusEnum.FULL.equals(plugin.getGame().getServerStatus())){
                        plugin.getGame().setNextMatch();
                    }
                }
            });
        }
    }
    
    public synchronized void playerQuit(final ACPlayer player) {
        
        //show hidden players to player that quit
        for(ACPlayer playerToShow : this.spectators.values()){
            player.getBukkitPlayer().showPlayer(playerToShow.getBukkitPlayer());
        }
        
        //if player is spectator just remove and return, dont do anything else
        if (this.spectators.containsKey(player.getBukkitPlayer().getName())){
            this.spectators.remove(player.getBukkitPlayer().getName());
            this.setPlayerRulesToSpectator(player);
            return;
        }
        
        switch (this.status) {
        case STARTING_MATCH:
            this.players.remove(player.getBukkitPlayer().getName());
            // Check if starting players number is reached
            int playersRemaining = this.requiredPlayersToMatch - this.players.size();
            if (playersRemaining >= 0) {
                // Cancel begin timer task
                if (this.timerTask != null) {
                    this.timerTask.cancel();
                }

                // Update server status
                this.status = MatchStatusEnum.STOPPED;
                final Collection<ACPlayer> players = this.players.values();
                //optional code in tasks in order to avoid some problem in main thread
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    ACPlayer acPlayer = player;
                    @Override
                    public void run() {
                        for (ACPlayer playerMatch : players) {
                            EngineUtils.clearBukkitPlayer(playerMatch.getBukkitPlayer());
                            playerMatch.setCharacter(null);
                            //playerMatch.setCurrentMatchName(null);
                            ACMatch.this.plugin.getGame().getPlayers().put(playerMatch.getBukkitPlayer().getName(), playerMatch);
                            
                            // Update scoreboard of the game
                            ACMatch.this.acScoreboard.unassignTeam(playerMatch);
                            playerMatch.getBukkitPlayer().setScoreboard(plugin.getGame().getACScoreboard().getScoreboard());
                            ACMatch.this.plugin.getGame().getACScoreboard().assignPlayerTeam(playerMatch);
                            ACMatch.this.plugin.getGame().loadLobbyInventory(acPlayer);
                            ACMatch.this.plugin.getGame().getGhostManager().setGhost(acPlayer.getBukkitPlayer(), true);
                        }
                     }
                 });
                this.broadcastMessage(String.format("%s%s%s quit the game%s, Match was cancelled", ChatColor.YELLOW, player.getBukkitPlayer().getName(), ChatColor.RED, ChatColor.DARK_GRAY));
                this.finish(false);
            }
            break;
        case READY_TO_START:
        case RUNNING:
            // Remove from players list
            this.players.remove(player.getBukkitPlayer().getName());
            EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
            player.setCharacter(null);
            //player.setCurrentMatchName(null);
            player.setInJail(false);
            player.setCooling(false);
            player.setEmeraldImproved(false);
            player.getBukkitPlayer().setLevel(1);
            player.getBukkitPlayer().setExp(0);
            
            // Save player stats
            player.getPlayerModel().setLosses(player.getPlayerModel().getLosses() + 1);
            player.getPlayerModel().setTimePlayed(player.getPlayerModel().getTimePlayed() + this.time - this.timeLeft);
            this.updatePlayer(player);
            this.broadcastMessage(String.format("%sThe player %s[%s%s%s] %squit %sthe game", ChatColor.DARK_GRAY, ChatColor.RESET, ChatColor.YELLOW, 
                    player.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.RED, ChatColor.DARK_GRAY));
            this.verifyGameover();
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;

        }
    }
    
    public void entityDeath(final EntityDeathEvent event, ACPlayer killer) {
        
        switch(this.status){
        case READY_TO_START:
            break;
        case RUNNING:
            if(event.getEntity() instanceof Zombie){
                if(killer != null){
                    if(CharacterEnum.ASSASSIN.equals(killer.getCharacter())) {
                        event.setDroppedExp(0);
                        event.getDrops().clear();
                        Zombie zombie = (Zombie)event.getEntity();
                        this.npcs.remove(zombie);
                        // Gains 2 levels when killing a player
                        killer.getBukkitPlayer().setLevel(killer.getBukkitPlayer().getLevel() + 2);
                        killer.getBukkitPlayer().sendMessage(String.format("[%s%s%s]%s%sCongratulations!%s %sYou have %skilled %san objective", 
                                ChatColor.YELLOW, killer.getBukkitPlayer().getName(), ChatColor.RESET, ChatColor.GOLD, ChatColor.BOLD, 
                                ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.RED, ChatColor.DARK_GRAY));
                        //Update scoreboard
                        //this.acScoreboard.setNPCs(this.npcs.size());
                        if(this.npcs.size() > 0){
                            // Add 30 seconds to match
                            this.timeLeft(this.timeLeft + 30);
                            // add time for kill npc to timer task
                            this.timerTask.setCountdown(this.timeLeft);
                            // Announce npc kill
                            this.broadcastMessage(String.format("%sTown Crier: %s is dead! %s 30 seconds added to the match!", 
                                    ChatColor.RED, zombie.getCustomName(), ChatColor.YELLOW));
                            killer.getBukkitPlayer().sendMessage(String.format("%sThere is still %s%s%s targets alive! go for them!", ChatColor.DARK_GRAY, 
                                    ChatColor.AQUA, this.npcs.size(), ChatColor.DARK_GRAY));
                            killer.getBukkitPlayer().sendMessage(String.format("%sUse the %sPlace finder%s[%sCompass%s] to locate the next one!", ChatColor.DARK_GRAY, 
                                    ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.YELLOW, ChatColor.DARK_GRAY));
                            // Gains 3 levels when killing a player
                            killer.getBukkitPlayer().setLevel(killer.getBukkitPlayer().getLevel() + 3);
                        }
                        // Update butter coins
                        this.addButterCoins(killer.getBukkitPlayer(), 1);
                    } else {
                        Bukkit.getLogger().severe(String.format("WRONG STATUS: A target was dead fore some weird reason: %s", event));
                    }
                } else {
                    Bukkit.getLogger().severe(String.format("WRONG STATUS: A target was dead fore some weird reason: %s", event));
                }
            }
            this.verifyGameover();
            break;
        case STARTING_MATCH:
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;
        
        }
    }
    
    public synchronized void playerDeath(PlayerDeathEvent event, ACPlayer death){
        event.setDeathMessage(null);
        switch(this.status){
        case READY_TO_START:
            break;
        case RUNNING:
            death.getBukkitPlayer().setLevel(1);
            if(CharacterEnum.ASSASSIN.equals(death.getCharacter())){
                death.setLives(death.getLives() - 1);
                // Gains 2 levels when killing a player
                death.getBukkitPlayer().setLevel(death.getBukkitPlayer().getLevel() + 2);
                this.verifyGameover();
            }
            break;
        case STARTING_MATCH:
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;
        
        }
    }
    
    public void playerRespawn(final PlayerRespawnEvent event, final ACPlayer player){
        
        if(this.spectators.containsKey(player.getBukkitPlayer().getName())){
            event.setRespawnLocation(this.world.getNavyRandomSpawn());
            return;
        }
        switch(this.status){
        case READY_TO_START:
            event.setRespawnLocation(this.world.getNavyRoomLocation());
            break;
        case RUNNING:
            CharacterEnum character = player.getCharacter();
            switch (character) {
            case ASSASSIN:
                event.setRespawnLocation(this.world.getNavyRandomSpawn());
                player.setCooling(false);
                // Start invisibility.
                ACCharacter.assassin(player);
                player.getBukkitPlayer().setItemInHand(null);
                player.getBukkitPlayer().getInventory().setArmorContents(null);
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1)); 
                player.getBukkitPlayer().sendMessage(String.format("%sYou are invisible for the next %s10%s seconds.", 
                        ChatColor.DARK_GRAY, ChatColor.AQUA, ChatColor.DARK_GRAY));
                broadcastMessageToNavys(String.format("%sWatch out: %sAssassin %sis invisible for the next %s10%s seconds!", 
                        ChatColor.GOLD, ChatColor.RED, ChatColor.DARK_GRAY, ChatColor.AQUA, ChatColor.DARK_GRAY));
                //put the armor when invisibility has gone
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.getBukkitPlayer().getInventory().setArmorContents(ACInventory.getAssassinArmor());
                        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
                        player.getBukkitPlayer().getInventory().addItem(ACInventory.getInvisibleEmerald());
                        player.getBukkitPlayer().getInventory().addItem(ACInventory.getSmokeBomb());
                        player.getBukkitPlayer().getInventory().addItem(ACInventory.getPlaceFinder());
                        player.getBukkitPlayer().getInventory().addItem(ACInventory.getShopIcon());
                        player.getBukkitPlayer().getInventory().setArmorContents(ACInventory.getAssassinArmor());
                    }
                }, 10 * 20);
                break;
            case BODYGUARD:
                event.setRespawnLocation(this.world.getKillBoxLocation());
                this.sendToJailNavy(player);
                ACCharacter.bodyguard(player);
                break;
            case MUSKETEER:
                event.setRespawnLocation(this.world.getKillBoxLocation());
                this.sendToJailNavy(player);
                ACCharacter.musketeer(player);
                break;
            case SWORDSMAN:
                event.setRespawnLocation(this.world.getKillBoxLocation());
                this.sendToJailNavy(player);
                ACCharacter.swordsman(player);
                break;
            default:
                break;
            }
            break;
        case STARTING_MATCH:
            event.setRespawnLocation(plugin.getGame().getLobbySpawnLocation());
            break;
        case STOPPING:
        case STOPPED:
            event.setRespawnLocation(plugin.getGame().getLobbySpawnLocation());
            break;
        default:
            event.setRespawnLocation(plugin.getGame().getLobbySpawnLocation());
            break;
        }
    }
    

    public boolean isUnderThisPotion(ACPlayer acPlayer, PotionEffectType potionType){
        for(PotionEffect potion : acPlayer.getBukkitPlayer().getActivePotionEffects()){
            if(potion.getType().equals(potionType)){
                return true;
            }
        }
        return false;
    }
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        
        switch(this.status){
        case RUNNING:
            if(event.getEntity() instanceof Player){
                Player victim = (Player)event.getEntity();
                ACPlayer acVictim = this.players.get(victim.getName());
                //victim is the assassin
                if(CharacterEnum.ASSASSIN.equals(acVictim.getCharacter())){
                    if(!isUnderThisPotion(acVictim, PotionEffectType.SPEED)){
                        acVictim.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 2));
                        acVictim.getBukkitPlayer().sendMessage(String.format("%sYou have been %shit, %syou can run %s%sfaster!", 
                                ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.BOLD));
                        this.broadcastMessageToNavys(String.format("%sAssassin %sescaped of the trouble by running %s%sfaster!", 
                                ChatColor.RED, ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.BOLD));
                    }
                    return;
                } else {
                    //victim is a navy
                    if(event.getDamager() != null){
                        if(event.getDamager() instanceof Player){
                            Player bukkitDamager = (Player)event.getDamager();
                            ACPlayer damager = this.players.get(bukkitDamager.getName());
                            if(damager != null && CharacterEnum.ASSASSIN.equals(damager.getCharacter())){
                                if ((acVictim.getBukkitPlayer().getHealth() - event.getDamage()) <= 0) {
                                    // victim was killed
                                    this.sendToJailNavy(acVictim);
                                    event.setCancelled(true);
                                }
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            break;
        case READY_TO_START:
        case STARTING_MATCH:
        case STOPPING:
        case STOPPED:
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
    
    public void entityDamagedByDamagerPlayer(EntityDamageByEntityEvent event, ACPlayer damager){
        //spectators cant damage any entity
        if(this.spectators.containsKey(damager.getBukkitPlayer().getName())){
            event.setCancelled(true);
            return;
        }
        
        switch(this.status){
        case RUNNING:
            if(event.getEntity() instanceof Zombie){
                if(event.getDamager() != null){
                    if(event.getDamager() instanceof Player){
                        Player bukkitDamager = (Player)event.getDamager();
                        ACPlayer acPlayer = this.players.get(bukkitDamager.getName());
                        if(acPlayer != null && !CharacterEnum.ASSASSIN.equals(acPlayer.getCharacter())){
                            event.setCancelled(true);
                        }
                    }
                }
            }
            break;
        case READY_TO_START:
        case STARTING_MATCH:
        case STOPPING:
        case STOPPED:
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
    
    public void entityDamagedByOtherEntity(ACPlayer victim, EntityDamageByEntityEvent event) {
        switch (this.status) {
        case STARTING_MATCH:
        case READY_TO_START:
        case STOPPING:
        case STOPPED:
            event.setCancelled(true);
        case RUNNING:
            if(event.getDamager() instanceof Arrow){
                
                Arrow arrow = (Arrow)event.getDamager();

                String ownerTeam = (arrow.hasMetadata("ownerteam")) ? arrow.getMetadata("ownerteam").get(0).asString() : "";
                
                //arrow was shooted by enemy
                if((victim.getCharacter() != null && CharacterEnum.valueOf(ownerTeam) != null) && 
                        !CharacterEnum.isSameTeam(victim.getCharacter(), CharacterEnum.valueOf(ownerTeam))){
                    if ((victim.getCharacter() != null) && CharacterEnum.navyValues().contains(victim.getCharacter())) {
                        if ((victim.getBukkitPlayer().getHealth() - event.getDamage()) <= 0) {
                            // victim was killed
                            this.sendToJailNavy(victim);
                            //arrow was shooted by a assassin to a navy, it kill the navy, cancel the event.
                            event.setCancelled(true);
                            return;
                        }
                    }
                    //arrow was shooted for a assassin to a navy, but it does not kill, dont cancel event
                    //arrow was shooted for a navy to a assassin
                    event.setCancelled(false);
                    return;
                }
                //arrow was shooted for a same team player
                event.setCancelled(true);
                return;
            }
        }
        event.setCancelled(false);
        return;
    }
    
    private void sendToJailNavy(final ACPlayer prisoner){
        prisoner.getBukkitPlayer().setHealth(2);
        prisoner.getBukkitPlayer().teleport(this.world.getKillBoxLocation());
        prisoner.setInJail(true);
        this.broadcastMessage(String.format("%s%s%s %s%swas sent to prison, will be free in %s30%s seconds!", 
                ChatColor.RED, ChatColor.BOLD, prisoner.getBukkitPlayer().getName(),
                ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.YELLOW, 
                ChatColor.DARK_GRAY));
        // navy will be free in 30 seconds
        final BukkitTask task = new BukkitRunnable(){
            int countDown = 30;
            @Override
            public void run() {
                if(countDown <= 0){
                    ACMatch.this.releasePrisioner(prisoner);
                    
                }
                
                if((countDown % 5) == 0) {
                    prisoner.getBukkitPlayer().sendMessage(String.format("%sYou are %sprisoner %snow, you will be free in %s%s %sseconds", 
                            ChatColor.DARK_GRAY, ChatColor.RED, ChatColor.DARK_GRAY, ChatColor.YELLOW, countDown, ChatColor.DARK_GRAY));
                }
                countDown--;
            }
        }.runTaskTimer(this.plugin, 1L, 20L);
        //only way to cancel this task.
        new BukkitRunnable(){
            public void run(){
                task.cancel();
            }
        }.runTaskLater(this.plugin, 620);
    }
    
    public void playerShootBow(EntityShootBowEvent event, ACPlayer shootingPlayer){
        //arrow was shooted by spectator - it must be cancelled
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(player != null && this.spectators.containsKey(player.getName())){
                event.setCancelled(true);
            }
        }
        //arrow was shooted by player in match
        Entity arrow = event.getProjectile();
        if(shootingPlayer.getCharacter() != null){
            MetadataValue ownerValue = new FixedMetadataValue(plugin, shootingPlayer.getCharacter());
            arrow.setMetadata("ownerteam", ownerValue);
        }
    }
    
    
    private List<ACPlayer> getNavysFree(){
        List<ACPlayer> navys = new ArrayList<>();
        for(ACPlayer player : this.players.values()){
            if(!CharacterEnum.ASSASSIN.equals(player.getCharacter()) && !player.isInJail()){
                navys.add(player);
            }
        }
        
        return navys;
    }
    
    private List<ACPlayer> getNavyPrisoners(){
        List<ACPlayer> prisoners = new ArrayList<>();
        for(ACPlayer player : this.players.values()){
            if(!CharacterEnum.ASSASSIN.equals(player.getCharacter()) && player.isInJail()){
                prisoners.add(player);
            }
        }
        
        return prisoners;
    }
    private void releasePrisioner(ACPlayer prisoner){
        synchronized (this.players) {
            prisoner.getBukkitPlayer().teleport(this.world.getNavyRandomSpawn());
            prisoner.setInJail(false);
        }
    }
    
    private void setPlayerRulesToSpectator(ACPlayer player){
        player.getBukkitPlayer().setAllowFlight(false);
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        this.showPlayer(player.getBukkitPlayer());
    }
    
    private void showPlayer(Player bukkitPlayer) {
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().showPlayer(bukkitPlayer);
        }

        for (ACPlayer player : this.spectators.values()) {
            player.getBukkitPlayer().showPlayer(bukkitPlayer);
        }
    }
    
    /**
     * On the player moves
     * @param player
     * @param event
     * @author kvnamo
     */
    public void playerMove(final PlayerMoveEvent event, ACPlayer player) {
        switch (this.status) {
        case READY_TO_START:
            if(!CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                Location location = player.getBukkitPlayer().getLocation().getBlock().getLocation();
                if(this.world.getBodyguardLocation().equals(location) && (player.getCharacter() == null || !CharacterEnum.BODYGUARD.equals(player.getCharacter()))){
                    player.setCharacter(CharacterEnum.BODYGUARD);
                    player.getBukkitPlayer().sendMessage(
                            String.format("%sYou have Selected the %sNavy%s skill: %s%s", ChatColor.DARK_GRAY, ChatColor.BLUE, 
                                    ChatColor.DARK_GRAY, ChatColor.RED, CharacterEnum.BODYGUARD.getName()));
                    player.getBukkitPlayer().sendMessage(
                            String.format("%s%s%s: Strong attack, Slow moving, Absortion effect", ChatColor.GREEN, CharacterEnum.BODYGUARD.getName(),
                                    ChatColor.DARK_GRAY));
                }
                if(this.world.getMusketeerLocation().equals(location) && (player.getCharacter() == null || !CharacterEnum.MUSKETEER.equals(player.getCharacter()))){
                    player.setCharacter(CharacterEnum.MUSKETEER);
                    player.getBukkitPlayer().sendMessage(
                            String.format("%sYou have Selected the %sNavy%s skill: %s%s", ChatColor.DARK_GRAY, ChatColor.BLUE, 
                                    ChatColor.DARK_GRAY, ChatColor.RED, CharacterEnum.MUSKETEER.getName()));
                    player.getBukkitPlayer().sendMessage(
                            String.format("%s%s%s: Fast moving, knockback attack, Bow attack", ChatColor.GREEN, CharacterEnum.MUSKETEER.getName(),
                                    ChatColor.DARK_GRAY));
                }
                if(this.world.getSwordsmanLocation().equals(location) && (player.getCharacter() == null || !CharacterEnum.SWORDSMAN.equals(player.getCharacter()))){
                    player.setCharacter(CharacterEnum.SWORDSMAN);
                    player.getBukkitPlayer().sendMessage(
                            String.format("%sYou have Selected the %sNavy%s skill: %s%s", ChatColor.DARK_GRAY, ChatColor.BLUE, 
                                    ChatColor.DARK_GRAY, ChatColor.RED, CharacterEnum.SWORDSMAN.getName()));
                    player.getBukkitPlayer().sendMessage(
                            String.format("%s%s%s: Sword attack, Knockback attack, Absortion effect", ChatColor.GREEN, CharacterEnum.SWORDSMAN.getName(),
                                    ChatColor.DARK_GRAY));
                }
            }
            break;
        case RUNNING:
            break;
        case STARTING_MATCH:
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;
        }
    }
    
 public void rightClick(PlayerInteractEvent event) {
        
        Player bukkitPlayer = event.getPlayer();
        //dont do anything if player is a spectator, spectators only can use the compass
        if(bukkitPlayer != null && this.spectators.containsKey(bukkitPlayer.getName())){
            if (bukkitPlayer.getItemInHand() != null) {
                if (ACInventory.getLeaveCompass().getType().equals(bukkitPlayer.getItemInHand().getType()))
                    Bukkit.getLogger().severe(String.format("Disconect match linea 1231"));
                    EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
            }
            event.setCancelled(true);
            return;
        }
        
        switch (this.status) {
        case READY_TO_START:
            break;
        case RUNNING:
            if (bukkitPlayer.getItemInHand() != null) {
                //use Material.COMPASS because compass has 2 funcionalities in this plugin: InventoryEnum.LEAVE_COMPASS and InventoryEnum.PLACE_FINDER
                if (Material.COMPASS.equals(bukkitPlayer.getItemInHand().getType())){
                    
                }
            }
            break;
        case STARTING_MATCH:
            if (bukkitPlayer.getItemInHand() != null) {
                if (Material.COMPASS.equals(bukkitPlayer.getItemInHand().getType())){
                    Bukkit.getLogger().severe(String.format("Disconect match linea 1252"));
                    EngineUtils.disconnect(bukkitPlayer, LOBBY, null);
                }
            }
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;
        }
    }
 
 /**
  * When player chats
  * 
  * @param player
  * @author jdgil
  */
 public void chatMessage(AsyncPlayerChatEvent event) {
     final ACPlayer player = this.players.get(event.getPlayer().getName());

     // Spectators are not allowed to send messages.
     if (player == null) {
         event.getPlayer().sendMessage(String.format("%sOnly living players can send messages.", ChatColor.GRAY));
         event.setCancelled(true);
         return;
     }

     // Last message.
     if (StringUtils.isNotBlank(player.getLastMessage()) && player.getLastMessage().equals(event.getMessage().toLowerCase())) {
         event.getPlayer().sendMessage(String.format("%sPlease don't send the same message multiple times!", ChatColor.GRAY));
         event.setCancelled(true);
     }

     event.getRecipients().clear();
     for(ACPlayer playerToChat : this.players.values()){
         event.getRecipients().add(playerToChat.getBukkitPlayer());
     }
     for(ACPlayer spectatorToChat : this.spectators.values()){
         event.getRecipients().add(spectatorToChat.getBukkitPlayer());
     }
     player.setLastMessage(event.getMessage().toLowerCase());
     PlayerTagEnum playerTag = PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount());
     event.setFormat(playerTag.getPrefix() + ChatColor.WHITE + "%s" + ChatColor.GRAY + ": %s");
 }
    
    private void teleportToLobby(Player player, String message){
        final Player bukkitPlayer = player;
        final String msg = message;
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().severe(String.format("Disconect Match linea 1306"));
                EngineUtils.disconnect(bukkitPlayer, LOBBY, msg);
            }
        });
    }
    
    private Zombie findTargetsInMatch(ACPlayer player){

        if(player == null  || player.getCharacter() == null || !CharacterEnum.ASSASSIN.equals(player.getCharacter()))
            return null;

        Collection<Zombie> targets = new ArrayList<>();
        for(Zombie target : this.npcs){
            if(!target.isDead() && target.isValid()){
                targets.add(target);
            }
        }
        if(targets.size() > 0){
            Zombie nearestTarget = this.findNearestTarget(player, targets);
            if(nearestTarget != null) {
                player.getBukkitPlayer().setCompassTarget(nearestTarget.getLocation().clone());
            }
            return nearestTarget;
        }
        return null;
    }
    
    private Zombie findNearestTarget(ACPlayer assassin, Collection<Zombie> targets){
        double lastDistance =  Double.MAX_VALUE;
        Zombie nearestTarget = null;
        for(Zombie target : targets){
            if(target.getWorld().getName().equalsIgnoreCase(assassin.getBukkitPlayer().getWorld().getName())){
                if(lastDistance > target.getLocation().clone().distanceSquared(assassin.getBukkitPlayer().getLocation().clone())){
                    lastDistance = target.getLocation().clone().distanceSquared(assassin.getBukkitPlayer().getLocation().clone());
                    nearestTarget = target;
                }
            } else {
                Bukkit.getLogger().severe(String.format("Match: %s, Player: %s, Opponent: %s, PlayerWorld: %s, OpponentWorld: %s", 
                        this.getMatchName(), assassin.getBukkitPlayer().getName(), target.getCustomName(), 
                        assassin.getBukkitPlayer().getWorld().getName(), target.getWorld().getName()));
            }
        }
        return nearestTarget;
    }
    
    public void hidePlayer(Player bukkitPlayer) {
        for (ACPlayer player : this.players.values()) {
            player.getBukkitPlayer().hidePlayer(bukkitPlayer);
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
     * Broadcast message only to spectators
     * 
     * @param message
     * @author jdgil
     */
    private void broadcastMessageToSpectators(String message) {
        for (ACPlayer player : this.spectators.values()) {
                player.getBukkitPlayer().sendMessage(message);
        }
    }
    
    private void broadcastMessageToNavys(String message) {
        for (ACPlayer player : this.players.values()) {
            if(!CharacterEnum.ASSASSIN.equals(player.getCharacter())){
                player.getBukkitPlayer().sendMessage(message);
            }
        }
    }
    
    private void updatePlayer(final ACPlayer player){
        
        // Save stats in database
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            
            @Override
            public void run() {
                ACMatch.this.plugin.getPersistence().updatePlayer(player.getPlayerModel());
            }
        });
    }
    
    /**
     * Add butter coins in sky central db
     * @param playerName
     * @param butterCoins
     */
    private void addButterCoins(final Player bukkitPlayer, final int butterCoins){
        
        // Update Butter Coins in central DB
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            
            @Override
            public void run() {
                int vipButterCoins = ACMatch.this.plugin.getPersistence().isPlayerStaffOrVIP(bukkitPlayer) ?
                    SettingsManager.getInstance().getInt(SettingsEnum.VIP_BUTTERCOIN_MULTIPLIER) * butterCoins :
                    SettingsManager.getInstance().getInt(SettingsEnum.BUTTERCOIN_MULTIPLIER) * butterCoins ;
                
                ACMatch.this.plugin.getPersistence().addButterCoins(bukkitPlayer.getName(), vipButterCoins);
                bukkitPlayer.sendMessage(String.format("%s[ButterCoins] %sYou have earned %s ButterCoins!", 
                        ChatColor.GOLD, ChatColor.YELLOW, vipButterCoins));
            }
        });
        
    }
    
    public void timeLeft(int timeLeft) {

        this.timeLeft = timeLeft;
        this.acScoreboard.setTimeLeft(timeLeft);

        if (timeLeft > 5)
            return;

        synchronized (this.players) {
            for (ACPlayer player : this.players.values()) {
                // Play sounds
                if (timeLeft == 0)
                    player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.LEVEL_UP, 3, -3);
                else if (timeLeft < 6)
                    player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 3, -3);
            }
        }
    }
    
    public void updateScoreBoard(){
        switch(this.status){
        case READY_TO_START:
        case RUNNING:
            this.acScoreboard.setNavy(this.getNavysFree().size());
            this.acScoreboard.setNPCs(this.getNPCValid());
            this.acScoreboard.setPrisioners(this.getNavyPrisoners().size());
            this.acScoreboard.setAssassinLives(this.getAssassinLives());
            break;
        case STARTING_MATCH:
            break;
        case STOPPING:
        case STOPPED:
            break;
        default:
            break;
        }
    }
    
    public void updateLobbyPortal(){
        switch(this.status){
        case RUNNING:
            this.plugin.getPortalManager().enablePortal(this.matchName);
            break;
        case STARTING_MATCH:
        case READY_TO_START:
        case STOPPING:
        case STOPPED:
            this.plugin.getPortalManager().disablePortal(this.matchName);
            break;
        default:
            break;
        }
    }
    
    private int getNPCValid(){
        List<Zombie> targets = new ArrayList<>();
        for(Zombie zombie : this.npcs){
            if(zombie.isValid()){
                targets.add(zombie);
            }
        }
        return targets.size();
    }
    
    /**
     * @return the matchName
     */
    public String getMatchName() {
        return matchName;
    }

    /**
     * @param matchName the matchName to set
     */
    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    /**
     * @return the acScoreboard
     */
    public ACScoreboard getAcScoreboard() {
        return acScoreboard;
    }

    /**
     * @param acScoreboard the acScoreboard to set
     */
    public void setAcScoreboard(ACScoreboard acScoreboard) {
        this.acScoreboard = acScoreboard;
    }

    /**
     * @return the spectators
     */
    public Map<String, ACPlayer> getSpectators() {
        return spectators;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(MatchStatusEnum status) {
        this.status = status;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Map<String, ACPlayer> getPlayers(){
        return this.players;
    }    

    public MatchStatusEnum getStatus(){
        return this.status;
    }

    public MinecadeWorld getMinecadeWorld(){
        return this.world;
    }
    
    public void setMinecadeWorld(ACBaseWorld world){
        this.world = world;
    }

    /**
     * @return the world
     */
    public ACBaseWorld getWorld() {
        return world;
    }
}
