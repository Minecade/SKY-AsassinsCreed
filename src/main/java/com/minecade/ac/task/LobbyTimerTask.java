package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACGame;
import com.minecade.ac.engine.ACMatch;

public class LobbyTimerTask extends BukkitRunnable{

    private ACGame game;
    
    private ACMatch match;
    
    /**
     * Set match
     * @param match
     * @author Kvnamo
     */
    public void setMatch(ACMatch match){
        this.match = match;
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
    
    /**
     * Timer task constructor
     * @param game
     * @param countdown
     * @author kvnamo
     */
    public LobbyTimerTask(ACGame game){
        this.game = game;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * @author kvnamo
     */
    @Override
    public void run() {
        this.countdown--;
        
//        this.game.timeLeft(this.countdown);
//        
//        if(this.countdown <= 0){
//            this.game.initNextMatch(this.match);
//            super.cancel();
//        }
    }
}
