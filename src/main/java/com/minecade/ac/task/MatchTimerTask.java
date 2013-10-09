package com.minecade.ac.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACMatch;
import com.minecade.ac.enums.MatchStatusEnum;

public class MatchTimerTask extends BukkitRunnable{

    private ACMatch match;
    
    private Player assassin;
    
    private int countdown;
    
    /**
     * Timer task constructor
     * @param match
     * @param countdown
     * @author kvnamo
     */
    public MatchTimerTask(ACMatch match, Player assassin, int countdown){
        this.match = match;
        this.assassin = assassin;
        this.countdown = countdown;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * @author kvnamo
     */
    @Override
    public void run() {
        
        // Gains 1 level every 10 seconds
        if(this.countdown % 10 == 0){
            assassin.setExp(assassin.getExp() + 1);
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
