package com.minecade.ac.engine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.enums.PlayerTagEnum;

public class ACScoreboard {

    /**
     * Scoreboard title
     */
    private final String TITLE = "Assassins Creed";
    
    /**
     * Scoreboard objective
     */
    private final String OBJECTIVE = "Black Flag";
    
    /**
     * Scoreboard players to start
     */
    private final String PLAYERS_TO_START = "Players to Start"; 
    
    /**
     * Scoreboard assassin lives
     */
    private final String ASSASSIN_LIVES = "Assassin lives";
    
    /**
     * Scoreboard Navy
     */
    private final String NAVY = "Navy";
    
    /**
     * Scoreboard Prisioners
     */
    private final String PRISIONERS = "Navy prisioners";
    
    /**
     * Scoreboard Prisioners
     */
    private final String TARGETS = "Targets";
    
    /**
     * Scoreboard players left
     */
    private final String TIME_LEFT = "Time Left";
    
    
    private Scoreboard scoreboard;
    
    /**
     * Gets the scoreboard
     * @author kvnamo
     */
    public Scoreboard getScoreboard(){
        return this.scoreboard;
    }
    
    /**
     * Scoreboard objective
     * @return The scoreboard objective.
     * @author kvnamo
     */
    private Objective getScoreboardObjective(){
        return this.scoreboard.getObjective(OBJECTIVE);
    }
    
    /**
     * Sets the number of players necessaries to start the game
     * @param playersToStart
     * @author kvnamo
     */
    public void setPlayersToStart(int matchPlayers){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(PLAYERS_TO_START)).setScore(matchPlayers);
    }
    
    /**
     * Sets the assassin remaining lives
     * @param assassinlives
     * @author kvnamo
     */
    public void setAssassinLives(int assassinlives){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(ASSASSIN_LIVES)).setScore(assassinlives);
    }
    
    /**
     * Sets the current alive navy 
     * @param navy
     * @author kvnamo
     */
    public void setNavy(int navy){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(NAVY)).setScore(navy);
    }
    
    /**
     * Sets the current prisioners
     * @param playersToStart
     * @author kvnamo
     */
    public void setPrisioners(int prisioners){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(PRISIONERS)).setScore(prisioners);
    }
    
    /**
     * Sets the current alive targets 
     * @param playersToStart
     * @author kvnamo
     */
    public void setNPCs(int npcs){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(TARGETS)).setScore(npcs);
    }
    
    /**
     * Sets the time left
     * @param timeLeft
     * @author kvnamo
     */
    public void setTimeLeft(int timeLeft) {
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(TIME_LEFT)).setScore(timeLeft);
    }
    
    /**
     * PMScoreboard constructor
     * @param plugin
     * @author kvnamo
     */
    public ACScoreboard(AssassinsCreedPlugin plugin){       
        // Creates new scoreboard
        this.scoreboard =  plugin.getServer().getScoreboardManager().getNewScoreboard();
        
        // Create teams
        if(this.scoreboard.getTeams().isEmpty()){
            for(PlayerTagEnum tag: PlayerTagEnum.values()){
                this.scoreboard.registerNewTeam(tag.name()).setPrefix(tag.getPrefix());
            }
        }
    }
    
    /**
     * Init scoreboard
     * @author kvnamo
     */
    public void init(){
        // Unregister previous scoreboard
        if (this.getScoreboardObjective() != null) {
            this.getScoreboardObjective().unregister();
        }
        
        // Setup scoreboard
        this.scoreboard.registerNewObjective(OBJECTIVE, OBJECTIVE)
            .setDisplayName(String.format("%s%s", ChatColor.RED, TITLE)); 
        this.getScoreboardObjective().setDisplaySlot(DisplaySlot.SIDEBAR);   
    }
    
    /**
     * Assign team to player
     * @param player
     * @author kvnamo
     */
    public void assignTeam(ACPlayer player){
        PlayerTagEnum playerTag = PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount());
        
        Team team = this.scoreboard.getTeam(playerTag.name());
        team.addPlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
        team.setPrefix(playerTag.getPrefix());
    }
}
