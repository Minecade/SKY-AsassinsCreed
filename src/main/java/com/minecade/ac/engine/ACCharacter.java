package com.minecade.ac.engine;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minecade.ac.enums.NPCEnum;
import com.minecade.ac.plugin.AssassinsCreedPlugin;
import com.minecade.engine.utils.EngineUtils;

public class ACCharacter {

    public static void assassin(ACPlayer player) { 
        
        // Clear player
        EngineUtils.clearBukkitPlayer(player.getBukkitPlayer());
        // Set inventory
        player.getBukkitPlayer().setLevel(1);
        player.getBukkitPlayer().setHealth(20);
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getInvisibleEmerald());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getSmokeBomb());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getPlaceFinder());
        player.getBukkitPlayer().getInventory().addItem(ACInventory.getShopIcon());
        player.getBukkitPlayer().getInventory().setArmorContents(ACInventory.getAssassinArmor());
//        player.getBukkitPlayer().getInventory().addItem(ACInventory.getCutLass());
//        player.getBukkitPlayer().getInventory().addItem(ACInventory.getDecoy());
//        player.getBukkitPlayer().getInventory().addItem(ACInventory.getBoardingAxe());
//        player.getBukkitPlayer().getInventory().addItem(ACInventory.getTomaHawk());
//        player.getBukkitPlayer().getInventory().addItem(ACInventory.getBlindnessPotion());
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
    }
    
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
    }
    
    
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
    }

    public static void zombie(AssassinsCreedPlugin plugin, final Zombie zombie, final NPCEnum npc){
        // Set characteristics
        zombie.setTarget(null);
        zombie.setMaxHealth(7);
        zombie.setVillager(false);
        zombie.setRemoveWhenFarAway(false);
        zombie.setCustomName(String.format("%s%s%s", npc.getChatColor(), ChatColor.BOLD, npc.name()));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -6));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 30));
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

