package com.minecade.ac.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecade.ac.engine.ACGame;
import com.minecade.engine.utils.MinecadeCommand;

@MinecadeCommand(commandName="topshop")
public class TopShopCommand implements CommandExecutor {
    
    private final ACGame game;

    /**
     * Top Shop command constructor
     * @param game
     * @author Kvnamo
     */
    public TopShopCommand(final ACGame game) {
        this.game = game;
    }

    /**
     * On topshop command
     * @author Kvnamo
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) this.game.goToTopShop((Player)sender); 
        else sender.sendMessage(String.format("%sYou need to be in the game to do this!", ChatColor.RED));

        return true;
    }
}
