package patches;

import basemod.BaseMod;
import characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import enums.MonsterIntentEnum;
import helpers.BasePlayerMinionHelper;
import monsters.AbstractFriendlyMonster;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/*
 * All of these are checking against AbstractPlayerWithMinions to avoid double calling of those if
 * someone is using AbstractPlayerWithMinions as their base start of a character.
 */
public class PlayerMethodPatches {


    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "initializeClass"
    )
    public static class InitializePatch{

        public static void Prefix(AbstractPlayer _instance) {
            BasePlayerMinionHelper.clearMinions(_instance);
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "damage",
            paramtypes = {"com.megacrit.cardcrawl.cards.DamageInfo"}
    )
    public static class DamagePatch{

        public static void Prefix(AbstractPlayer _instance, DamageInfo info) {
            if(!(_instance instanceof AbstractPlayerWithMinions)) {
                AbstractMonster owner;
                boolean attackingMonster = false;
                if (info.owner instanceof AbstractMonster) {
                    owner = (AbstractMonster) info.owner;
                    attackingMonster = checkAttackMonsterIntent(owner.intent);
                }
                if (attackingMonster) {
                    damageFriendlyMonster(info);
                    return;
                }
            }

        }

        private static boolean checkAttackMonsterIntent(AbstractMonster.Intent intent) {

            if(intent == MonsterIntentEnum.ATTACK_MONSTER || intent == MonsterIntentEnum.ATTACK_MONSTER_BUFF
                    || intent == MonsterIntentEnum.ATTACK_MONSTER_DEBUFF || intent == MonsterIntentEnum.ATTACK_MONSTER_DEFEND
                    || intent == MonsterIntentEnum.DEBUFF_MONSTER || intent == MonsterIntentEnum.STRONG_DEBUFF_MONSTER
                    || intent == MonsterIntentEnum.DEFEND_DEBUFF_MONSTER) {

                return true;
            }

            return false;

        }

        private static void damageFriendlyMonster(DamageInfo info){

            MonsterGroup minions;
            minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);

            int randomMinionIndex = AbstractDungeon.aiRng.random(minions.monsters.size() - 1);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters.get(randomMinionIndex), info, AbstractGameAction.AttackEffect.NONE));
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "render",
            paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"}
    )
    public static class RenderPatch{

        public static void Prefix(AbstractPlayer _instance, SpriteBatch sb) {

            if(!(_instance instanceof AbstractPlayerWithMinions)) {
                MonsterGroup minions;
                minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);

                if(AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
                    if (AbstractDungeon.getCurrRoom() != null) {
                        switch (AbstractDungeon.getCurrRoom().phase) {
                            case COMBAT:
                                if(BasePlayerMinionHelper.hasMinions(AbstractDungeon.player))
                                    minions.render(sb);
                                break;
                        }
                    }
                }
            }
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "update"
    )
    public static class UpdatePatch{

        public static void Postfix(AbstractPlayer _instance) {

            if(!(_instance instanceof AbstractPlayerWithMinions)) {
                MonsterGroup minions;
                minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);


                if (AbstractDungeon.getCurrRoom() != null) {
                    if(AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
                        switch (AbstractDungeon.getCurrRoom().phase) {
                            case COMBAT:
                                if(BasePlayerMinionHelper.hasMinions(AbstractDungeon.player))
                                    minions.update();
                                break;
                        }
                    }
                }
            }
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "preBattlePrep"
    )
    public static class PreBattlePatch{

        public static void Postfix(AbstractPlayer _instance) {

            if(!(_instance instanceof AbstractPlayerWithMinions)) {
                BasePlayerMinionHelper.clearMinions(_instance);
            }
        }

    }

   @SpirePatch(
            cls = "com.megacrit.cardcrawl.core.AbstractCreature",
            method = "applyEndOfTurnTriggers"
    )
    public static class EndOfTurnPatch{

        public static void Postfix(AbstractCreature _instance) {
            if((_instance instanceof AbstractPlayer) && !(_instance instanceof AbstractPlayerWithMinions)) {
                BaseMod.logger.info("----------- Minion Before Attacking --------------");
                PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters.forEach(monster -> monster.takeTurn());
            }
        }

    }



}
