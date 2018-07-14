package monsters;

import actions.ChooseAction;
import actions.ChooseActionInfo;
import cards.MonsterCard;
import characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.TintEffect;

import java.util.ArrayList;

public abstract class AbstractFriendlyMonster extends AbstractMonster {

    private ArrayList<ChooseActionInfo> monsterMoves;
    private AbstractCard monsterCard;
    protected boolean hasAttacked = false;

    //Recommend extending this and making your own constructor and use a lookup table for the hitbox values
    public AbstractFriendlyMonster(String name, String id, int maxHealth, ArrayList<ChooseActionInfo> monsterMoves, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.monsterMoves = monsterMoves;
        monsterCard = new MonsterCard();
        this.tint = new TintEffect();
    }

    public AbstractFriendlyMonster(String name, String id, int maxHealth, ArrayList<ChooseActionInfo> monsterMoves, AbstractCard monsterCard, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.monsterMoves = monsterMoves;
        this.monsterCard = monsterCard;
        this.tint = new TintEffect();
    }

    @Override
    public void takeTurn() {
        int numMonsters = AbstractDungeon.getMonsters().monsters.size();
        int randomMonsterIndex = AbstractDungeon.aiRng.random(numMonsters);
        ChooseAction action = new ChooseAction(monsterCard,AbstractDungeon.getMonsters().monsters.get(randomMonsterIndex), "Choose your minions move.");
        monsterMoves.forEach(move -> action.add(move.getName(), move.getDescription(), move.getAction()));
        AbstractDungeon.actionManager.addToBottom(action);
        //hasAttacked = true;
    }

    public void setMonsterMoves(ArrayList<ChooseActionInfo> monsterMoves) {
        this.monsterMoves = monsterMoves;
    }

    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
        //hasAttacked = false;
    }

    @Override
    public void die() {
        ((AbstractPlayerWithMinions)AbstractDungeon.player).removeMinion(this);
        super.die();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
//        sb.setColor(Color.RED);
//        this.healthHb.render(sb);
        //this.hb.render(sb);
    }
}
