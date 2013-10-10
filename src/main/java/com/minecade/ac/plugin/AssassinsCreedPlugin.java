package com.minecade.ac.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.minecade.ac.data.ACPersistence;
import com.minecade.ac.engine.ACGame;
import com.minecade.ac.listener.ACListener;
import com.minecade.ac.world.ACLobby;
import com.minecade.ac.world.ACWorld;
import com.minecade.ac.world.ACWorld1;
import com.minecade.ac.world.ACWorld2;
import com.minecade.ac.world.ACWorld3;
import com.minecade.ac.world.ACWorld4;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.command.CommandFactory;

public class AssassinsCreedPlugin extends MinecadePlugin {
   
    private static final String ASSASSINSCREED_COMMANDS_PACKAGE = "com.minecade.ac.command";

    private ACPersistence persistence;  
    
    /**
     * Get persistence
     * @return ACPersistence
     * @author kvnamo
     */
    public ACPersistence getPersistence() {
        return this.persistence;
    }

    private ACGame game;

    /**
     * Get game
     * @return
     * @author Kvnamo
     */
    public ACGame getGame() {
        return game;
    }

    /**
     * Set game
     * @param match
     * @author Kvnamo
     */
    public void setGame(ACGame game) {
        this.game = game;
    }
    
    private List<ACWorld> acWorlds = new ArrayList<ACWorld>();
    
    /**
     * Returns a world.
     * @param index
     * @return ac world
     * @author kvnamo
     */
    public ACWorld getACWorld(int index) {
        return acWorlds.get(index);
    }
    
    /**
     * Gets the random announcement.
     * @return the random announcement
     * @author kvnamo
     */
    public String getRandomAnnouncement() {
        final List<String> announcements = getConfig().getStringList("server.announcements");
        return ChatColor.translateAlternateColorCodes('&', announcements.get(getRandom().nextInt(announcements.size())));
    }
    
    /**
     * (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     * @author kvnamo
     */
    @Override
    public void onEnable(){
        super.onEnable();
        super.getServer().getLogger().info("onEnable has been invoked!");
        
        // Save config.yml default values and completes the new values from the jar file
        super.saveDefaultConfig();
        super.getConfig().options().copyDefaults(true);
        
        // Register listeners
        super.getServer().getPluginManager().registerEvents(new ACListener(this), this);     
        
        // Initialize game.
        this.game = new ACGame(this);
        
        // Register commands
        CommandFactory.registerCommands(this, ASSASSINSCREED_COMMANDS_PACKAGE);
        
        // Initialize persistence
        this.persistence = new ACPersistence(this);
        this.persistence.createOrUpdateServer();
        
        // Initiliaze worlds
        new ACLobby(this);
        this.acWorlds.add(new ACWorld1(this));
        this.acWorlds.add(new ACWorld2(this));
        this.acWorlds.add(new ACWorld3(this));
        this.acWorlds.add(new ACWorld4(this));
        
        // Initialize Matches.
        this.game.initMatches();

        // Register Bungeecord
        super.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Send an announcement every 5 minutes
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                final String announcement = AssassinsCreedPlugin.this.getRandomAnnouncement();
                for (final Player online : AssassinsCreedPlugin.this.getServer().getOnlinePlayers()) {
                    online.sendMessage(announcement);
                }
            }
        }, 6000L, 6000L);

        // Update player count every 10 seconds if it has changed
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                AssassinsCreedPlugin.this.persistence.updateServerPlayers();
            }
        }, 200L, 200L);
    }
    
   /**
    * (non-Javadoc)
    * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
    * @author kvnamo
    */
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
        // This will unregister all events from the specified plugin-
        HandlerList.unregisterAll(this);
    }

    /**
     * Force match start
     * @author kvnamo
     */
    @Override
    public String forceStart() {
        return null;//this.game.forceStartMatch();
    }
}
