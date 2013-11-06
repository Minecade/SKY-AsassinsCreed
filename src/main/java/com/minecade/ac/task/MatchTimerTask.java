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

    private ACMatch match;
    
    /**
     * Set match
     * @param plugin
     * @author Kvnamo
     */
    public void setMatch(ACMatch match){
        this.match = match;
    }
    
    private Player assassin;
    
    /**
     * Set player
     * @param assassin
     * @author Kvnamo
     */
    public void setPlayer(Player assassin){
        this.assassin = assassin;
    }
    
    private int countdown;
    
    /**
     * Set countdown
     * @param countdown
     * @author Kvnamo
     */
    public void setCountdown(int countdown){
        this.countdown = countdown;
    }
    
    private Random random;

    private List<String> announcements;
    
    /**
     * Timer task constructor
     * @param match
     * @param countdown
     * @author kvnamo
     */
    public MatchTimerTask(AssassinsCreedPlugin plugin, Random random){
        this.random = random;
        this.announcements = plugin.getConfig().getStringList("match.announcements");
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
            
            super.cancel();
            
            if(MatchStatusEnum.RUNNING.equals(this.match.getStatus())){
                this.match.finish();
            }
            else this.match.start();
        }
    }
}
