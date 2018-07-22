package characters;

import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.G3DJAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPlayerWithMinions extends CustomPlayer {

    public MonsterGroup minions;
    private AbstractFriendlyMonster[] p_minions;
    int maxMinions;

    public AbstractPlayerWithMinions(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, String model, String animation) {
        this(name, playerClass, orbTextures, orbVfxPath, (float[])null, model, animation);
    }

    public AbstractPlayerWithMinions(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, float[] layerSpeeds, String model, String animation) {
        this(name, playerClass, orbTextures, orbVfxPath, (float[])layerSpeeds, (AbstractAnimation)(new G3DJAnimation(model, animation)));
    }

    public AbstractPlayerWithMinions(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, AbstractAnimation animation) {
        this(name, playerClass, orbTextures, orbVfxPath, (float[])null, (AbstractAnimation)animation);
    }

    public AbstractPlayerWithMinions(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, float[] layerSpeeds, AbstractAnimation animation) {
        super(name, playerClass, orbTextures, orbVfxPath, layerSpeeds, animation);
        this.maxMinions = this.getInfo().maxMinions;
        clearMinions();
    }


    @Override
    public void preBattlePrep() {
        super.preBattlePrep();
        clearMinions();
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

    public abstract CustomCharSelectInfo getInfo();

    public int getMaxMinions(){
        return this.maxMinions;
    }


    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
        this.minions.monsters.forEach(minion -> minion.takeTurn());
        this.minions.monsters.forEach(minion -> minion.applyEndOfTurnTriggers());
        this.minions.monsters.forEach(minion -> minion.powers.forEach(power -> power.atEndOfRound()));
    }

    @Override
    public void applyStartOfTurnPostDrawPowers() {
        super.applyStartOfTurnPostDrawPowers();
        this.minions.monsters.forEach(minion -> minion.applyStartOfTurnPostDrawPowers());
    }

    @Override
    public void applyStartOfTurnPowers() {
        super.applyStartOfTurnPowers();
        this.minions.monsters.forEach(minion -> minion.applyStartOfTurnPowers());
    }

    @Override
    public void applyTurnPowers() {
        super.applyTurnPowers();
        this.minions.monsters.forEach(minion -> minion.applyTurnPowers());
    }

    @Override
    public void updatePowers() {
        super.updatePowers();
        this.minions.monsters.forEach(minion -> minion.updatePowers());
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
    public void update() {
        super.update();
        if(AbstractDungeon.getCurrRoom() != null) {
            switch (AbstractDungeon.getCurrRoom().phase) {
                case COMBAT:
                    minions.update();
                    break;
            }
        }
    }

    public void changeMaxMinionAmount(int newAmount) {
        this.maxMinions = newAmount;
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
        p_minions = new AbstractFriendlyMonster[this.maxMinions];
        minions = new MonsterGroup(p_minions);
        minions.monsters.removeIf(Objects::isNull);
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
        int randomMinionIndex = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
        AbstractFriendlyMonster minion = (AbstractFriendlyMonster) minions.monsters.get(randomMinionIndex);
        info.applyPowers(info.owner, minion);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(minions.monsters.get(randomMinionIndex), info, AbstractGameAction.AttackEffect.NONE));
    }

    public boolean hasMinions() {
        return minions.monsters.size() > 0;
    }

    public MonsterGroup getMinions(){
        return minions;
    }

}
