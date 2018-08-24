package kobting.friendlyminions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/*
 Class by: twanvl
 https://github.com/twanvl/sts-mad-science-mod/blob/master/src/main/java/madsciencemod/actions/common/ChooseAction.java
 Using for deciding minion moves.
 */
public class ChooseAction extends AbstractGameAction {

    AbstractCard baseCard;
    AbstractMonster target;
    CardGroup choices = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    ArrayList<Runnable> actions = new ArrayList<>();
    String message;

    public ChooseAction(AbstractCard baseCard, AbstractMonster target, String message) {
        this.setValues(AbstractDungeon.player, AbstractDungeon.player, 1);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.baseCard = baseCard;
        this.message = message;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    public void add(String name, String description, Runnable action) {
        AbstractCard choice = baseCard.makeStatEquivalentCopy();
        choice.name = name;
        choice.rawDescription = description;
        choice.initializeDescription();
        if (target != null) {
            choice.calculateCardDamage(target);
        } else {
            choice.applyPowers();
        }
        choices.addToTop(choice);
        actions.add(action);
    }

    @Override
    public void update() {
        if (choices.isEmpty()) {
            this.tickDuration();
            this.isDone = true;
            return;
        }
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            if (choices.size() > 1) {
                AbstractDungeon.gridSelectScreen.open(this.choices, 1, message, false, false, false, false);
                this.tickDuration();
                return;
            } else {
                actions.get(0).run();
                this.tickDuration();
                this.isDone = true;
                return;
            }
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard pick = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            int i = choices.group.indexOf(pick);
            actions.get(i).run();
        }
        this.tickDuration();
    }
}
