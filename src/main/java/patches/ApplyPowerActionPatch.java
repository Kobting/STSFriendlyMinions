package patches;

import characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import helpers.BasePlayerMinionHelper;
import monsters.AbstractFriendlyMonster;

import java.lang.reflect.Field;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.actions.common.ApplyPowerAction",
        method = SpirePatch.CONSTRUCTOR,
        paramtypes = {"com.megacrit.cardcrawl.core.AbstractCreature",
                      "com.megacrit.cardcrawl.core.AbstractCreature",
                      "com.megacrit.cardcrawl.powers.AbstractPower",
                      "int", "boolean", "com.megacrit.cardcrawl.actions.AbstractGameAction$AttackEffect"}
)
//TODO: This might need to be changed. Could allow for monster to attack and debuff two different minions.
public class ApplyPowerActionPatch {


    public static void Prefix(ApplyPowerAction applyPowerAction, AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {
        if((target instanceof AbstractPlayerWithMinions || BasePlayerMinionHelper.hasMinions(AbstractDungeon.player)) && source instanceof AbstractMonster) {
            if(switchTarget()) {
                try {
                    Field doneField = AbstractGameAction.class.getField("isDone");
                    doneField.setAccessible(true);
                    doneField.setBoolean(applyPowerAction, true);

                    if(!(AbstractDungeon.player instanceof AbstractPlayerWithMinions)){
                        if(BasePlayerMinionHelper.hasMinions(AbstractDungeon.player)){
                            MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);
                            System.out.println("------Power Switching Target------");
                            int randomMinion = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
                            AbstractMonster newTarget = minions.monsters.get(randomMinion);
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(newTarget, source, powerToApply, stackAmount, isFast, effect));
                        }
                    } else {
                        AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) AbstractDungeon.player;
                        MonsterGroup minions = player.getMinions();
                        if (minions.monsters.size() > 0) {
                            System.out.println("------Power Switching Target------");
                            int randomMinion = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
                            AbstractMonster newTarget = player.minions.monsters.get(randomMinion);
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(newTarget, source, powerToApply, stackAmount, isFast, effect));
                        }
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean switchTarget(){

        int randomChoice = AbstractDungeon.aiRng.random(0,3);

        return !(randomChoice > 1);

    }

}
