package com.minecade.ac.task;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACMatch;
import com.minecade.ac.enums.MatchStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;

public class MatchTimerTask extends BukkitRunnable{

    private AssassinsCreedPlugin plugin;
    
    private ACMatch match;
    
    private Player assassin;
    
    private int countdown;
    
    private List<String> announcements;
    
    public Random random = new Random();
    
    /**
     * Timer task constructor
     * @param match
     * @param countdown
     * @author kvnamo
     */
    public MatchTimerTask(AssassinsCreedPlugin plugin, ACMatch match, Player assassin, int countdown){
        this.plugin = plugin;
        this.match = match;
        this.assassin = assassin;
        this.countdown = countdown;
        this.announcements = this.plugin.getConfig().getStringList("match.announcements");
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * @author kvnamo
     */
    @Override
    public void run() {
        
        // Gains 1 level every 10 seconds
        if(this.countdown % 10 == 0){
            assassin.setLevel(assassin.getLevel() + 1);
        }
        
        // Announcements
        if(this.countdown % 30 == 0){
            this.match.broadcastMessage(ChatColor.translateAlternateColorCodes(
                '&', this.announcements.get(random.nextInt(this.announcements.size()))));
        }
        
        this.countdown--;
        
        // Set time left
        this.match.timeLeft(this.countdown);
        
        // If countdown is cero cancel this
        if(this.countdown <= 0){
            
            if(MatchStatusEnum.RUNNING.equals(this.match.getStatus())){
                this.match.finish();
            }
            else this.match.start();
                
            super.cancel();
        }
    }
}
