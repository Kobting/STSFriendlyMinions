package kobting.friendlyminions.helpers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

public class BaseSummonHelper {

    public static void summonMinion(AbstractFriendlyMonster monster) {
        AbstractPlayerWithMinions player;
        if(AbstractDungeon.player instanceof AbstractPlayerWithMinions) {
            player = (AbstractPlayerWithMinions)AbstractDungeon.player;
            player.addMinion(monster);
        }
        else {
            BasePlayerMinionHelper.addMinion(AbstractDungeon.player, monster);
        }
    }

}
