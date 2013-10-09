package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACGame;

public class LobbyTimerTask extends BukkitRunnable{

    private ACGame game;
    
    private int countdown;
    
    /**
     * Timer task constructor
     * @param game
     * @param countdown
     * @author kvnamo
     */
    public LobbyTimerTask(ACGame game, int countdown){
        this.game = game;
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
            this.game.initNextMatch();
            super.cancel();
        }
    }
}
