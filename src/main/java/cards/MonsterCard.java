package cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MonsterCard extends CustomCard {

    public static final String ID = "MonsterCard";
    public static final String NAME = "Monster Move";

    public MonsterCard() {
        super(ID, NAME, "cards/monster_card.png", -2, "NONE", CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF_AND_ENEMY);

    }

    //Can't be upgraded
    @Override
    public void upgrade() {

    }

    @Override
    public AbstractCard makeCopy() {
        return new MonsterCard();
    }

    //When selected a custom Action will happen. This isn't needed.
    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }
}
