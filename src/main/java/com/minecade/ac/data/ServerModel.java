package com.minecade.ac.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// This class is an entity that should be persisted
@Entity
// Name of the table in the database/file
@Table(name = "servers")
public class ServerModel {

    @Id
    @Column(name = "id", unique = true)
    private int serverId;
    
    /**
     * Get server id
     * @return the serverId
     * @author Kvnamo
     */
    public int getServerId() {
        return serverId;
    }

    /**
     * @param serverId 
     * @author Kvnamo
     */
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
    
    @Column(name = "max_players", nullable = false)
    private int maxPlayers;
    
    /**
     * Get max players
     * @return the maxPlayers
     * @author Kvnamo
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set max players
     * @param maxPlayers 
     * @author Kvnamo
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    @Column(name = "online_players", nullable = false)
    private int onlinePlayers;

    /**
     * Get online players
     * @return onlinePlayers
     * @author Kvnamo
     */
    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Set online players
     * @param onlinePlayers 
     * @author Kvnamo
     */
    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }
}
