package com.minecade.ac.enums;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface ACShop {

    /**
     * Get cost
     * @return cost
     * @author Kvnamo
     */
    public int getCost();
    
    /**
     * Get effect
     * @return effect
     * @author Kvnamo
     */
    public Effect getEffect();
    
    /**
     * Get item
     * @return item
     * @author Kvnamo
     */
    public ItemStack getItem();
    
    /**
     * Get location
     * @return location
     * @author Kvnamo
     */
    public Location getLocation();
}
