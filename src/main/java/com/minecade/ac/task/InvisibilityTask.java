package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACPlayer;
import com.minecade.ac.enums.CharacterEnum;

public class InvisibilityTask extends BukkitRunnable{

    final private ACPlayer player;
    private int invisibilityTime;
    private int coolingTime;
    
    /**
     * Invisivility task constructor
     * @param assasin
     * @author kvnamo
     */
    public InvisibilityTask(final ACPlayer player, int invisibilityTime){
        this.player = player;
        this.coolingTime = 0;
        this.invisibilityTime = invisibilityTime;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * This task must me executed every 10 seconds
     * @author kvnamo
     */
    @Override
    public void run() {
        
        // Validate assassin player character
        if(!CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            super.cancel();
        }
        
        // Validate assassin is not cooling down
        if(!this.player.isCooling()){
            this.player.setCooling(true);
            this.player.getCurrentMatch().hidePlayer(this.player.getBukkitPlayer());
        }
        // 1 is equal to 10 seconds
        else if(this.coolingTime == invisibilityTime){
            this.player.getCurrentMatch().showPlayer(this.player.getBukkitPlayer());
        }
        // 3 is equal to 30 seconds
        else if(this.coolingTime == 3){
            this.player.setCooling(false);
            super.cancel();
        }
        
        this.coolingTime++;
    }   
}
