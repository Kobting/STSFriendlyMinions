package kobting.friendlyminions.characters;

import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.G3DJAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;
import java.util.Objects;

public abstract class AbstractPlayerWithMinions extends CustomPlayer{

    public MonsterGroup minions;
    private AbstractFriendlyMonster[] p_minions;
    private int maxMinions;
    private int baseMinions;

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
        this.baseMinions = this.getInfo().maxMinions;
        this.maxMinions = this.baseMinions;
        clearMinions();
    }


    @Override
    public void preBattlePrep() {
        super.preBattlePrep();
        this.maxMinions = this.baseMinions;
        clearMinions();
    }

    @Override
    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
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
            AbstractDungeon.actionManager.addToBottom(new DamageAction(MonsterHelper.getTarget((AbstractMonster) info.owner), info, AbstractGameAction.AttackEffect.NONE));
            //damageFriendlyMonster(info);
        }
        else if(attackingMonster && minions.monsters.size() <= 0) {
            MonsterHelper.switchTarget((AbstractMonster) info.owner, null);
            info.applyPowers(info.owner, this);
            super.damage(info);
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
        this.minions.monsters.forEach(minion -> minion.loseBlock());
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

    public boolean hasMinion(String minionID) {
        for(AbstractMonster m : minions.monsters) {
            if(((AbstractFriendlyMonster)m).id.equals(minionID)) {
                return true;
            }
        }
        return false;
    }

    public AbstractFriendlyMonster getMinion(String minionID) {
        return (AbstractFriendlyMonster) minions.getMonster(minionID);
    }

    public boolean addMinion(AbstractFriendlyMonster minion){
        if(minions.monsters.size() == maxMinions) {
            return false;
        } else {
            minion.init();
            minion.usePreBattleAction();
            //minion.useUniversalPreBattleAction(); //This might be causing blights to effect minions
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

        if(intent == MonsterIntentEnum.ATTACK_MINION
                || intent == MonsterIntentEnum.ATTACK_MINION_BUFF
                || intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
                || intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {

            return true;
        }

        return false;

    }


    public boolean hasMinions() {
        return minions.monsters.size() > 0;
    }

    public MonsterGroup getMinions(){
        return minions;
    }

}
