package com.minecade.ac.world;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.MapLocation;
import com.minecade.engine.MinecadePlugin;


public class ACWorld extends ACBaseWorld {

    /**
     * ACWorld constructor
     * 
     * @param plugin
     * @author Kvnamo
     */
    public ACWorld(MinecadePlugin plugin) {

        super(plugin);

        // Set assassin spawn locations.
        addAssassinSpawnLocation(new MapLocation(-170, 171, 28));

        // Set navy spawn locations.
        addNavySpawnLocation(new MapLocation(-190, 116, -44));

        addNavyRoomSpawnLocation(new MapLocation(-161, 96, -49));
        addBodyGuardSpawnLocation(new MapLocation(-149, 96, -47));
        addMusketeerSpawnLocation(new MapLocation(-149, 96, -50));
        addSwordsmanSpawnLocation(new MapLocation(-149, 96, -53));

        // Set top shop location
        addTopShopLocation(new MapLocation(-169, 114, 34));
        addLowerShopLocation(new MapLocation(-167, 107, 18));

        // Set killBox spawn locations.
        addKillBoxLocation(new MapLocation(-195, 96, -49));

        // Set NPC spawn locations.
        addNpcLocation(NPCEnum.GREEN, new MapLocation(-142.31, 123, -83.7));
        addNpcLocation(NPCEnum.GRAY, new MapLocation(-154.3, 117, -48.7 ));
        addNpcLocation(NPCEnum.RED, new MapLocation(-182.3, 117, -52.6));
        addNpcLocation(NPCEnum.YELLOW, new MapLocation(-208.6, 124, -68.7));
        addNpcLocation(NPCEnum.WHITE, new MapLocation(-216.5, 116, -37.3));
    }

}
