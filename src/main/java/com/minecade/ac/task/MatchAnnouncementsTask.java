package com.minecade.ac.task;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACMatch;
import com.minecade.ac.plugin.AssassinsCreedPlugin;

public class MatchAnnouncementsTask extends BukkitRunnable{
    
    private AssassinsCreedPlugin plugin;
    
    private ACMatch match;
    
    private List<String> announcements;
    
    public Random random = new Random();
    
    /**
     * MatchAnnouncementsTask constructor
     * @param match
     * @author Kvnamo
     */
    public MatchAnnouncementsTask(AssassinsCreedPlugin plugin, ACMatch match){
        this.match = match;
        this.announcements = this.plugin.getConfig().getStringList("match.announcements");
    }
    
    /**
     * Bukkit runnable task
     */
    @Override
    public void run() {
        this.match.broadcastMessage(ChatColor.translateAlternateColorCodes(
            '&', this.announcements.get(random.nextInt(this.announcements.size()))));
    }
}
