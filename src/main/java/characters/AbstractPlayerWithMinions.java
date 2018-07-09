package characters;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import enums.MonsterIntentEnum;
import monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public abstract class AbstractPlayerWithMinions extends AbstractPlayer {

    ArrayList<AbstractFriendlyMonster> minions;
    int maxMinions;

    public AbstractPlayerWithMinions(String name, PlayerClass setClass) {
        super(name, setClass);
    }

    @Override
    protected void initializeClass(String imgUrl, String shoulder2ImgUrl, String shouldImgUrl, String corpseImgUrl, CharSelectInfo info, float hb_x, float hb_y, float hb_w, float hb_h, EnergyManager energy) {
        super.initializeClass(imgUrl, shoulder2ImgUrl, shouldImgUrl, corpseImgUrl, info, hb_x, hb_y, hb_w, hb_h, energy);
        this.maxMinions = ((CustomCharSelectInfo)info).maxMinions;
        minions = new ArrayList<>();
    }

    @Override
    public void damage(DamageInfo info) {

        AbstractMonster owner;
        boolean attackingMonster = false;
        if(info.owner instanceof AbstractMonster) {
            owner = (AbstractMonster) info.owner;
            attackingMonster = checkAttackMonsterIntent(owner.intent);
        }

        if(attackingMonster) {
            damageFriendlyMonster(info);
        } else {
            super.damage(info);
        }

    }



    private boolean checkAttackMonsterIntent(AbstractMonster.Intent intent) {

        if(intent == MonsterIntentEnum.ATTACK_MONSTER || intent == MonsterIntentEnum.ATTACK_MONSTER_BUFF
                || intent == MonsterIntentEnum.ATTACK_MONSTER_DEBUFF || intent == MonsterIntentEnum.ATTACK_MONSTER_DEFEND
                || intent == MonsterIntentEnum.DEBUFF_MONSTER || intent == MonsterIntentEnum.STRONG_DEBUFF_MONSTER
                || intent == MonsterIntentEnum.DEFEND_DEBUFF_MONSTER) {

            return true;
        }

        return false;

    }

    private void damageFriendlyMonster(DamageInfo info){
        int randomMinionIndex = AbstractDungeon.aiRng.random(minions.size());
        minions.get(randomMinionIndex).damage(info);
    }

    public boolean addMinion(AbstractFriendlyMonster minion){
        if(minions.size() == maxMinions) {
            return false;
        } else {
            minion.init();
            minion.usePreBattleAction();
            minion.useUniversalPreBattleAction();
            minion.showHealthBar();
            minion.applyPowers();
            minions.add(minion);
            return true;
        }
    }

    public boolean removeMinion(AbstractFriendlyMonster minion) {
        return minions.remove(minion);
    }

    public void clearMinions(){
        this.minions.clear();
    }

    public boolean hasMinions() {
        return minions.size() > 0;
    }

    public ArrayList<AbstractFriendlyMonster> getMinions(){
        return this.minions;
    }

}
