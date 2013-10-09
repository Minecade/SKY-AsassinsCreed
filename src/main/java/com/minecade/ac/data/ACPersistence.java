package com.minecade.ac.data;

import java.util.Date;

import com.avaje.ebean.SqlUpdate;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.data.MinecadeAccount;
import com.minecade.engine.data.MinecadePersistence;

public class ACPersistence extends MinecadePersistence { 

    private final AssassinsCreedPlugin plugin;
    private int lastPlayerCount;

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
        return plugin.getDatabase().find(ServerModel.class).where().eq("serverId", serverId).findUnique();
    }

    /**
     * Creates the or update server in the db.
     * @author kvnamo
     */
    public void createOrUpdateServer() {
        ServerModel server = getServerById(this.plugin.getConfig().getInt("server.id"));
        
        if(server == null) {
         // create a new bean that is managed by bukkit
            server = this.plugin.getDatabase().createEntityBean(ServerModel.class);
            server.setServerId(this.plugin.getConfig().getInt("server.id"));
        }        

        server.setMaxPlayers(this.plugin.getConfig().getInt("match.required-players"));
        server.setOnlinePlayers(0);
        //server.setState(PMSStatusEnum.WAITING_FOR_PLAYERS);
        
        // store the bean
        this.plugin.getDatabase().save(server);
    }
    
//    /**
//     * Update server players.
//     * @param serverStatus the server status
//     * @author kvnamo
//     */
//    public void updateServerStatus(PMSStatusEnum status) {
//        StringBuilder dml = new StringBuilder("update server set state=:state ");
//        
//        if(status == PMSStatusEnum.RESTARTING) {
//            dml.append(", online_players=:online_players ");
//        }        
//        
//        dml.append("where id = :id");
//        
//        SqlUpdate update = plugin.getDatabase().createSqlUpdate(dml.toString())
//                .setParameter("state", status.toString())
//                .setParameter("id", this.plugin.getConfig().getInt("server.id"));
//        
//        if(status == PMSStatusEnum.RESTARTING) {
//            update.setParameter("online_players", 0);
//        }
//        
//        update.execute();        
//    }

    /**
     * Update server players.
     * @author kvnamo
     */
    public void updateServerPlayers() {
        
        final int playerCount = this.plugin.getServer().getOnlinePlayers().length;
        if (playerCount == this.lastPlayerCount) {
            return;
        }
        
        this.lastPlayerCount = playerCount;
        
        String dml = "update server set online_players = :online_players where id = :id";
        SqlUpdate update = this.plugin.getDatabase().createSqlUpdate(dml)
                .setParameter("online_players", playerCount)
                .setParameter("id", this.plugin.getConfig().getInt("server.id"));
        update.execute();
    }

    /**
     * Save the player by name.
     * @param playerName
     * @author kvnamo
     */
    public PlayerModel getPlayer(String playerName){
        
        PlayerModel playerModel = this.plugin.getDatabase().find(PlayerModel.class)
            .where().eq("username", playerName).findUnique();
        
        if(null == playerModel){ 
            // create a new bean that is managed by bukkit
            playerModel = this.plugin.getDatabase().createEntityBean(PlayerModel.class);
            playerModel.setUsername(playerName); 
            playerModel.setWins(0);
            playerModel.setLosses(0);
            playerModel.setLastSeen((new Date())); 
            playerModel.setTimePlayed(0); 

            // Store the bean
            this.plugin.getDatabase().save(playerModel);
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