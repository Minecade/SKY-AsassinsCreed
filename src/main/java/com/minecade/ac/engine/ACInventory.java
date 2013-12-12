package com.minecade.ac.engine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class ACInventory {
    
    public static ItemStack[] getAssassinArmor() {
        
        final ItemStack[] armor = new ItemStack[4];
        
        final ItemStack helmet = new ItemStack(Material.IRON_HELMET, 1);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, Integer.MAX_VALUE);
        
        armor[3] = helmet;
        
        final ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
        chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, Integer.MAX_VALUE);
        
        armor[2] = chestplate;
        
        final ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
        leggings.addUnsafeEnchantment(Enchantment.DURABILITY, Integer.MAX_VALUE);
        
        armor[1] = leggings;
        
        final ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, Integer.MAX_VALUE);
        
        armor[0] = boots;
        
        return armor;
    }
    
    //Navy weapon
    public static ItemStack getArrow() {
        return new ItemStack(Material.ARROW, 1);
    }
    //Navy weapon
    public static ItemStack getBow() {
        
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "Musket");
        bowMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreaking X.",
                ChatColor.DARK_GRAY + "An excellent long range weapon.",
                ChatColor.BLUE + "Infinity I"));
        
        bow.setItemMeta(bowMeta);
        
        return bow;
    }
    //Navy weapon
    public static ItemStack getStrongCane(){
        
        final ItemStack strongCane = new ItemStack(Material.STICK, 1);
        strongCane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
        
        final ItemMeta strongCaneMeta = strongCane.getItemMeta();
        strongCaneMeta.setDisplayName(ChatColor.WHITE + "Strong Cane");
        strongCaneMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Knockback III.",
            ChatColor.DARK_GRAY + "For when they are too close to shoot."));
        
        strongCane.setItemMeta(strongCaneMeta);
        
        return strongCane;
    }
    //Navy Weapon
    public static ItemStack getCane(){
        
        final ItemStack cane = new ItemStack(Material.STICK, 1);
        cane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        
        final ItemMeta caneMeta = cane.getItemMeta();
        caneMeta.setDisplayName(ChatColor.WHITE + "Cane");
        caneMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Knockback  II.",
            ChatColor.DARK_GRAY + "Used for splitting up feuding individuals."));
        
        cane.setItemMeta(caneMeta);
        
        return cane;
    }
    //Navy Weapon
    public static ItemStack getScimitar(){
        
        final ItemStack scimitar = new ItemStack(Material.STONE_SWORD, 1);
        scimitar.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta scimitarMeta = scimitar.getItemMeta();
        scimitarMeta.setDisplayName(ChatColor.WHITE + "Scimitar");
        scimitarMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Unbreaking X.",
            ChatColor.DARK_GRAY + "The perfect weapon for a member of the Navy."));
        
        scimitar.setItemMeta(scimitarMeta);
        
        return scimitar;
    }
    //Navy Weapon
    public static ItemStack getSolidCane(){
        
        final ItemStack solidCane = new ItemStack(Material.STICK, 1);
        solidCane.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
        solidCane.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        
        final ItemMeta solidCaneMeta = solidCane.getItemMeta();
        solidCaneMeta.setDisplayName(ChatColor.WHITE + "Solid Cane");
        solidCaneMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Knockback IV.",
                ChatColor.DARK_GRAY + "No one is getting past you."));
        
        solidCane.setItemMeta(solidCaneMeta);
        
        return solidCane;
    }
     

    public static ItemStack getCutLass() {
        
        ItemStack cutlass = new ItemStack(Material.STONE_AXE, 1);
        cutlass.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        cutlass.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta cutlassMeta = cutlass.getItemMeta();
        cutlassMeta.setDisplayName(ChatColor.WHITE + "Cutlass");
        cutlassMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreaking X.",
                ChatColor.DARK_GRAY + "A pirate assassin's best friend.",
                ChatColor.BLUE + "+5 Attack Damage."));
        
        cutlass.setItemMeta(cutlassMeta);
        
        return cutlass;
    }

    public static ItemStack getDecoy() {
        
        ItemStack decoy = new ItemStack(Material.EGG, 1);
        
        final ItemMeta decoyMeta = decoy.getItemMeta();
        decoyMeta.setDisplayName(ChatColor.WHITE + "Decoy");
        decoyMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "A Skeletal crewman willing to cause a distraction."));
        
        decoy.setItemMeta(decoyMeta);
        
        return decoy;
    }
    
    public static ItemStack getFireball(){

        final ItemStack granade = new ItemStack(Material.FIREBALL, 10);
        
        final ItemMeta granadeMeta = granade.getItemMeta();
        granadeMeta.setDisplayName(ChatColor.GREEN + "Fireball");
        granadeMeta.setLore(Arrays.asList(ChatColor.GRAY + "Set player on fire on Impact"));
        granade.setItemMeta(granadeMeta);
        
        return granade;
    }
    
    public static ItemStack getImproveEmerald(ACPlayer player){
        ItemStack improveEmerald = new ItemStack(Material.EMERALD_ORE, 1);
        ItemMeta emeraldMeta = improveEmerald.getItemMeta();
        emeraldMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Long effect emerald");
        if(player != null && player.isEmeraldImproved()){
            emeraldMeta.setLore(Arrays.asList(ChatColor.GREEN + "Item Purchased"));
        } else {
            emeraldMeta.setLore(Arrays.asList(ChatColor.GRAY + "Improve your emerald, it will have an effect of 20 seconds"));
        }
        improveEmerald.setItemMeta(emeraldMeta);
        return improveEmerald;
    }
    
    public static ItemStack getHiddenBlade(){
        
        final ItemStack hiddenBlade = new ItemStack(Material.STICK, 1);
        hiddenBlade.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        hiddenBlade.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        final ItemMeta hiddenBladeMeta = hiddenBlade.getItemMeta();
        hiddenBladeMeta.setDisplayName(ChatColor.RED + "Hidden Blade");
        hiddenBladeMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Unbreaking X.",
            ChatColor.DARK_GRAY + "A simple pirating tool.",
            ChatColor.BLUE + "2+ Attack Damage."));
        hiddenBlade.setItemMeta(hiddenBladeMeta);
        return hiddenBlade;
    }
    
    public static ItemStack getBlackAle(){
        ItemStack blackAle = new Potion(PotionType.INSTANT_DAMAGE).toItemStack(1);
        final PotionMeta blackAleMeta = (PotionMeta) blackAle.getItemMeta();
        blackAleMeta.setDisplayName(ChatColor.WHITE + "Black Ale");
        blackAleMeta.addEnchant(Enchantment.DAMAGE_ALL, 260, true);
        blackAleMeta.addCustomEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(90 * 20, 2), true);
        blackAleMeta.addCustomEffect(PotionEffectType.CONFUSION.createEffect(5 * 20, 1), true);
        blackAleMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Strength II (1:30).",
                ChatColor.GRAY + "Nausea (0:10).",
                ChatColor.DARK_GRAY + "Induces a drunken rage with good and bad effects.",
                ChatColor.BLUE + "+260% Attack Damage."));
        blackAle.setItemMeta(blackAleMeta);
        return blackAle;
    }
    
    public static ItemStack getBoardingAxe() {
        
        ItemStack boardingAxe = new ItemStack(Material.STONE_HOE, 1);
        boardingAxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        boardingAxe.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta boardingAxeMeta = boardingAxe.getItemMeta();
        boardingAxeMeta.setDisplayName(ChatColor.WHITE + "Boarding Axe");
        boardingAxeMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreaking X.",
                ChatColor.DARK_GRAY + "Used when boarding enemy ships.",
                ChatColor.BLUE + "+3 Attack Damage."));
        
        boardingAxe.setItemMeta(boardingAxeMeta);
        
        return boardingAxe;
    }
    
    public static ItemStack getTomaHawk() {
        ItemStack tomaHawk = new ItemStack(Material.STONE_AXE, 1);
        tomaHawk.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
        tomaHawk.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
        
        final ItemMeta tomaHawkMeta = tomaHawk.getItemMeta();
        tomaHawkMeta.setDisplayName(ChatColor.WHITE + "Toma Hawk");
        tomaHawkMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreaking X.",
                ChatColor.DARK_GRAY + "Light axe used for close combat.",
                ChatColor.BLUE + "+4 Attack Damage."));
        tomaHawk.setItemMeta(tomaHawkMeta);
        return tomaHawk;
    }
    
    public static ItemStack getBlindnessPotion(){
        ItemStack blindnessPotion = new Potion(PotionType.getByEffect(PotionEffectType.BLINDNESS)).splash().toItemStack(1);
        blindnessPotion.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        final PotionMeta blindnessPotionMeta = (PotionMeta) blindnessPotion.getItemMeta();
        blindnessPotionMeta.setDisplayName(ChatColor.WHITE + "Blindness potion");
        blindnessPotionMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Instant Blindness.",
            ChatColor.DARK_GRAY + "Effective against the Navy, innefective against village folk."));
        blindnessPotion.setItemMeta(blindnessPotionMeta);
        return blindnessPotion;
    }

    public static ItemStack getInvisibleEmerald() {
        
        final ItemStack invisibleMeca = new ItemStack(Material.EMERALD, 1);
        
        ItemMeta invisibleMecaMeta = (ItemMeta) invisibleMeca.getItemMeta();
        invisibleMecaMeta.setDisplayName(ChatColor.WHITE + "Invisible Emerald");            
        invisibleMecaMeta.setLore(Arrays.asList(ChatColor.GRAY + "10 - 20 seconds of invisivility"));          
        invisibleMeca.setItemMeta(invisibleMecaMeta);
        
        return invisibleMeca;
    }
    
    public static ItemStack getLeaveCompass() {
        
        final ItemStack compass = new ItemStack(Material.COMPASS, 1);
        
        final ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.WHITE + "Leave Game");            
        compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to Use"));            
        compass.setItemMeta(compassMeta);
        
        return compass;
    }

    public static ItemStack getSmokeBomb() {
        
        ItemStack smokeBomb = new Potion(PotionType.SLOWNESS).splash().toItemStack(1);
        PotionMeta smokeBombMeta = (PotionMeta) smokeBomb.getItemMeta();
        smokeBombMeta.setDisplayName(ChatColor.DARK_RED + "Smoke Bomb");
        smokeBombMeta.addCustomEffect(PotionEffectType.BLINDNESS.createEffect(640, 100), true);
        smokeBombMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Blindness (0:08)",
                ChatColor.DARK_GRAY + "Restricts the view of the unfortunate targets."));
        
        smokeBomb.setItemMeta(smokeBombMeta);

        return smokeBomb;
    }
    
    public static ItemStack getShopIcon() {
        
        ItemStack shop = new SpawnEgg(EntityType.CREEPER).toItemStack();
        ItemMeta shopMeta = shop.getItemMeta();
        shopMeta.setDisplayName(ChatColor.DARK_PURPLE + "Assassin Shop");
        shopMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Get experience and Improve your Character",
                ChatColor.DARK_GRAY + "buy here new featetures and weapons"));
        
        shop.setItemMeta(shopMeta);

        return shop;
    }
    
    public static ItemStack getPlaceFinder() {
        
        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(String.format("%s%s Place Finder", ChatColor.RESET, ChatColor.GREEN));
        
        List<String> colored = new ArrayList<>();
        colored.add(ChatColor.GRAY + "Click to find nearest target alive");
        meta.setLore(colored);
        
        compass.setItemMeta(meta);
        return compass;

    }
    
    public static ItemStack[] getAssassinShopInventory(ACPlayer assassin){
        ItemStack[] arrayItems = new ItemStack[9];
        arrayItems[0] = getImproveEmerald(assassin);
        return arrayItems;
    }
    
    public static ItemStack getStatsBook(ACPlayer player) {
        
        final ItemStack stats = new ItemStack(Material.WRITTEN_BOOK, 1);
        
        final BookMeta statsMeta = (BookMeta) stats.getItemMeta();
        statsMeta.setTitle(ChatColor.WHITE + "Stats book");
        
        String timePlayed = new DecimalFormat("0.000").format((double)player.getPlayerModel().getTimePlayed() / (double)86400);
        
        statsMeta.setPages(
                String.format("%s%s%s STATS! \n\n\n%s %sWins: %s%s\n %sLooses: %s%s\n %sLast seen: %s%s\n %sTime played: %s days.",
                ChatColor.BOLD, ChatColor.RED, player.getBukkitPlayer().getName().toUpperCase(), ChatColor.DARK_GRAY,
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getWins(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getLosses(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, player.getPlayerModel().getLastSeen(),
                ChatColor.BOLD, ChatColor.DARK_GRAY, timePlayed));
        stats.setItemMeta(statsMeta);
        
        return stats;
    } 
    
    public static ItemStack getInstructionsBook(){
        
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);        
        
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle(ChatColor.WHITE + "Instructions book");
        bookMeta.setPages(
            String.format("%s%sWELCOME TO Assassin's Creed IV: Black Flag! \n\n\n%s" +
                "One person plays as the assassin, his aim is to eliminate the 5 NPCs in the map, " +
                "each one is found in one of the 5 buildings...", ChatColor.BOLD, ChatColor.RED, ChatColor.DARK_GRAY),
            String.format("%s\nThe other players play as the Royal Navy, and must defend them! " +
                "The assassin has 6 minutes to complete his contract or else he fails...", ChatColor.DARK_GRAY),
            String.format("%s\nDotted around the map are stashs of loot that the assassin can collect to buy more " +
                    "powerful upgrades. He also earns money slowly over time and for every kill he commits...", ChatColor.DARK_GRAY),
            String.format("%s\nThe assassin has 3 lives, the Navy have unlimited, but stay dead for a long time. " +
                "Only 1 NPC has to be alive at the end for the assassin to fail...", ChatColor.DARK_GRAY),
            String.format("%s\nThe assassin may use /topshop or /lowershop commands to travel to shops buildings.", ChatColor.DARK_GRAY));
        
        book.setItemMeta(bookMeta);
        
        return book;
    }
    
    public static ItemStack[] getZombieArmor(Color color) { 

        final ItemStack[] armor = new ItemStack[4];
        
        // A steve head
        final ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);    
        armor[3] = head;
        
        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        final LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(color);
        chestplate.setItemMeta(chestplateMeta);
        
        armor[2] = chestplate;
        
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        final LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(color);
        leggings.setItemMeta(leggingsMeta);
        
        armor[1] = leggings;
        
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        final LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta(); 
        bootsMeta.setColor(color);
        boots.setItemMeta(bootsMeta);
        
        armor[0] = boots;
        
        return armor;
    }
    
    public enum ShopItemEnum {
        IMPROVE_EMERALD(8, getImproveEmerald(null), 20);
        
        private ShopItemEnum(int value, ItemStack item, int duration){
            this.cost = value;
            this.itemStack = item;
            this.duration = duration;
        }
        
        private int cost;
        private int duration;
        private ItemStack itemStack;
        
        public ItemStack getItemStack(){
            return this.itemStack;
        }
        
        public int getDuration(){
            return this.duration;
        }
        
        public int getCost(){
            return this.cost;
        }
    }
}
