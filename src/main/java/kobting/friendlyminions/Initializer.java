package kobting.friendlyminions;

import basemod.BaseMod;
import basemod.interfaces.*;
import kobting.friendlyminions.cards.MonsterCard;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;

@SpireInitializer
public class Initializer implements
        EditCardsSubscriber, PostBattleSubscriber,
        EditKeywordsSubscriber {

    //Used by @SpireInitializer
    public static void initialize(){
        Initializer initializer = new Initializer();
    }

    public Initializer() {
        BaseMod.subscribe(this);
    }


    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new MonsterCard());
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        BaseMod.logger.info("End of battle: Clearing players minions.");
        if(!(AbstractDungeon.player instanceof AbstractPlayerWithMinions)) {
            BasePlayerMinionHelper.clearMinions(AbstractDungeon.player);
        } else {
            ((AbstractPlayerWithMinions)AbstractDungeon.player).clearMinions();
        }
    }

    @Override
    public void receiveEditKeywords() {

        String[] minionKeyword = new String[] {"minion", "minions"};
        BaseMod.addKeyword(minionKeyword, "A friendly monster that fights for you and has a chance to receive #yVulnerable, #yWeak, #yFrail, or #yStrength loss instead of you.");

    }

}
