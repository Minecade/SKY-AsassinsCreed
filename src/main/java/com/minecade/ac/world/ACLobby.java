package com.minecade.ac.world;

import org.bukkit.Material;

import com.minecade.engine.MapLocation;
import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;
import com.minecade.engine.utils.MinecadePortal;

public class ACLobby extends MinecadeWorld {
    

    public ACLobby(MinecadePlugin plugin) {
        super(plugin);
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld", new MapLocation(-1111, 13, 2453), 
//                new MapLocation(-1111, 16, 2455), Material.PORTAL, this.world));
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld", new MapLocation(-1111, 13, 2453), 
//                new MapLocation(-1111, 16, 2455), Material.PORTAL, this.world));
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld1", new MapLocation(-1111, 13, 2450), 
//                new MapLocation(-1111, 16, 2452), Material.PORTAL, this.world));
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld2", new MapLocation(-1131, 13, 2453), 
//                new MapLocation(-1131, 16, 2455), Material.PORTAL, this.world));
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld3", new MapLocation(-1131, 13, 2450), 
//                new MapLocation(-1131, 16, 2452), Material.PORTAL, this.world));
//        plugin.getPortalManager().addPortalToMatch(new MinecadePortal("ACWorld4", new MapLocation(-1123, 13, 2462), 
//                new MapLocation(-1121, 16, 2462), Material.PORTAL, this.world));
        plugin.getPortalManager().addPortalToMatch( new MinecadePortal("ACWorld5", new MapLocation(-1120, 13, 2462), 
                new MapLocation(-1118, 16, 2462), Material.PORTAL, this.world));
        // player v.s player enable
        super.world.setPVP(false);
    }
}
