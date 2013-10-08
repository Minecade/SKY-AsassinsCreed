package com.minecade.ac.enums;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public interface ACShopInventory {

    /**
     * Get cost
     * @return cost
     * @author Kvnamo
     */
    public int getCost();
    
    /**
     * Get potion effect
     * @return effect
     * @author Kvnamo
     */
    public PotionEffect getPotionEffect();
    
    /**
     * Get item
     * @return item
     * @author Kvnamo
     */
    public ItemStack getItem();
    
    /**
     * Get X
     * @return x
     * @author Kvnamo
     */
    public double getX();
    
    /**
     * Get Y
     * @return y
     * @author Kvnamo
     */
    public double getY();
    
    /**
     * Get Z
     * @return z
     * @author Kvnamo
     */
    public double getZ();
}
