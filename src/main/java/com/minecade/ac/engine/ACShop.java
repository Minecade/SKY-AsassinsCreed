package com.minecade.ac.engine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minecade.ac.enums.LowerShopEnum;
import com.minecade.ac.enums.TopShopEnum;

public class ACShop {

    /**
     * Shop items and skills
     * @param player
     * @author Kvnamo
     */
    public static void shop(Player player) {
 
        World world = player.getWorld();
        Location location = player.getLocation();
        
        // Top shop
        if(ACShop.getLocation(world, TopShopEnum.ARMOR.getX(), TopShopEnum.ARMOR.getY(), TopShopEnum.ARMOR.getZ()).equals(location)){
            ACShop.buyTopShop(player, TopShopEnum.ARMOR);
        }
        else if(ACShop.getLocation(world, TopShopEnum.HEALTH.getX(), TopShopEnum.HEALTH.getY(), TopShopEnum.HEALTH.getZ()).equals(location)){
            ACShop.buyTopShop(player, TopShopEnum.HEALTH);
        }
        else if(ACShop.getLocation(world, TopShopEnum.INVISIBILITY.getX(), TopShopEnum.INVISIBILITY.getY(), TopShopEnum.INVISIBILITY.getZ()).equals(location)){
            ACShop.buyTopShop(player, TopShopEnum.INVISIBILITY);
        }
        else if(ACShop.getLocation(world, TopShopEnum.JUMP.getX(), TopShopEnum.JUMP.getY(), TopShopEnum.JUMP.getZ()).equals(location)){
            ACShop.buyTopShop(player, TopShopEnum.JUMP);
        }
        else if(ACShop.getLocation(world, TopShopEnum.SPRINT.getX(), TopShopEnum.SPRINT.getY(), TopShopEnum.SPRINT.getZ()).equals(location)){
            ACShop.buyTopShop(player, TopShopEnum.SPRINT);
        }
        //Lower shop
        else if(ACShop.getLocation(world, LowerShopEnum.BLACKALE.getX(), LowerShopEnum.BLACKALE.getY(), LowerShopEnum.BLACKALE.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.BLACKALE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.BOARDINGAXE.getX(), LowerShopEnum.BOARDINGAXE.getY(), LowerShopEnum.BOARDINGAXE.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.BOARDINGAXE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.CUTLASS.getX(), LowerShopEnum.CUTLASS.getY(), LowerShopEnum.CUTLASS.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.CUTLASS);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.DECOOY.getX(), LowerShopEnum.DECOOY.getY(), LowerShopEnum.DECOOY.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.DECOOY);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.GRENADE.getX(), LowerShopEnum.GRENADE.getY(), LowerShopEnum.GRENADE.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.GRENADE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.HIDDENBLADE.getX(), LowerShopEnum.HIDDENBLADE.getY(), LowerShopEnum.HIDDENBLADE.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.HIDDENBLADE);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.SOMEKEBOMB.getX(), LowerShopEnum.SOMEKEBOMB.getY(), LowerShopEnum.SOMEKEBOMB.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.SOMEKEBOMB);
        }
        else if(ACShop.getLocation(world, LowerShopEnum.TOMAHAWK.getX(), LowerShopEnum.TOMAHAWK.getY(), LowerShopEnum.TOMAHAWK.getZ()).equals(location)){
            ACShop.buyLowerShop(player, LowerShopEnum.TOMAHAWK);
        }
    }
       
    /**
     * Buy skills in top shop
     * @param player
     * @param skill
     * @author Kvnamo
     */
    private static void buyTopShop(Player player, TopShopEnum skill){
        
        if (player.getExp() >= skill.getCost()){
            player.setExp(player.getExp() - skill.getCost());
            player.addPotionEffect(skill.getPotionEffect());
        }
        
        player.sendMessage(String.format("%sYou do not have enough experience to buy this.", ChatColor.YELLOW)); 
    }
    
    /**
     * Buy items in lower shop
     * @param player
     * @param item
     * @author Kvnamo
     */
    private static void buyLowerShop(Player player, LowerShopEnum item){
        if (player.getExp() >= item.getCost()){
            player.setExp(player.getExp() - item.getCost());
            player.getInventory().addItem(item.getItem());
        }
        
        player.sendMessage(String.format("%sYou do not have enough experience to buy this.", ChatColor.YELLOW)); 
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
