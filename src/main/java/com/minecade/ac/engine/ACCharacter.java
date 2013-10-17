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
     * Setup the assassin
     * @param player
     * @author Kvnamo
     */
    public static void assassin(final ACPlayer player) { 
        
        // Clear player
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        
        // Set default lives
        player.setLives(3);
        
        // Set inventory
        player.setInvisibilityTime(1);
        player.getBukkitPlayer().setLevel(1);
        player.getBukkitPlayer().setHealth(30);
        player.getBukkitPlayer().setMaxHealth(30);
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getInvisibleEmerald());
        player.getBukkitPlayer().getInventory().setArmorContents(ACInventory.getAssassinArmor());
    }
    
    /**
     * Setup the bodyguard
     * @param player
     * @author Kvnamo
     */
    public static void bodyguard(final ACPlayer player) { 
        
        // Clear player
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        
        // Set default lives
        player.setLives(1);
        
        // Set potions 
        player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
        player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 5));
        
        // Set inventory
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getSolidCane());
        player.getBukkitPlayer().sendMessage(String.format(
            "%s%sYou are a Royal Navy Bodyguard. MISSION: defend the 5 victims from the Assassin.", 
            ChatColor.BLUE, ChatColor.BOLD));
    }
    
    /**
     * Setup the musketeer
     * @param player
     * @author Kvnamo
     */
    public static void musketeer(final ACPlayer player) {
        
        // Clear player
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        
        // Set default lives
        player.setLives(1);
        
        // Set potions
        player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        
        // Set inventory
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getStrongCane());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getBow());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getArrow());
        player.getBukkitPlayer().sendMessage(String.format(
            "%s%sYou are a Royal Navy Musketeer. MISSION: defend the 5 victims from the Assassin.", 
            ChatColor.BLUE, ChatColor.BOLD)); 
    }
    
    /**
     * Setup the swordsman
     * @param player
     * @author Kvnamo
     */
    public static void swordsman(final ACPlayer player) {
        
        // Clear player
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());

        // Set default lives
        player.setLives(1);
        
        // Set potions
        player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1));
        
        // Set inventory
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getCane());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getScimitar());
        player.getBukkitPlayer().sendMessage(String.format(
            "%s%sYou are a Royal Navy Swordsman. MISSION: defend the 5 victims from the Assassin.", 
            ChatColor.BLUE, ChatColor.BOLD));        
    }

    /**
     * Setup zombie
     * @param creature
     * @param zombie
     * @author Kvnamo
     */
    public static void zombie(Zombie zombie, NPCEnum npc){
        
        // Set characteristics
        zombie.setTarget(null);
        zombie.setMaxHealth(30);
        zombie.setVillager(false);
        zombie.setCustomName(npc.getChatColor() + npc.name());
        
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