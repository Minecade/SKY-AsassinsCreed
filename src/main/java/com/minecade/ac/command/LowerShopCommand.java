package com.minecade.ac.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecade.ac.engine.ACGame;
import com.minecade.engine.utils.MinecadeCommand;

@MinecadeCommand(commandName="lowershop")
public class LowerShopCommand implements CommandExecutor {
    
    private final ACGame game;

    /**
     * Lower Shop command constructor
     * @param game
     * @author Kvnamo
     */
    public LowerShopCommand(final ACGame game) {
        this.game = game;
    }

    /**
     * On lowershop command
     * @author Kvnamo
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) this.game.goToLowerShop((Player)sender); 
        else sender.sendMessage(String.format("%sYou need to be in the game to do this!", ChatColor.RED));

        return true;
    }
}
