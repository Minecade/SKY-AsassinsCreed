package com.minecade.ac.engine;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.minecade.ac.enums.CharacterEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.enums.PlayerTagEnum;

public class ACScoreboard {

    /**
     * Scoreboard title
     */
    private final String TITLE = "Assassin's Creed";
    
    private final String TITLE_SERVER_FULL = "Server Full";
    
    /**
     * Scoreboard title
     */
    private final String ASSASSIN = "Assassin";
    
    /**
     * Scoreboard players left
     */
    private final String TIME_LEFT = "Time Left";
    
    /**
     * Scoreboard players to start
     */
    private final String PLAYERS_TO_START = "Players to Start";
    
    private final String PLAYERS_IN_LOBBY = "Players in Lobby";
    
    /**
     * Scoreboard objective
     */
    private final String OBJECTIVE = "Black Flag";
    
    /**
     * Scoreboard assassin lives
     */
    private final String ASSASSIN_LIVES = "Assassin's Lives";
    
    /**
     * Scoreboard Navy
     */
    private final String NAVY = "Navy";
    
    /**
     * Scoreboard Prisioners
     */
    private final String TARGETS = "Targets";
    
    /**
     * Scoreboard Prisioners
     */
    private final String PRISIONERS = "Prisioners";

    private Scoreboard scoreboard;
    
    /**
     * Gets the scoreboard
     * @author kvnamo
     */
    public Scoreboard getScoreboard(){
        return this.scoreboard;
    }

    private Objective getScoreboardObjective(){
        return this.scoreboard.getObjective(OBJECTIVE);
    }
    
    public void registerTitleServerFull(){
        this.scoreboard.getObjective(OBJECTIVE).setDisplayName(String.format("%s%s", ChatColor.GOLD, TITLE_SERVER_FULL));
    }

    public void setLobbyPlayers(int matchPlayers){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(PLAYERS_TO_START)).setScore(matchPlayers);
    }
    
    public void setPlayersInLobby(int matchPlayers){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(PLAYERS_IN_LOBBY)).setScore(matchPlayers);
    }
    
    public void setAssassinLives(int assassinlives){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(String.format("%s", ASSASSIN_LIVES))).setScore(assassinlives);
    }

    public void setNavy(int navy){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(String.format("%s%s Free", ChatColor.DARK_GRAY, NAVY))).setScore(navy);
    }

    public void setPrisioners(int prisioners){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(String.format("%s%s", ChatColor.WHITE, PRISIONERS))).setScore(prisioners);
    }

    public void setNPCs(int npcs){
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(String.format("%s%s", ChatColor.DARK_GRAY, TARGETS))).setScore(npcs);
    }

    public void setTimeLeft(int timeLeft) {
        this.getScoreboardObjective().getScore(Bukkit.getOfflinePlayer(String.format("%s%s", ChatColor.WHITE, TIME_LEFT))).setScore(timeLeft);
    }
    
    public void unassignTeam(ACPlayer player){
        Team team = this.scoreboard.getTeam(PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount()).name());
        team.removePlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
    }

    public ACScoreboard(AssassinsCreedPlugin plugin){       
        // Creates new scoreboard
        this.scoreboard =  plugin.getServer().getScoreboardManager().getNewScoreboard();
        
        // Create teams
        if(this.scoreboard.getTeams().isEmpty()){
            for(PlayerTagEnum tag: PlayerTagEnum.values()){
                this.scoreboard.registerNewTeam(tag.name()).setPrefix(tag.getPrefix());
            }
        }
        // Unregister previous scoreboard
        if (this.getScoreboardObjective() != null) {
            this.getScoreboardObjective().unregister();
        }
        
        // Setup scoreboard
        this.scoreboard.registerNewObjective(OBJECTIVE, OBJECTIVE)
            .setDisplayName(String.format("%s%s", ChatColor.GOLD, TITLE)); 
        this.getScoreboardObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    /**
     * Assign team to player
     * @param player
     * @author kvnamo
     */
    public void assignPlayerTeam(ACPlayer player){
        if(player.getBukkitPlayer().isValid()){
            PlayerTagEnum playerTag = PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount());
            Team team = this.scoreboard.getTeam(playerTag.name());
            team.addPlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
            team.setPrefix(playerTag.getPrefix());
        }
    }
    

    public void assignCharacterNavyTeam(Collection<ACPlayer> players){
        
        if(players != null && players.size() > 0){
            Team team;
            Team navyTeam;
            //create the new teams
            navyTeam = this.scoreboard.getTeam(this.NAVY);
            if(navyTeam == null){
                navyTeam = this.scoreboard.registerNewTeam(this.NAVY);
            } else {
                //clean team players if team already exist
                Iterator<OfflinePlayer> navyPlayers = navyTeam.getPlayers().iterator();
                while(navyPlayers.hasNext()) {
                    navyTeam.removePlayer(navyPlayers.next());
                }
            }
            
            for(ACPlayer player : players){
                if(!CharacterEnum.ASSASSIN.equals(player.getCharacter()) && player.getBukkitPlayer().isValid()){
                    team = this.scoreboard.getTeam(PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount()).name());
                    team.removePlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
                    navyTeam.addPlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
                    navyTeam.setPrefix(String.format("[%s%s%s] ", ChatColor.BLUE, NAVY, ChatColor.RESET));
                    player.getBukkitPlayer().setScoreboard(this.getScoreboard());
                }
            }
        }
    }
    
    public void assignAssassinTeam(ACPlayer player){
        if(player != null && CharacterEnum.ASSASSIN.equals(player.getCharacter()) 
                && player.getBukkitPlayer() != null && !player.getBukkitPlayer().isValid()){
            return;
        }
        Team team;
        Team navyTeam;
        //create the new teams
        navyTeam = this.scoreboard.getTeam(CharacterEnum.ASSASSIN.name());
        if(navyTeam == null){
            navyTeam = this.scoreboard.registerNewTeam(CharacterEnum.ASSASSIN.name());
        } else {
            //clean team if this already exist
            Iterator<OfflinePlayer> navyPlayers = navyTeam.getPlayers().iterator();
            while(navyPlayers.hasNext()) {
                navyTeam.removePlayer(navyPlayers.next());
            }
        }
        if(player != null && player.getCharacter() != null && CharacterEnum.ASSASSIN.equals(player.getCharacter())){
            team = this.scoreboard.getTeam(PlayerTagEnum.getTag(player.getBukkitPlayer(), player.getMinecadeAccount()).name());
            team.removePlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
            navyTeam.addPlayer(Bukkit.getOfflinePlayer(player.getBukkitPlayer().getName()));
            navyTeam.setPrefix(String.format("[%s%s%s] ", ChatColor.RED, ASSASSIN, ChatColor.RESET));
            player.getBukkitPlayer().setScoreboard(this.getScoreboard());
        }
    }
}
