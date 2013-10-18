package com.minecade.ac.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.minecade.ac.enums.ServerStatusEnum;

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
    
    @Column(name = "world_name", nullable = false)
    private String worldName;
    
    /**
     * Get the world name
     * @return world name
     * @author Kvnamo
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Set the world name
     * @param world name 
     * @author Kvnamo
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ServerStatusEnum status = ServerStatusEnum.WAITING_FOR_PLAYERS;
    
    /**
     * Get the status
     * @return status
     * @author Kvnamo
     */
    public ServerStatusEnum getStatus() {
        return status;
    }

    /**
     * Set the status
     * @param status
     * @author Kvnamo
     */
    public void setStatus(ServerStatusEnum status) {
        this.status = status;
    }
}
