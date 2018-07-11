package characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import enums.MonsterIntentEnum;
import monsters.AbstractFriendlyMonster;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPlayerWithMinions extends AbstractPlayer {

    public MonsterGroup minions;
    private AbstractFriendlyMonster[] p_minions;
    int maxMinions;

    public AbstractPlayerWithMinions(String name, PlayerClass setClass) {
        super(name, setClass);
    }

    @Override
    protected void initializeClass(String imgUrl, String shoulder2ImgUrl, String shouldImgUrl, String corpseImgUrl, CharSelectInfo info, float hb_x, float hb_y, float hb_w, float hb_h, EnergyManager energy) {
        super.initializeClass(imgUrl, shoulder2ImgUrl, shouldImgUrl, corpseImgUrl, info, hb_x, hb_y, hb_w, hb_h, energy);
        this.maxMinions = ((CustomCharSelectInfo)info).maxMinions;
        p_minions = new AbstractFriendlyMonster[this.maxMinions];
        minions = new MonsterGroup(p_minions);
        minions.monsters.removeIf(Objects::isNull);
    }

    @Override
    public void damage(DamageInfo info) {

        AbstractMonster owner;
        boolean attackingMonster = false;
        if(info.owner instanceof AbstractMonster) {
            owner = (AbstractMonster) info.owner;
            attackingMonster = checkAttackMonsterIntent(owner.intent);
        }

        if(attackingMonster && minions.monsters.size() > 0) {
            damageFriendlyMonster(info);
        }
        else {
            super.damage(info);
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(AbstractDungeon.getCurrRoom() != null){
            switch (AbstractDungeon.getCurrRoom().phase) {
                case COMBAT:
                    minions.render(sb);
                    break;
            }
        }

    }



    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();

        this.minions.monsters.forEach(minion -> minion.takeTurn());
    }

    public boolean addMinion(AbstractFriendlyMonster minion){
        if(minions.monsters.size() == maxMinions) {
            return false;
        } else {
            minion.init();
            minion.usePreBattleAction();
            minion.useUniversalPreBattleAction();
            minion.showHealthBar();
            minions.add(minion);
            return true;
        }
    }

    public boolean removeMinion(AbstractFriendlyMonster minion) {
        return minions.monsters.remove(minion);
    }

    public void clearMinions(){
        minions.monsters.clear();
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

    /*  This causes a delay when attacking the monster but I can't find another way around it other
     *  than patching every monsters attacks. Which isn't realistic.
     *
     *  This is needed because if the player is blocking or intangible or has any effects applied it
     *  would count towards how the minions are damaged.
     */
    private void damageFriendlyMonster(DamageInfo info){
        info.output = info.base;
        int randomMinionIndex = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(minions.monsters.get(randomMinionIndex), info, AbstractGameAction.AttackEffect.NONE));
    }

    public boolean hasMinions() {
        return minions.monsters.size() > 0;
    }

    public MonsterGroup getMinions(){
        return minions;
    }

}
