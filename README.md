# STSFriendlyMinions
Library for adding Custom Characters that can spawn Friendly Minions

Requires BaseMod

(Not finished. Readme will be updated when Library is finished)

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
            CharacterEnum.Example,
            getStartingRelics(),
            getStartingDeck(),
            false);

    return info;
}
```

### Example FriendlyMinion
```java
public class TestingCompanion extends AbstractFriendlyMonster {

    public static String NAME = "TestingCompanion";
    public static String ID = "TestingCompanion";
    private ArrayList<ChooseActionInfo> moveInfo;
    private boolean hasAttacked = false;

    public TestingCompanion() {
        super(NAME, ID, 20, null, -8.0F, 10.0F, 230.0F, 240.0F, "images/monsters/monster_testing.png", -700.0F, 0);
        this.setMonsterMoves(makeMoves());
        moveInfo = makeMoves();
    }

    @Override
    public void takeTurn() {
        if(!hasAttacked){
            ChooseAction pickAction = new ChooseAction(new MonsterCard(), AbstractDungeon.getRandomMonster(), "Choose your attack");
            this.moveInfo.forEach( move -> {
                pickAction.add(move.getName(), move.getDescription(), move.getAction());
            });
            AbstractDungeon.actionManager.addToBottom(pickAction);
        }
    }

    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
        this.hasAttacked = false;
    }

    //Create possible moves for the monster
    private ArrayList<ChooseActionInfo> makeMoves(){
        ArrayList<ChooseActionInfo> tempInfo = new ArrayList<>();

        tempInfo.add(new ChooseActionInfo("Attack", "Deal 5 damage.", () -> {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.getRandomMonster(),
                    new DamageInfo(this, 5, DamageInfo.DamageType.NORMAL)));
        }));

        tempInfo.add(new ChooseActionInfo("Debuff", "Apply 1 weaken.", () -> {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.getRandomMonster(),this,
                    new WeakPower(this, 1, false)));
        }));

        return tempInfo;
    }


    //Not needed unless doing some kind of random move like normal Monsters
    @Override
    protected void getMove(int i) {

    }
}
```

### Example use method for a Card to summon a minion
```java
public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    if(abstractPlayer instanceof AbstractPlayerWithMinions) {
        AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) abstractPlayer;
        player.addMinion(companion);
    }
}
```
