package com.minecade.ac.enums;

import org.bukkit.Effect;
import org.bukkit.Location;

public enum ShopEnum implements ACShop{

    // Improved armor
    ARMOR(8, null, -172, 115, 36),
    
    HEALTH(5, null, -167, 115, 35), 
    
    INVISIBILITY(8, null, -168, 115, 36),
    
    JUMP(1, null, -167, 115, 33),        
    
    SPRINT(1, null, -170, 115, 36);
    
    private int cost;
    private Location location;
    private Effect effect;
    
    /**
     * Get cost
     * @return cost
     * @author Kvnamo
     */
    @Override
    public int getCost(){
        return this.cost;
    }
    
    /**
     * Get effect
     * @return effect
     * @author Kvnamo
     */
    @Override
    public Effect getEffect(){
        return this.effect;
    }
    
    /**
     * Get Location
     * @return location
     * @author Kvnamo
     */
    @Override
    public Location getLocation(){
        return this.location;
    }
    
    /**
     * Shop enum constructor
     * @param cost
     * @param effect
     * @param location
     * @author Kvnamo
     */
    private ShopEnum(int cost, Effect effect, double x, double y, double z){
        this.cost = cost;
        this.effect = effect;
        //this.location = new location(x, y, z);
    }
}
