package com.minecade.ac.task;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACCharacter;
import com.minecade.ac.engine.ACMatch;
import com.minecade.ac.enums.NPCEnum;

public class SpawnZombieTask extends BukkitRunnable{

    final private ACMatch match;
    
    /**
     * Spawn NPC task constructor
     * @param assasin
     * @author kvnamo
     */
    public SpawnZombieTask(ACMatch match){
        this.match = match;
    }
    
    /**
     * Sync task runned by bukkit scheduler
     * This task must me executed every 10 seconds
     * @author kvnamo
     */
    @Override
    public void run() {
        
        // Get Spawn location
        NPCEnum npc = NPCEnum.values()[new Random().nextInt(NPCEnum.values().length)];
        Location location = match.getACWorld().getNPCLocation(npc);
        
        // Spawn zombie
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        ACCharacter.setupZombie(zombie, npc);
    }        
}
