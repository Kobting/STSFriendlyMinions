package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import monsters.AbstractFriendlyMonster;

import java.util.Objects;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
        method = SpirePatch.CLASS
)
public class PlayerAddFieldsPatch {

    private static Integer maxMinions = 1;
    private static AbstractFriendlyMonster[] p_minions = new AbstractFriendlyMonster[maxMinions];
    private static MonsterGroup minions = new MonsterGroup(p_minions);


    public static SpireField<Integer> f_maxMinions = new SpireField<>(maxMinions);
    public static SpireField<MonsterGroup> f_minions = new SpireField<>(minions);

}
