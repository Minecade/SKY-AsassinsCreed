package com.minecade.ac.listener;

import org.bukkit.GameMode;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.minecade.ac.plugin.AssassinsCreedPlugin;

public class ACListener implements Listener{

    private final AssassinsCreedPlugin plugin;
    
    /**
     * Listener constructor.
     * @author: kvnamo
     */
    public ACListener(AssassinsCreedPlugin plugin){
        this.plugin = plugin;
    }
    
    /**
     * On world initialization
     * @param event
     * @author: kvnamo
     */
    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        this.plugin.getGame().initWorld(event); 
    }
    
    /**
     * Called by PlayerJoinEvent when player joins the match.
     * @param playerJoinEvent
     * @author: kvnamo
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Remove join message.
        event.setJoinMessage(null);
        this.plugin.getGame().playerJoin(event);
    }
    
    /**
     * Called by PlayerQuitEvent when player exits the match.
     * @param playerQuitEvent
     * @author kvnamo
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        // Remove quit message.
        event.setQuitMessage(null);  
        this.plugin.getGame().playerQuit(event);
    }
    
      /**
      * Called by PlayerDeathEvent when player dies
      * @param playerDeathEvent
      * @author kvnamo
      */
     @EventHandler
     public void onPlayerDeath(PlayerDeathEvent event) {
         // Remove quit message.
         event.setDeathMessage(null);
         this.plugin.getGame().playerDeath(event);
     }
     
      /**
      * Called by PlayerRespawnEvent when player respawns.
      * @param event
      * @author 
      */
     @EventHandler
     public void onPlayerRespawn(PlayerRespawnEvent event) {
         this.plugin.getGame().playerRespawn(event); 
     }
    
    /**
     * When player moves
     * @param event
     * @author kvnamo
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        this.plugin.getGame().playerMove(event);
    }
    
    /**
    * Call by PlayerToggleFlightEvent on flight attempt
    * @param event
    * @author kvnamo
    */
   @EventHandler
   public void onPlayerSuperJump(PlayerToggleFlightEvent event) { 
       if(!GameMode.CREATIVE.equals(event.getPlayer().getGameMode())){
           this.plugin.getGame().playerSuperJump(event);
       }
   } 
    
//    /**
//     * Call when a entity is damage.
//     * @param event
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onEntityDamage(final EntityDamageEvent event) {
//        this.plugin.getGame().entityDamage(event);
//    }
//    
//    /**
//     * Call by PlayerInteractEvent handler when player interacts.
//     * @param playerInteractEvent
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        this.plugin.getGame().playerInteract(event);
//    }
    
    /**
     * On inventory open.
     * @param playerInteractEvent
     * @author kvnamo
     */
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event){
        if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest){
            event.setCancelled(true);
        }
    }
    
//    /**
//     * 
//     * @param event
//     * @author kvnamo
//     */
//    @EventHandler
//    public void playerItemBreakEvent(PlayerItemBreakEvent event){
//        ItemStack item = event.getBrokenItem().clone();
//        item.setDurability((short)0);
//        event.getPlayer().getInventory().addItem(item);
//    }
    
//    /**
//     * Call by PlayerInteractEvent handler when player interacts.
//     * @param playerInteractEvent
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onEntityShootBow(EntityShootBowEvent  event) {
//        this.plugin.getGame().playerShootBow(event);
//    }
//    
//    /**
//     * Call by ProjectileHitEvent handler when projectile hits something.
//     * @param event
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onProjectileHitEvent(ProjectileHitEvent event){
//        this.plugin.getGame().projectileHit(event);
//    }
//        
//    /** 
//     * Call by AsyncPlayerChatEvent on player chat
//     * @param event
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onPlayerChat(AsyncPlayerChatEvent event) {
//        this.plugin.getGame().chatMessage(event);
//    }
    
    /**
     * Called by PlayerGameModeChangeEvent when a player tries to change game mode.
     * @param playerGameModeChangeEvent
     * @author kvnamo
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!event.getPlayer().isOp() && GameMode.CREATIVE.equals(event.getNewGameMode())) {
            event.setCancelled(true);
        }
    }
    
//    /**
//     * On entity death
//     * @param event
//     * @author kvnamo
//     */
//    @EventHandler
//    public void onEntityDeath(EntityDeathEvent event) {
//        // Prevent mobs spawned from Pokeballs from dropping items or experience
//        event.getDrops().clear();
//        event.setDroppedExp(0);
//    }
//    
    /**
     * Call when a block breaks.
     * @param event
     * @author: kvnamo
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
    
    /**
     * Call when LeavesDecayEvent
     * @param event
     * @author kvnamo
     */
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
    
    /**
     * Call when a Player is kicked.
     * @param event
     * @author kvnamo
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    /**
     * Calls when an item is dropped.
     * @param event
     * @author kvnamo
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
