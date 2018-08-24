package kobting.friendlyminions.enums;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MonsterIntentEnum {

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MINION;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MINION_BUFF;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MINION_DEBUFF;

    @SpireEnum
    public static AbstractMonster.Intent ATTACK_MINION_DEFEND;

    @SpireEnum
    public static AbstractMonster.Intent DEBUFF_MONSTER;

    @SpireEnum
    public static AbstractMonster.Intent STRONG_DEBUFF_MONSTER;

    @SpireEnum
    public static AbstractMonster.Intent DEFEND_DEBUFF_MONSTER;

}
