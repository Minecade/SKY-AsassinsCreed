package com.minecade.ac.world;

import java.util.Map;

import org.bukkit.Location;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;

public class ACWorld1 extends MinecadeWorld{
    
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
    
    private Location topShopLocation;
    
    /**
     * Get top shop location
     * @author Kvnamo
     */
    public Location getTopShopLocation(){
        return this.topShopLocation;
    }
    
    private Location killBox;
    
    /**
     * Get kill box spawn location
     * @author Kvnamo
     */
    public Location getkillBoxLocation(){
        return this.killBox;
    }
    
    private Map<NPCEnum, Location> npcLocation;
    
    /**
     * Get npc spawn location
     * @author Kvnamo
     */
    public Location getNPCLocation(NPCEnum npc){
        return npcLocation.get(npc);
    }

    /**
     * ACWorld1 constructor
     * @param plugin
     * @author Kvnamo
     */
    public ACWorld1(MinecadePlugin plugin) {
        super("Assassins Creed 1", "assassins1", plugin);
        
//        // Set assassin spawn locations.
//        this.shipLocation = new Location(-169, 114, 34);
//        
//        // Set navy spawn locations.
//        this.navyLocation = new Location(-161, 96, -49);
//        
//        // Set top shop location
//        this.topShopLocation = new Location(-167, 115, 33);
//        
//        // Set killBox spawn locations.
//        this.killBox = new Location(-176, 96, -49);
//        
//        // Set NPC spawn locations.
//        this.npcLocation = new ConcurrentHashMap<NPCEnum, Location>();
//        this.npcLocation.set(NPCEnum.GREEN, new Location(-142, 123, -83);
//        this.npcLocation.set(NPCEnum.GREY, new Location(-154, 117, -48);
//        this.npcLocation.set(NPCEnum.RED, new Location(-182, 117, -52);
//        this.npcLocation.set(NPCEnum.YELLOW, new Location(-208, 124, -68);
//        this.npcLocation.set(NPCEnum.WHITE, new Location(-216, 116, -37);
    }
}
