package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACPlayer;

public class ExpTask extends BukkitRunnable{

    final private ACPlayer player;
 
    /**
     * XP task constructor
     * @param assasin
     * @author kvnamo
     */
    public ExpTask(final ACPlayer player){
        this.player = player;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * @author kvnamo
     */
    @Override
    public void run() {
        this.player.getBukkitPlayer().setExp(this.player.getBukkitPlayer().getExp() + 1);
    }
}
