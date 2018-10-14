package kobting.friendlyminions.characters;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import java.util.ArrayList;

//Use this CharSelectInfo if you are adding minions to your character
public class CustomCharSelectInfo extends CharSelectInfo {

    public int maxMinions;

    public CustomCharSelectInfo(String name, String flavorText, int currentHp, int maxHp, int maxOrbs, int maxMinions, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, boolean resumeGame) {
        super(name, flavorText, currentHp, maxHp, maxOrbs, gold, cardDraw, player, relics, deck, resumeGame);
        this.maxMinions = maxMinions;
    }

    public CustomCharSelectInfo(String name, String fText, int currentHp, int maxHp, int maxOrbs, int maxMinions, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, long saveDate, int floorNum, String levelName, boolean isHardMode) {
        super(name, fText, currentHp, maxHp, maxOrbs, gold, cardDraw, player, relics, deck, saveDate, floorNum, levelName, isHardMode);
        this.maxMinions = maxMinions;
    }

}
