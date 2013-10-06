package com.minecade.ac.engine;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class ACInventory {

    /**
     * Get Bow
     * @return Bow
     * @author kvnamo
     */
    public static ItemStack getBow() {
        
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.AQUA + "Bow");
        bowMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreaking X.",
                ChatColor.DARK_GRAY + "An excellent long range weapon.",
                ChatColor.BLUE + "Infinity I"));
        
        bow.setItemMeta(bowMeta);
        
        return bow;
    }
    
    /**
     * Get Cane 
     * @author Kvnamo
     */
    public static ItemStack getCane(){
        
        final ItemStack cane = new ItemStack(Material.STICK, 1);
        cane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        
        final ItemMeta caneMeta = cane.getItemMeta();
        caneMeta.setDisplayName(ChatColor.RED + "Cane");
        caneMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Knockback  II.",
            ChatColor.DARK_GRAY + "Used for splitting up feuding individuals."));
        
        cane.setItemMeta(caneMeta);
        
        return cane;
    }
    
// FIXME: esto podría estar en el mapa    
//    /**
//     * Get hidden blade 
//     * @author Kvnamo
//     */
//    public static ItemStack getHiddenBlade(){
//        
//        final ItemStack hiddenBlade = new ItemStack(Material.STICK, 1);
//        hiddenBlade.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
//        
//        final ItemMeta hiddenBladeMeta = hiddenBlade.getItemMeta();
//        hiddenBladeMeta.setDisplayName(ChatColor.RED + "Hidden Blade");
//        hiddenBladeMeta.setLore(Arrays.asList(
//            ChatColor.GRAY + "Unbreaking X.",
//            ChatColor.DARK_GRAY + "A simple pirating tool.",
//            ChatColor.BLUE + "2+ Attack Damage."));
//        
//        hiddenBlade.setItemMeta(caneMeta);
//        
//        return cane;
//    }
    
    /**
     * Instructions book
     * @return InstructionBook
     * @author kvnamo
     */
    public static ItemStack getInstructionsBook(){
        
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle(ChatColor.YELLOW + "Instructions book");
        bookMeta.setPages(
            String.format("%s%sWELCOME TO TNT RUN! \n\n\n%s" +
                "Run all over the map to destroy the blocks so other players fall into the void. Last player remaining wins the game.", 
                ChatColor.BOLD, ChatColor.RED, ChatColor.DARK_GRAY),
            String.format("%s%sRULES! \n\n\n %s1. Diamond blocks gives you double jumps like XP points.\n" +
                "2. Gold blocks gives you speed boost.\n" +
                "3. TNT blocks drops all block around it.\n" +
                "4. If you quit during a match it will count as a lost",
                ChatColor.BOLD, ChatColor.RED, ChatColor.DARK_GRAY));
        book.setItemMeta(bookMeta);
        
        return book;
    }
    
    /**
     * Invisible meca
     * @return Invisible Meca.
     * @author kvnamo
     */
    public static ItemStack getInvisibleMeca() {
        
        final ItemStack invisibleMeca = new ItemStack(Material.EMERALD, 1);
        
        ItemMeta invisibleMecaMeta = (ItemMeta) invisibleMeca.getItemMeta();
        invisibleMecaMeta.setDisplayName(ChatColor.RED + "Invisible Meca");            
        invisibleMecaMeta.setLore(Arrays.asList(ChatColor.GRAY + "10 seconds of invisivility"));          
        invisibleMeca.setItemMeta(invisibleMecaMeta);
        
        return invisibleMeca;
    }
    
    /**
     * Leave Compass
     * @return leaveCompass 
     * @author kvnamo
     */
    public static ItemStack getLeaveCompass() {
        
        final ItemStack compass = new ItemStack(Material.COMPASS, 1);
        
        final ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.RED + "Leave Game");            
        compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to Use"));            
        compass.setItemMeta(compassMeta);
        
        return compass;
    }
    
    /**
     * Get scimitar
     * @author Kvnamo
     */
    public static ItemStack getScimitar(){
        
        final ItemStack scimitar = new ItemStack(Material.STONE_SWORD, 1);
        scimitar.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta scimitarMeta = scimitar.getItemMeta();
        scimitarMeta.setDisplayName(ChatColor.GREEN + "Scimitar");
        scimitarMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Unbreaking X.",
            ChatColor.DARK_GRAY + "The perfect weapon for a member of the Navy."));
        
        scimitar.setItemMeta(scimitarMeta);
        
        return scimitar;
    }
    
    /**
     * Get solid cane
     * @author Kvnamo
     */
    public static ItemStack getSolidCane(){
        
        final ItemStack solidCane = new ItemStack(Material.STICK, 1);
        solidCane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
        
        final ItemMeta solidCaneMeta = solidCane.getItemMeta();
        solidCaneMeta.setDisplayName(ChatColor.RED + "Solid Cane");
        solidCaneMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Knockback IV.",
                ChatColor.DARK_GRAY + "No one is getting past you."));
        
        solidCane.setItemMeta(solidCaneMeta);
        
        return solidCane;
    }
    
    /**
     * Stats Book
     * @return stats book Item.
     * @author kvnamo
     */
    public static ItemStack getStatsBook(ACPlayer player) {
        
        final ItemStack stats = new ItemStack(Material.WRITTEN_BOOK, 1);
        
        final BookMeta statsMeta = (BookMeta) stats.getItemMeta();
        statsMeta.setTitle(ChatColor.LIGHT_PURPLE + "Stats book");
        
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        String kdr = (double)player.getPlayerModel().getLosses() != 0 ? 
            "0" : decimalFormat.format((double)player.getPlayerModel().getKills() / (double)player.getPlayerModel().getLosses());
        String timePlayed = decimalFormat.format((double)player.getPlayerModel().getTimePlayed() / (double)86400);
        
        statsMeta.setPages(
                String.format("%s%s%s STATS! \n\n\n%s %sWins: %s%s\n %sLooses: %s%s\n %sKills: %s%s\n %sKill/Death ratio: %s%s\n %sTime played: %s%s days.",
                ChatColor.BOLD, ChatColor.LIGHT_PURPLE, player.getBukkitPlayer().getName().toUpperCase(), ChatColor.DARK_GRAY,
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getWins(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getLosses(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getKills(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, kdr,
                ChatColor.BOLD, ChatColor.DARK_GRAY, timePlayed));
        stats.setItemMeta(statsMeta);
        
        return stats;
    } 
    
    /**
     * Get strong cane
     * @author Kvnamo
     */
    public static ItemStack getStrongCane(){
        
        final ItemStack strongCane = new ItemStack(Material.STICK, 1);
        strongCane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
        
        final ItemMeta strongCaneMeta = strongCane.getItemMeta();
        strongCaneMeta.setDisplayName(ChatColor.GOLD + "Strong Cane");
        strongCaneMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Knockback III.",
            ChatColor.DARK_GRAY + "For when they are too close to shoot."));
        
        strongCane.setItemMeta(strongCaneMeta);
        
        return strongCane;
    }  
    
    /**
     * Get zombie armor
     * @return zombie armor content.
     * @author kvnamo
     */
    public static ItemStack[] getZombieArmor(Color green) {      

        final ItemStack[] armor = new ItemStack[4];
        
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        final LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(green);      
        helmet.setItemMeta(helmetMeta);
                
        armor[3] = helmet;
        
        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        final LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(green);
        chestplate.setItemMeta(chestplateMeta);
        
        armor[2] = chestplate;
        
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        final LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(green);
        leggings.setItemMeta(leggingsMeta);
        
        armor[1] = leggings;
        
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        final LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta(); 
        bootsMeta.setColor(green);
        boots.setItemMeta(bootsMeta);
        
        armor[0] = boots;
        
        return armor;
    }
}
