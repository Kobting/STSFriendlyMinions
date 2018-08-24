package kobting.friendlyminions.patches;

import kobting.friendlyminions.cards.CustomSummonCard;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.cards.AbstractCard",
        method = "hasEnoughEnergy"
)
public class CardPatch {

    public static SpireReturn<Boolean> Prefix(AbstractCard _instance) {

        AbstractPlayer player = AbstractDungeon.player;
        if(_instance instanceof CustomSummonCard) {
            if (player instanceof AbstractPlayerWithMinions) {
                if (((AbstractPlayerWithMinions) player).minions.monsters.size() >= ((AbstractPlayerWithMinions) player).getMaxMinions()) {
                    _instance.cantUseMessage = "Max minions already summoned.";
                    return SpireReturn.Return(false);
                } else {
                    return SpireReturn.Continue();
                }
            } else {
                if (BasePlayerMinionHelper.getMinions(player).monsters.size() >= BasePlayerMinionHelper.getMaxMinions(player)) {
                    _instance.cantUseMessage = "Max minions already summoned.";
                    return SpireReturn.Return(false);
                } else {
                    return SpireReturn.Continue();
                }
            }
        } else {
            return SpireReturn.Continue();
        }
    }

}
