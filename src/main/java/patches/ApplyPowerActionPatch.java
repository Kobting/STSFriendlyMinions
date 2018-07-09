package patches;

import characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.actions.common.ApplyPowerAction",
        method = SpirePatch.CONSTRUCTOR
)
//TODO: This might need to be changed. Could allow for monster to attack and debuff two different minions.
public class ApplyPowerActionPatch {

    public static void Prefix(ApplyPowerAction applyPowerAction, AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {
        if(target instanceof AbstractPlayerWithMinions && source instanceof AbstractMonster) {
            if(switchTarget()) {
                AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) AbstractDungeon.player;
                MonsterGroup minions = player.getMinions();

                if(minions.monsters.size() > 0) {
                    int randomMinion = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
                    target = minions.monsters.get(randomMinion);
                }
            }
        }
    }

    private static boolean switchTarget(){

        int randomChoice = AbstractDungeon.aiRng.random(0,3);

        return !(randomChoice > 1);

    }

}
