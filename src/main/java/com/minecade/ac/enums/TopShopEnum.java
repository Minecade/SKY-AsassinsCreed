package com.minecade.ac.enums;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum TopShopEnum implements ACShopInventory{

    ARMOR(8, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2), -172, 114, 36),
    
    HEALTH(5, new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 3), -167, 114, 35), 
    
    INVISIBILITY(8, null, -168, 114, 36),
    
    JUMP(1, new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2), -167, 114, 33),        
    
    SPRINT(1, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), -170, 114, 36);
    
    private int cost;

    /**
     * Get cost
     * @return cost
     * @author Kvnamo
     */
    @Override
    public int getCost(){
        return this.cost;
    }
    
    private PotionEffect potionEffect;
    
    /**
     * Get effect
     * @return effect
     * @author Kvnamo
     */
    @Override
    public PotionEffect getPotionEffect(){
        return this.potionEffect;
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
    
    private double x;

    /**
     * Get X
     * @return x
     * @author Kvnamo
     */
    @Override
    public double getX(){
        return this.x;
    }
    
    
    private double y;
    
    /**
     * Get Y
     * @return y
     * @author Kvnamo
     */
    @Override
    public double getY(){
        return this.y;
    }
    
    private double z;
    
    /**
     * Get Z
     * @return z
     * @author Kvnamo
     */
    @Override
    public double getZ(){
        return this.z;
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
    private TopShopEnum(int cost, PotionEffect potionEffect, double x, double y, double z){
        this.cost = cost;
        this.potionEffect = potionEffect;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
