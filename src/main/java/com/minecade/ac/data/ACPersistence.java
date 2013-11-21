package com.minecade.ac.data;

import java.util.Date;

import com.avaje.ebean.SqlUpdate;
import com.minecade.ac.enums.ServerStatusEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.data.MinecadeAccount;
import com.minecade.engine.data.MinecadePersistence;

public class ACPersistence extends MinecadePersistence { 

    private int lastPlayerCount;
    private final AssassinsCreedPlugin plugin;

    /**
     * ACPersistence constructor
     * @param plugin
     * @author kvnamo
     */
    public ACPersistence(final AssassinsCreedPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    /**
     * Gets the server id
     * @param serverId
     * @return
     * @author kvnamo
     */
    public ServerModel getServerById(long serverId) {
        return super.plugin.getDatabase().find(ServerModel.class).where().eq("serverId", serverId).findUnique();
    }

    /**
     * Creates the or update server in the db.
     * @param world name
     * @author kvnamo
     */
    public void createOrUpdateServer(String worldName) {
        int serverId = plugin.getConfig().getInt("server.id");
        
        ServerModel server = plugin.getDatabase().find(ServerModel.class)
            .where().eq("serverId", serverId).findUnique(); 
        
        // Creates a new bean that is managed by bukkit
        if(server == null) {
            server = plugin.getDatabase().createEntityBean(ServerModel.class);
            server.setServerId(serverId);
        }        

        server.setMaxPlayers(plugin.getConfig().getInt("match.required-players"));
        server.setOnlinePlayers(0);
        server.setStatus(ServerStatusEnum.WAITING_FOR_PLAYERS);
        server.setWorldName(worldName);
        
        // Stores the bean
        plugin.getDatabase().save(server);
    }
    /**
     * Update server status.
     * @param serverStatus 
     * @author kvnamo
     */
    public void updateServerStatus(ServerStatusEnum state, String worldName) {
        StringBuilder query = new StringBuilder("Update servers set state=:state ");
        
        if(ServerStatusEnum.OFFLINE.equals(state)) {
            query.append(", online_players=:online_players ");
        }
        if(ServerStatusEnum.FULL.equals(state)) {
            query.append(", online_players=:online_players ");
        }
        query.append(", world_name=:world_name ");
        
        query.append("where id = :id");
        
        SqlUpdate update = plugin.getDatabase()
            .createSqlUpdate(query.toString())
            .setParameter("state", state.name())
            .setParameter("id", plugin.getConfig().getInt("server.id"));
        
        if(ServerStatusEnum.OFFLINE.equals(state)) {
            update.setParameter("online_players", 0)
            .setParameter("world_name", "empty");
        }else if(ServerStatusEnum.FULL.equals(state)){
            update.setParameter("online_players", plugin.getConfig().getInt("match.required-players"));
        }
        update.setParameter("world_name", worldName);
        
        update.execute();
    }

    /**
     * Update server players.
     * @author kvnamo
     */
    public void updateServerPlayers() {
        final int playerCount = this.plugin.getGame().getPlayers().size();
        
        if(ServerStatusEnum.FULL.equals(this.plugin.getGame().getServerStatus()))
            return;
        
        if (playerCount == this.lastPlayerCount) {
            return;
        }
        
        this.lastPlayerCount = playerCount;
        
        String query = "update servers set online_players=:online_players where id = :id";
        SqlUpdate update = plugin.getDatabase().createSqlUpdate(query)
                .setParameter("online_players", playerCount)
                .setParameter("id", plugin.getConfig().getInt("server.id"));
        update.execute();
    }

    /**
     * Save the player by name.
     * @param playerName
     * @author kvnamo
     */
    public PlayerModel getPlayer(String playerName){
        
        PlayerModel playerModel = super.plugin.getDatabase().find(PlayerModel.class)
            .where().eq("username", playerName).findUnique();
        
        if(null == playerModel){ 
            // create a new bean that is managed by bukkit
            playerModel = super.plugin.getDatabase().createEntityBean(PlayerModel.class);
            playerModel.setUsername(playerName); 
            playerModel.setWins(0);
            playerModel.setLosses(0);
            playerModel.setLastSeen((new Date())); 
            playerModel.setTimePlayed(0); 

            // Store the bean
            super.plugin.getDatabase().save(playerModel);
        }
        
        return playerModel;
    }
    
    /**
     * Gets the minecade account
     * @param playerName
     * @return MinecadeAccount
     */
    public MinecadeAccount getMinecadeAccount(String playerName){
        return super.getMinecadeAccount(playerName);
    }
    
    /**
     * Update player
     * @param playerModel
     * @author kvnamo
     */
    public void updatePlayer(PlayerModel playerModel){
        this.plugin.getDatabase().update(playerModel);
    }
}