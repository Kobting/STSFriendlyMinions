package kobting.friendlyminions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.helpers.MinionConfigHelper;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.lang.reflect.Field;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "setMove",
        paramtypez = {String.class, byte.class, AbstractMonster.Intent.class, int.class, int.class, boolean.class}
)
public class MonsterSetMovePatch {

    @SpirePostfixPatch
    public static void Postfix(AbstractMonster monster, String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {

        if(BasePlayerMinionHelper.hasMinions(AbstractDungeon.player) ||
                (AbstractDungeon.player instanceof AbstractPlayerWithMinions && ((AbstractPlayerWithMinions)AbstractDungeon.player).hasMinions())){
            switch (intent) {
                case ATTACK:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MINION, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_BUFF:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MINION_BUFF, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_DEBUFF:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MINION_DEBUFF, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_DEFEND:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MINION_DEFEND, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
            }
        }
    }


    private static void maybeChangeIntent(AbstractMonster monster, AbstractMonster.Intent possibleNewIntent, byte nextMove, int intentBaseDmg, int multiplier, boolean isMultiDamage) {

        try {
            System.out.println("--------- Maybe Change Intent -----------");

            if(AbstractDungeon.aiRng.randomBoolean(MinionConfigHelper.MinionAttackTargetChance)) {

                System.out.println("-------- Changing Intent -----------");

                Field moveInfo = AbstractMonster.class.getDeclaredField("move");
                moveInfo.setAccessible(true);

                EnemyMoveInfo newInfo = new EnemyMoveInfo(nextMove, possibleNewIntent, intentBaseDmg, multiplier, isMultiDamage);
                moveInfo.set(monster, newInfo);


            } else {
                MonsterHelper.setTarget(monster, null);
            }

            System.out.println("-------- End Change Intent -------------");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
