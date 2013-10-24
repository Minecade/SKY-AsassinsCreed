package com.minecade.ac.engine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minecade.ac.enums.LowerShopEnum;
import com.minecade.ac.enums.TopShopEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;

public class ACShop {

    /**
     * Shop items and skills
     * @param plugin
     * @param bukkitPlayer
     * @author Kvnamo
     */
    public static void shop(AssassinsCreedPlugin plugin, ACPlayer player) {
 
        Player bukkitPlayer = player.getBukkitPlayer();
        World world = bukkitPlayer.getWorld();
        Location location = bukkitPlayer.getLocation().getBlock().getLocation();
        
        // Top shop
        if(ACShop.getLocation(world, TopShopEnum.ARMOR.getX(), TopShopEnum.ARMOR.getY(), TopShopEnum.ARMOR.getZ()).equals(location)){
            ACShop.buyTopShop(plugin, player, TopShopEnum.ARMOR);
        }
        else if(ACShop.getLocation(world, TopShopEnum.HEALTH.getX(), TopShopEnum.HEALTH.getY(), TopShopEnum.HEALTH.getZ()).equals(location)){
            ACShop.buyTopShop(plugin, player, TopShopEnum.HEALTH);
        }
        else if(ACShop.getLocation(world, TopShopEnum.INVISIBILITY.getX(), TopShopEnum.INVISIBILITY.getY(), TopShopEnum.INVISIBILITY.getZ()).equals(location)){
            ACShop.buyTopShop(plugin, player, TopShopEnum.INVISIBILITY);
        }
        else if(ACShop.getLocation(world, TopShopEnum.JUMP.getX(), TopShopEnum.JUMP.getY(), TopShopEnum.JUMP.getZ()).equals(location)){
            ACShop.buyTopShop(plugin, player, TopShopEnum.JUMP);
        }
        else if(ACShop.getLocation(world, TopShopEnum.SPRINT.getX(), TopShopEnum.SPRINT.getY(), TopShopEnum.SPRINT.getZ()).equals(location)){
            ACShop.buyTopShop(plugin, player, TopShopEnum.SPRINT);
        }
        //Lower shop
        else if(ACShop.getLocation(world, LowerShopEnum.BLACKALE.getX(), LowerShopEnum.BLACKALE.getY(), LowerShopEnum.BLACKALE.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.BLACKALE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.BOARDINGAXE.getX(), LowerShopEnum.BOARDINGAXE.getY(), LowerShopEnum.BOARDINGAXE.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.BOARDINGAXE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.CUTLASS.getX(), LowerShopEnum.CUTLASS.getY(), LowerShopEnum.CUTLASS.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.CUTLASS);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.DECOOY.getX(), LowerShopEnum.DECOOY.getY(), LowerShopEnum.DECOOY.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.DECOOY);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.FIREBALL.getX(), LowerShopEnum.FIREBALL.getY(), LowerShopEnum.FIREBALL.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.FIREBALL);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.HIDDENBLADE.getX(), LowerShopEnum.HIDDENBLADE.getY(), LowerShopEnum.HIDDENBLADE.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.HIDDENBLADE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.SOMEKEBOMB.getX(), LowerShopEnum.SOMEKEBOMB.getY(), LowerShopEnum.SOMEKEBOMB.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.SOMEKEBOMB);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.TOMAHAWK.getX(), LowerShopEnum.TOMAHAWK.getY(), LowerShopEnum.TOMAHAWK.getZ()).equals(location)){
            ACShop.buyLowerShop(plugin, bukkitPlayer, LowerShopEnum.TOMAHAWK);
        }
    }
       
    /**
     * Buy skills in top shop
     * @param plugin
     * @param acplayer
     * @param skill
     * @author Kvnamo
     */
    private static void buyTopShop(final AssassinsCreedPlugin plugin, final ACPlayer player, final TopShopEnum skill){
        
        final Player bukkitPlayer = player.getBukkitPlayer();
        
        if (bukkitPlayer.getLevel() >= skill.getCost()){
            
            // Run in next tick 
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    bukkitPlayer.setLevel(bukkitPlayer.getLevel() - skill.getCost());
                    
                    // If the player bought invisibility
                    if(TopShopEnum.HEALTH.equals(skill)){
                        bukkitPlayer.addPotionEffect(skill.getPotionEffect());
                        bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
                    }
                    else if(TopShopEnum.INVISIBILITY.equals(skill)){
                        player.setInvisibilityTime(2);
                    }
                    else bukkitPlayer.addPotionEffect(skill.getPotionEffect());
                    
                    bukkitPlayer.sendMessage(String.format("%sYou have bought %s skill.", ChatColor.YELLOW, skill.name()));
                }
            });
            
            return;
        }
        
        bukkitPlayer.sendMessage(String.format("%sYou do not have enough level experience to buy %s skill.", ChatColor.YELLOW, skill.name())); 
    }
    
    /**
     * Buy items in lower shop
     * @param plugin
     * @param player
     * @param item
     * @author Kvnamo
     */
    private static void buyLowerShop(final AssassinsCreedPlugin plugin, final Player bukkitPlayer, final LowerShopEnum item){
        
        if (bukkitPlayer.getLevel() >= item.getCost()){
            
            // Run in next tick 
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    bukkitPlayer.setLevel(bukkitPlayer.getLevel() - item.getCost());
                    bukkitPlayer.getInventory().addItem(item.getItem());
                    bukkitPlayer.sendMessage(String.format("%sYou have bought %s item.", ChatColor.YELLOW, item.name()));
                }
            });
            
            return;
        }
        
        bukkitPlayer.sendMessage(String.format("%sYou do not have enough level experience to buy %s item.", ChatColor.YELLOW, item.name())); 
    }
    
    /**
     * Get item or skill location
     * @param world
     * @param x
     * @param y
     * @param z
     * @return location
     * @author Kvnamo
     */
    private static Location getLocation(World world, double x, double y, double z){
        return new Location(world, x, y, z);
    }
}
