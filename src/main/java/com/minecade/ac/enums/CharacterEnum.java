package com.minecade.ac.enums;

import java.util.ArrayList;
import java.util.List;

public enum CharacterEnum {
    
    ASSASSIN("Assassin"),
    
    BODYGUARD("BodyGuard"),
    
    MUSKETEER("Musketeer"),
    
    SWORDSMAN("SwordMan");
    
    private String name;
    
    private CharacterEnum(String name){
        this.name = name;
    }
    
    public static List<CharacterEnum> navyValues(){
        List<CharacterEnum> navyValues = new ArrayList<>();
        for (CharacterEnum type : values()){
            if(!type.equals(CharacterEnum.ASSASSIN)){
                navyValues.add(type);
            }
        }
        return navyValues;
    }
    
    public static boolean isSameTeam(CharacterEnum compareValue, CharacterEnum compareBase){
        List<CharacterEnum> navyValues = navyValues();
        if(compareValue != null  && compareBase != null){
            if(navyValues.contains(compareBase) && navyValues.contains(compareValue)){
                return true;
            } else {
                if(compareBase.equals(compareValue)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
