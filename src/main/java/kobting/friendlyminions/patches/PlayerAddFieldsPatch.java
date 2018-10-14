package kobting.friendlyminions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
        method = SpirePatch.CLASS
)
public class PlayerAddFieldsPatch {

    private static Integer maxMinions = 1;
    private static AbstractFriendlyMonster[] p_minions = new AbstractFriendlyMonster[maxMinions];
    private static MonsterGroup minions = new MonsterGroup(p_minions);

    public static SpireField<Integer> f_baseMinions = new SpireField<>(() ->maxMinions);
    public static SpireField<Integer> f_maxMinions = new SpireField<>(() ->maxMinions);
    public static SpireField<MonsterGroup> f_minions = new SpireField<>(() ->minions);

}
