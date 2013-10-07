package com.minecade.ac.enums;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public enum TopShopEnum implements ACShop{

    ARMOR(8, null, -172, 115, 36),
    
    HEALTH(5, null, -167, 115, 35), 
    
    INVISIBILITY(8, null, -168, 115, 36),
    
    JUMP(1, null, -167, 115, 33),        
    
    SPRINT(1, null, -170, 115, 36);
    
    private int cost;
    private Effect effect;
    private Location location;
    
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
     * Get item
     * @return item
     * @author Kvnamo
     */
    @Override
    public ItemStack getItem() {
        return null;
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
     * @param x
     * @param y
     * @param z
     * @author Kvnamo
     */
    private TopShopEnum(int cost, Effect effect, double x, double y, double z){
        this.cost = cost;
        this.effect = effect;
        //this.location = new location(x, y, z);
    }
}
