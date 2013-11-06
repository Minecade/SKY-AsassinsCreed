package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACGame;
import com.minecade.ac.engine.ACMatch;

public class LobbyTimerTask extends BukkitRunnable{

    private ACGame game;
    
    private ACMatch match;
    
    private int countdown;
    
    /**
     * Timer task constructor
     * @param game
     * @param countdown
     * @author kvnamo
     */
    public LobbyTimerTask(ACGame game, ACMatch match, int countdown){
        this.game = game;
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
        
        this.game.timeLeft(this.countdown);
        
        if(this.countdown <= 0){
            this.game.initNextMatch(match);
            super.cancel();
        }
    }
}
