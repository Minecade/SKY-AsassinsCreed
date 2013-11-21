package com.minecade.ac.world;

import com.minecade.engine.MinecadePlugin;
import com.minecade.engine.MinecadeWorld;

public class ACLobby extends MinecadeWorld {

    /**
     * ACLobby constructor
     * @param plugin
     * @author Kvnamo
     */
    public ACLobby(MinecadePlugin plugin) {
        
        super(plugin);
        
        // player v.s player enable
        super.world.setPVP(false);
    } 
}
