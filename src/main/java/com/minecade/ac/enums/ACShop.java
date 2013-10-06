package com.minecade.ac.enums;

import org.bukkit.Effect;
import org.bukkit.Location;

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
     * Get location
     * @return location
     * @author Kvnamo
     */
    public Location getLocation();
}
