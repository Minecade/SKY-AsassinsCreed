package com.minecade.ac.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.MapLocation;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;

public class ACBaseWorld  extends MinecadeWorld {
    
    private List<Location> navySpawnLocation;
    private List<Location> assassinSpawnLocation;
    
    private Location navyRooomLocation;
    private Location bodyguardLocation;
    private Location musketeerLocation;
    private Location swordsmanLocation;
    private Location topShopLocation;
    private Location lowerShopLocation;
    private Location killBoxLocation;
    private Map<NPCEnum, Location> npcLocation;

    public ACBaseWorld(String worldName, String worldLocation, MinecadePlugin plugin) {
        super(worldName, worldLocation, plugin);
        configureACWorld(world);
        navySpawnLocation = new ArrayList<>();
        assassinSpawnLocation = new ArrayList<>();
        npcLocation = new HashMap<NPCEnum, Location>();
    }
    
    public ACBaseWorld(MinecadePlugin plugin) {
        super(plugin);
        configureACWorld(world);
        navySpawnLocation = new ArrayList<>();
        assassinSpawnLocation = new ArrayList<>();
        npcLocation = new HashMap<NPCEnum, Location>();
    }
    
    private static void configureACWorld(World world) {
        // Allows/Disallows player to naturally regenerate health, regardless of food level
        world.setGameRuleValue("naturalRegeneration", "true");
    }
    
    /**
     * Adds a location to possible entity spawn points.
     * @param location 
     */
    public void addAssassinSpawnLocation(MapLocation location) {
        assassinSpawnLocation.add(location.toLocation(world));
    }
    public Location getAssassinSpawn() {
        return assassinSpawnLocation.get(plugin.getRandom().nextInt(assassinSpawnLocation.size()));
    }
    /**
     * Adds a location to possible entity spawn points.
     * @param location 
     */
    public void addNavySpawnLocation(MapLocation location) {
        navySpawnLocation.add(location.toLocation(world));
    }
    /**
     * Returns a spawn point for entities from a list.
     * @return 
     */
    public Location getNavyRandomSpawn() {
        return navySpawnLocation.get(plugin.getRandom().nextInt(navySpawnLocation.size()));
    }
    
    public void addNavyRoomSpawnLocation(MapLocation location) {
        navyRooomLocation = location.toLocation(world);
    }
    
    public void addBodyGuardSpawnLocation(MapLocation location) {
        bodyguardLocation = location.toLocation(world);
    }
    
    public void addMusketeerSpawnLocation(MapLocation location) {
        musketeerLocation = location.toLocation(world);
    }
    
    public void addSwordsmanSpawnLocation(MapLocation location) {
        swordsmanLocation = location.toLocation(world);
    }
    public void addTopShopLocation(MapLocation location) {
        topShopLocation = location.toLocation(world);
    }
    public void addLowerShopLocation(MapLocation location) {
        lowerShopLocation = location.toLocation(world);
    }
    public void addKillBoxLocation(MapLocation location) {
        killBoxLocation = location.toLocation(world);
    }
    public void addNpcLocation(NPCEnum type, MapLocation location) {
        npcLocation.put(type, location.toLocation(world));
    }
    
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
    /**
     * Get musketeer location
     * @author Kvnamo
     */
    public Location getMusketeerLocation(){
        return this.musketeerLocation;
    }
    
    /**
     * Get swordsman location
     * @author Kvnamo
     */
    public Location getSwordsmanLocation(){
        return this.swordsmanLocation;
    }
    
    
    
    /**
     * Get assassin spawn location
     * @author Kvnamo
     */
    public Location getTopShopLocation(){
        return this.topShopLocation;
    }
    
    
    
    /**
     * Get lowe shop location
     * @author Kvnamo
     */
    public Location getLowerShopLocation(){
        return this.lowerShopLocation;
    }
    
    
    
    /**
     * Get kill box spawn location
     * @author Kvnamo
     */
    public Location getKillBoxLocation(){
        return this.killBoxLocation;
    }
    
    
    
    /**
     * Get npc spawn location
     * @author Kvnamo
     */
    public Location getNPCLocation(NPCEnum npc){
        return npcLocation.get(npc);
    }

}
