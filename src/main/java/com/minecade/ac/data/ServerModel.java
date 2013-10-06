package com.minecade.ac.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// This class is an entity that should be persisted
@Entity
// Name of the table in the database/file
@Table(name = "server")
public class ServerModel {

    @Id
    @Column(name = "id", unique = true)
    private long serverId;
    
    /**
     * Get server id
     * @return the serverId
     * @author Kvnamo
     */
    public long getServerId() {
        return serverId;
    }

    /**
     * @param serverId 
     * @author Kvnamo
     */
    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
    
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private PMSStatusEnum status = PMSStatusEnum.WAITING_FOR_PLAYERS;
    
//    /**
//     * Get status
//     * @return the state
//     * @author Kvnamo
//     */
//    public PMSStatusEnum getStatus() {
//        return status;
//    }
//
//    /**
//     * Set status
//     * @param state 
//     * @author Kvnamo
//     */
//    public void setStatus(PMSStatusEnum status) {
//        this.status = status;
//    }
    
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
