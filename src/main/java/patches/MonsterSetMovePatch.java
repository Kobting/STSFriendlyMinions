package patches;

import characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import enums.MonsterIntentEnum;
import helpers.BasePlayerMinionHelper;
import helpers.MinionConfigHelper;

import java.lang.reflect.Field;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
        method = "setMove"
)
public class MonsterSetMovePatch {

    public static void Postfix(AbstractMonster monster, String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {

        if(BasePlayerMinionHelper.hasMinions(AbstractDungeon.player) ||
                (AbstractDungeon.player instanceof AbstractPlayerWithMinions && ((AbstractPlayerWithMinions)AbstractDungeon.player).hasMinions())){
            switch (intent) {
                case ATTACK:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MONSTER, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_BUFF:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MONSTER_BUFF, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_DEBUFF:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MONSTER_DEBUFF, nextMove, baseDamage, multiplier, isMultiDamage);
                    break;
                case ATTACK_DEFEND:
                    maybeChangeIntent(monster, MonsterIntentEnum.ATTACK_MONSTER_DEFEND, nextMove, baseDamage, multiplier, isMultiDamage);
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


            }

            System.out.println("-------- End Change Intent -------------");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private static void switchIntentType(AbstractMonster.Intent originalIntent) {

    }

}
