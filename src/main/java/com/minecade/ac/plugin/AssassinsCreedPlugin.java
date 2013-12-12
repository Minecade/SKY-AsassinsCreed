package com.minecade.ac.plugin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.minecade.ac.data.ACPersistence;
import com.minecade.ac.engine.ACGame;
import com.minecade.ac.listener.ACListener;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.command.CommandFactory;
import com.minecade.engine.utils.PassManager;

public class AssassinsCreedPlugin extends MinecadePlugin{
   
    private static final String ASSASSINSCREED_COMMANDS_PACKAGE = "com.minecade.ac.command";

    private ACPersistence persistence;  
    
    private ACGame game;
    
    @Override
    public void onEnable(){
        
        super.setPassManager(new PassManager(this, "Assassin"));
        super.onEnable();
        super.getServer().getLogger().info("onEnable has been invoked!");
        
        // Save config.yml default values and completes the new values from the jar file
        super.saveDefaultConfig();
        super.getConfig().options().copyDefaults(true);
        
        // Register listeners
        super.getServer().getPluginManager().registerEvents(new ACListener(this), this);
        
        // Initialize persistence
        this.persistence = new ACPersistence(this);
        
        // Initialize game.
        this.game = new ACGame(this);
        // Initiliaze worlds and lobby
        this.game.init();
        
        // Register commands
        CommandFactory.registerCommands(this, ASSASSINSCREED_COMMANDS_PACKAGE);

        // Create or update server status in DB.
        this.persistence.createOrUpdateServer(this.game.getCurrentMatch().getMatchName());

        // Register Bungeecord
        super.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Send an announcement every 5 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
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
    
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
        // This will unregister all events from the specified plugin-
        HandlerList.unregisterAll(this);
    }

    @Override
    public String forceStart() {
        return this.game.forceStartMatch();
    }
    

    public ACPersistence getPersistence() {
        return this.persistence;
    }

    public ACGame getGame() {
        return game;
    }

    public void setGame(ACGame game) {
        this.game = game;
    }

    public String getRandomAnnouncement() {
        final List<String> announcements = getConfig().getStringList("server.announcements");
        return ChatColor.translateAlternateColorCodes('&', announcements.get(getRandom().nextInt(announcements.size())));
    }
}
