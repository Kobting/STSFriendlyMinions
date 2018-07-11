import basemod.BaseMod;
import basemod.interfaces.*;
import cards.MonsterCard;
import characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpireInitializer
public class Initializer implements EditCardsSubscriber, PostBattleSubscriber {

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
        if(AbstractDungeon.player instanceof AbstractPlayerWithMinions) {
            ((AbstractPlayerWithMinions)AbstractDungeon.player).clearMinions();
        }
    }

}
