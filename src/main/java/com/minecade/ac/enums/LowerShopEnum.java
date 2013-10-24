package com.minecade.ac.enums;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.minecade.ac.engine.ACInventory;

public enum LowerShopEnum implements ACShopInventory{

    BLACKALE(5, ACInventory.getBlackAle(), -172, 107, 21),
    
    BOARDINGAXE(4, ACInventory.getBoardingAxe(), -170, 107, 15),
    
    CUTLASS(5, ACInventory.getCutLass(), -166, 107, 15), 
    
    DECOOY(3, ACInventory.getDecoy(), -166, 107, 21),
    
    FIREBALL(8, ACInventory.getFireball(), -170, 107, 21),
    
    HIDDENBLADE(1, ACInventory.getHiddenBlade(), -172, 107, 15), 
    
    SOMEKEBOMB(1, ACInventory.getSmokeBomb(), -168, 107, 21),
    
    TOMAHAWK(1, ACInventory.getTomaHawk(), -168, 107, 15);
    
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
    
    /**
     * Get potion effect
     * @return potion effect
     * @author Kvnamo
     */
    @Override
    public PotionEffect getPotionEffect(){
        return null;
    }
    
    private ItemStack item;
    
    /**
     * Get item
     * @return item
     * @author Kvnamo
     */
    @Override
    public ItemStack getItem() {
        return this.item;
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
    * @param item
    * @param x
    * @param y
    * @param z
    * @author Kvnamo
    */
    private LowerShopEnum(int cost, ItemStack item, double x, double y, double z){
        this.cost = cost;
        this.item = item;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
