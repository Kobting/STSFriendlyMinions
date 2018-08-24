package kobting.friendlyminions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
        method = SpirePatch.CLASS
)
public class MonsterAddFieldsPatch {

    public static SpireField<AbstractFriendlyMonster> f_target = new SpireField<>(()-> null);

}
