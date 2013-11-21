package com.minecade.ac.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

// this class is an entity that should be persisted
@Entity
// name of the table in the database/file
@Table(name = "players")
public class PlayerModel {
    
    @Id
    @Column(name = "username", length = 16, unique = true, nullable = false)
    private String username;
    @Column(name = "wins", nullable = false)
    private int wins;
    @Column(name = "losses", nullable = false)
    private int losses;
    @Column(name = "time_played", nullable = false)
    private int timePlayed;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_seen", nullable = false)
    private Date lastSeen;
    @Column(name = "kills", nullable = false)
    private long kills;
    @Column(name = "deaths", nullable = false)
    private long deaths;
    
    /**
     * Get username
     * @return username
     * @author Kvnamo
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     * @param username 
     * @author Kvnamo
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get wins
     * @return wins
     * @author Kvnamo
     */
    public int getWins() {
        return wins;
    }

    /**
     * Set wins
     * @param wins
     * @author Kvnamo
     */
    public void setWins(int wins) {
        this.wins = wins;
    }
    /**
     * Get losses
     * @return losses
     * @author Kvnamo
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Set losses
     * @param losses 
     * @author Kvnamo
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }
    /**
     * Get total time played
     * @return total time played
     * @author Kvnamo
     */
    public int getTimePlayed() {
        return this.timePlayed;
    }

    /**
     * Set total time played
     * @param total time played
     * @author Kvnamo
     */
    public void setTimePlayed(int timePlayed) {
        this.timePlayed = timePlayed;
    }
    /**
     * Get last seen 
     * @return lastSeen
     * @author Kvnamo
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * Set last seen
     * @param lastSeen 
     * @author Kvnamo
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * @return the kills
     */
    public long getKills() {
        return kills;
    }

    /**
     * @param kills the kills to set
     */
    public void setKills(long kills) {
        this.kills = kills;
    }

    /**
     * @return the deaths
     */
    public long getDeaths() {
        return deaths;
    }

    /**
     * @param deaths the deaths to set
     */
    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }
}
