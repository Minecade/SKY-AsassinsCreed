package com.minecade.ac.engine;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.engine.utils.EngineUtils;

public class ACCharacter {

    /**
     * Setup the player's character
     * @param player
     * @author Kvnamo
     */
    public static void setupPlayer(final ACPlayer player) {
        
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        
        switch (player.getCharacter()) {
        
            case ASSASSIN:
                // Set default lives
                player.setLives(3);
                player.getBukkitPlayer().setLevel(1);
             
                // Set potions
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getInvisibleEmerald());
                player.getBukkitPlayer().sendMessage(String.format("%sYou are the Assassin!", ChatColor.RED));
                break;
            
            case BODYGUARD:
                // Set default lives
                player.setLives(1);
                
                // Set potions 
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 5));
                
                // Set inventory
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getSolidCane());
                player.getBukkitPlayer().sendMessage(String.format("%sNavy: You are a Bodyguard!", ChatColor.BLUE));
                break;
                    
            case MUSKETEER:
                // Set default lives
                player.setLives(1);
                
                // Set potions
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                
                // Set inventory
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getStrongCane());
                player.getBukkitPlayer().sendMessage(String.format("%sNavy: You are a Musketeer!", ChatColor.BLUE));
                break;
                
            case SWORDSMAN:
                // Set default lives
                player.setLives(1);
                
                // Set potions
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1));
                
                // Set inventory
                player.getBukkitPlayer().getInventory().addItem(ACInventory.getCane());
                player.getBukkitPlayer().sendMessage(String.format("%sNavy: You are a Swordsman!", ChatColor.BLUE));
                break;

            default:
                break;
        }
    }

    /**
     * Setup zombie
     * @param creature
     * @param zombie
     * @author Kvnamo
     */
    public static void setupZombie(Zombie zombie, NPCEnum npc){
        
        // Set characteristics
        zombie.setHealth(20);
        zombie.setTarget(null);
        zombie.setCustomName(npc.name());
        
        // Set equipment
        EntityEquipment equipment = zombie.getEquipment();
        
        switch (npc) {
            case GREEN:
                equipment.setArmorContents(ACInventory.getZombieArmor(Color.GREEN));
            break;
            case GRAY:
                equipment.setArmorContents(ACInventory.getZombieArmor(Color.GRAY));
                break;
            case RED:
                equipment.setArmorContents(ACInventory.getZombieArmor(Color.RED));
                break;
            case YELLOW:
                equipment.setArmorContents(ACInventory.getZombieArmor(Color.YELLOW));
                break;
            case WHITE:
                equipment.setArmorContents(ACInventory.getZombieArmor(Color.WHITE));
                break;
            default:
                break;
    }
}
}