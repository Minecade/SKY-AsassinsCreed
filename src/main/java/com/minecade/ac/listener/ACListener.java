package com.minecade.ac.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
    
    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        this.plugin.getGame().initWorld(event); 
    }
    
    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event){
        this.plugin.getGame().entityTarget(event);
    }
    
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        // Prevent mobs from burning in the daylight
        if (event.getDuration() == 8 && !(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Remove join message.
        event.setJoinMessage(null);
        this.plugin.getGame().playerJoin(event);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        // Remove quit message.
        event.setQuitMessage(null);  
        this.plugin.getGame().playerQuit(event);
    }
    
     @EventHandler
     public void onEtityDeath(EntityDeathEvent event) {
         this.plugin.getGame().entityDeath(event);
         event.getDrops().clear();
         event.setDroppedExp(0);
     }
     
     @EventHandler
     public void onPlayerRespawn(PlayerRespawnEvent event) {
         this.plugin.getGame().playerRespawn(event); 
     }
     
     @EventHandler
     public void onPlayerDeath(PlayerDeathEvent event) {
         this.plugin.getGame().playerDeath(event); 
     }
     
     @EventHandler
     public void onEntityDamage(EntityDamageEvent event) {
         this.plugin.getGame().entityDamage(event);
     }
     
     @EventHandler
     public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
         this.plugin.getGame().entityDamagedByEntity(event);
     }
     
     @EventHandler
     public void onEntityShootBowEvent(EntityShootBowEvent  event){
         this.plugin.getGame().entityShootBowEvent(event);
     }
     
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        this.plugin.getGame().playerMove(event);
    }
    
   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
       this.plugin.getGame().playerInteract(event);
   }
   
   @EventHandler
   public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
       event.setCancelled(true);
   }
   
   @EventHandler
   public void onPlayerPortalEvent(PlayerPortalEvent event) {
       //this.plugin.getGame().portalEvent(event);
   }
   
   @EventHandler
   public void onEntityPortalEnterEvent(EntityPortalEnterEvent event) {
       this.plugin.getGame().portalEvent(event);
   }
   
   @EventHandler
   public void onProjectileHitEvent(ProjectileHitEvent event){
       this.plugin.getGame().projectileHit(event);
   }
   
//   /**
//    * Call by PotionSplashEvent handler when potion hits something.
//    * @param event
//    * @author kvnamo
//    */
//   @EventHandler
//   public void onPotionSplashEvent(PotionSplashEvent event){
//       this.plugin.getGame().potionSplash(event);
//   }
   
   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent event) {
       this.plugin.getGame().chatMessage(event);
   }
   
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event){
        if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest){
            event.setCancelled(true);
            return;
        }
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        this.plugin.getGame().inventoryClickEvent(event);
    }
    
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!event.getPlayer().isOp() && GameMode.CREATIVE.equals(event.getNewGameMode())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Prevent mobs spawned from Pokeballs from dropping items or experience
        event.getDrops().clear();
        
    }
    
    @EventHandler
    public void onCreatureSpawnEvent(final CreatureSpawnEvent event){
        this.plugin.getGame().creatureSpawn(event);
    }
    
    @EventHandler
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent event){
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
