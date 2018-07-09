import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PreMonsterTurnSubscriber;
import cards.MonsterCard;
import characters.AbstractPlayerWithMinions;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpireInitializer
public class Initializer implements PreMonsterTurnSubscriber, EditCardsSubscriber, PostBattleSubscriber {

    //Used by @SpireInitializer
    public static void initialize(){
        Initializer initializer = new Initializer();
    }

    public Initializer() {
        BaseMod.subscribe(this);
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        //Let minions take their turn
        if(AbstractDungeon.player instanceof AbstractPlayerWithMinions){
            AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) AbstractDungeon.player;
            player.getMinions().forEach(minion -> minion.takeTurn());
        }
        return true;
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
