package com.minecade.ac.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;

public abstract class ACWorld extends MinecadeWorld{
    
    private Location shipLocation;
    
    /**
     * Get assassin spawn location
     * @author Kvnamo
     */
    public Location getShipLocation(){
        return this.shipLocation;
    }
    
    private Location navyLocation;
    
    /**
     * Get navy spawn location
     * @author Kvnamo
     */
    public Location getNavyLocation(){
        return this.navyLocation;
    }
    
    private Location navyRooomLocation;
    
    /**
     * Get navy room spawn location
     * @author Kvnamo
     */
    public Location getNavyRoomLocation(){
        return this.navyRooomLocation;
    }
    
    /**
     * Get bodyguard location
     * @author Kvnamo
     */
    public Location getBodyguardLocation(){
        return this.bodyguardLocation;
    }
    
    private Location musketeerLocation;
    
    /**
     * Get musketeer location
     * @author Kvnamo
     */
    public Location getMusketeerLocation(){
        return this.musketeerLocation;
    }
    
    private Location swordsmanLocation;
    
    /**
     * Get swordsman location
     * @author Kvnamo
     */
    public Location getSwordsmanLocation(){
        return this.swordsmanLocation;
    }
    
    private Location lowerShopLocation;
    
    /**
     * Get lowe shop location
     * @author Kvnamo
     */
    public Location getLowerShopLocation(){
        return this.lowerShopLocation;
    }
    
    private Location killBoxLocation;
    
    /**
     * Get kill box spawn location
     * @author Kvnamo
     */
    public Location getKillBoxLocation(){
        return this.killBoxLocation;
    }
    
    private Location bodyguardLocation;
    
 
    
    private Map<NPCEnum, Location> npcLocation;
    
    /**
     * Get npc spawn location
     * @author Kvnamo
     */
    public Location getNPCLocation(NPCEnum npc){
        return npcLocation.get(npc);
    }

    /**
     * ACWorld constructor
     * @param plugin
     * @author Kvnamo
     */
    public ACWorld(MinecadePlugin plugin, int world) {
        super(String.format("Assassin's Creed %s", world),
            String.format("assassins%s", world), plugin);
        
        // Set assassin spawn locations.
        this.shipLocation = new Location(super.world, -169, 114, 34);
        
        // Set navy spawn locations.
        this.navyLocation = new Location(super.world, -170, 113, 18);
        this.navyRooomLocation = new Location(super.world, -161, 96, -49);
        this.bodyguardLocation = new Location(super.world, -149, 96, -47);
        this.musketeerLocation = new Location(super.world, -149, 96, -50);
        this.swordsmanLocation = new Location(super.world, -149, 96, -53);
        
        // Set top shop location
        this.lowerShopLocation = new Location(super.world, -167, 107, 18);
        
        // Set killBox spawn locations.
        this.killBoxLocation = new Location(super.world, -176, 96, -49);
        
        // Set NPC spawn locations.
        this.npcLocation = new ConcurrentHashMap<NPCEnum, Location>();
        this.npcLocation.put(NPCEnum.GREEN, new Location(super.world, -142, 123, -83));
        this.npcLocation.put(NPCEnum.GRAY, new Location(super.world, -154, 117, -48));
        this.npcLocation.put(NPCEnum.RED, new Location(super.world, -182, 117, -52));
        this.npcLocation.put(NPCEnum.YELLOW, new Location(super.world, -208, 124, -68));
        this.npcLocation.put(NPCEnum.WHITE, new Location(super.world, -216, 116, -37));
    }
}
