package com.minecade.ac.enums;

import org.bukkit.ChatColor;

public enum NPCEnum {

    GREEN(ChatColor.GREEN),
    
    GRAY(ChatColor.GRAY),
    
    RED(ChatColor.RED),
    
    YELLOW(ChatColor.YELLOW),
    
    WHITE(ChatColor.WHITE);
    
    private ChatColor color;
    
    /**
     * Get chat color
     * @return
     * @author Kvnamo
     */
    public ChatColor getChatColor(){
        return this.color;
    }
    
    /**
     * NPC enum constructor
     * @param color
     * @author Kvnamo
     */
    private NPCEnum(ChatColor color){
        this.color = color;
    }
    
    public static NPCEnum getNPCEnumByString(String string){
        for(NPCEnum value : values()){
            if(value.toString().equalsIgnoreCase(string)){
                return value;
            }
        }
        return null;
    }
}
