package enums;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MonsterIntentEnum {

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MONSTER;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MONSTER_BUFF;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MONSTER_DEBUFF;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MONSTER_DEFEND;

    @SpireEnum
    public static AbstractMonster.Intent DEBUFF_MONSTER;

    @SpireEnum
    public static AbstractMonster.Intent STRONG_DEBUFF_MONSTER;

    @SpireEnum
    public static AbstractMonster.Intent DEFEND_DEBUFF_MONSTER;

}
