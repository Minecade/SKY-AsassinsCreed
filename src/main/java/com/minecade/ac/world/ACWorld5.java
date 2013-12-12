package com.minecade.ac.world;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.MapLocation;
import com.minecade.engine.MinecadePlugin;

public class ACWorld5 extends ACBaseWorld{
    public ACWorld5(MinecadePlugin plugin) {

        super(plugin);

        // Set assassin spawn locations.
        addAssassinSpawnLocation(new MapLocation(-169.42, 172.5, 27));
        addAssassinSpawnLocation(new MapLocation(-168.79, 145.5, -47.62));
        addAssassinSpawnLocation(new MapLocation(-206, 141.5, 1.26));
        addAssassinSpawnLocation(new MapLocation(-185.69, 139.5, 11.34));
        addAssassinSpawnLocation(new MapLocation(-202, 145, -58.4));

        // Set navy spawn locations.
          addNavySpawnLocation(new MapLocation(-190, 116, -44));
          addNavySpawnLocation(new MapLocation(-204, 119, -4));
          addNavySpawnLocation(new MapLocation(-189.81, 123, -75.5));
          addNavySpawnLocation(new MapLocation(-138.22, 123, -56.7));
          addNavySpawnLocation(new MapLocation(-145, 116, -32.3));

        addNavyRoomSpawnLocation(new MapLocation(-161, 96, -49));
        addBodyGuardSpawnLocation(new MapLocation(-149, 96, -47));
        addMusketeerSpawnLocation(new MapLocation(-149, 96, -50));
        addSwordsmanSpawnLocation(new MapLocation(-149, 96, -53));

        // Set top shop location
        addTopShopLocation(new MapLocation(-169, 114, 34));
        addLowerShopLocation(new MapLocation(-167, 107, 18));

        // Set killBox spawn locations.
        addKillBoxLocation(new MapLocation(-176, 96, -49));

        // Set NPC spawn locations.
        addNpcLocation(NPCEnum.YELLOW, new MapLocation(-202.46, 123, -63.69));
        addNpcLocation(NPCEnum.GREEN, new MapLocation(-154.57, 123, -65.86));
        addNpcLocation(NPCEnum.GRAY, new MapLocation(-153.48, 116, -35.65));
        addNpcLocation(NPCEnum.RED, new MapLocation(-177.58, 117, -39.19));
        addNpcLocation(NPCEnum.WHITE, new MapLocation(-203.43, 117, -36.07));
    }
}
