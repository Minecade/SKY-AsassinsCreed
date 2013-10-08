package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACMatch;

public class MatchTimerTask extends BukkitRunnable{

    private ACMatch match;
    private int countdown;
    
    /**
     * Timer task constructor
     * @param match
     * @param countdown
     * @author kvnamo
     */
    public MatchTimerTask(ACMatch match, int countdown){
        this.match = match;
        this.countdown = countdown;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * @author kvnamo
     */
    @Override
    public void run() {
        this.countdown--;
        
        this.match.timeLeft(this.countdown);
        
        if(this.countdown <= 0){
            this.match.start();
            super.cancel();
        }
    }
}
