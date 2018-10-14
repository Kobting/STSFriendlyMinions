# STSFriendlyMinions
Library for adding Custom Characters that can spawn Friendly Minions

Currently in Beta and open for testing. Appreciate any bug reports.

### Requirements

- [BaseMod (4.0.0+)](https://github.com/daviscook477/BaseMod/releases)
- [ModTheSpire (3.0.0+)](https://github.com/kiooeht/ModTheSpire/releases)

### Example GetLoadout
```java
public static CharSelectInfo getLoadout() {

    CharSelectInfo info = new CustomCharSelectInfo(
            "Character Name",
            "Character Flavor",
            80, //currentHP
            80, //maxHP
            0,  //maxOrbs
            2,  //maxMinions
            99, //gold
            5,  //cardDraw
            this,
            getStartingRelics(),
            getStartingDeck(),
            false);

    return info;
}
```

### Example FriendlyMinion
```java
public class TestMinion extends AbstractFriendlyMonster {
    
    private static String NAME = "Testing Minion";
    private static String ID = "TestingMinion";
    private AbstractMonster target;

    public TestMinion(int offsetX, int offsetY) {
        super(NAME, ID, 20, -8.0F, 10.0F, 230.0F, 240.0F, "images/monsters/monster_testing.png", offsetX, offsetY);
        addMoves();
    }

    private void addMoves(){
        this.moves.addMove(new MinionMove("Attack", this, new Texture("images/monsters/atk_bubble.png"), "Deal 5 damage", () -> {
            target = AbstractDungeon.getRandomMonster();
            DamageInfo info = new DamageInfo(this,5,DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, target); // <--- This lets powers effect minions attacks
            AbstractDungeon.actionManager.addToBottom(new DamageAction(target, info));
        }));
        this.moves.addMove(new MinionMove("Defend", this, new Texture("images/monsters/atk_bubble.png"),"Gain 5 block", () -> {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this, 5));
        }));
    }
}
```

### Example use method for a Card to summon a minion with CustomCharacter
```java
public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    if(abstractPlayer instanceof AbstractPlayerWithMinions) {
        AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) abstractPlayer;
        player.addMinion(companion);
    }
}
```

### Example use method for a Card to summon a minion with BaseCharacter
```java
public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    BasePlayerMinionHelper.addMinion(abstractPlayer, companion);
}
```
