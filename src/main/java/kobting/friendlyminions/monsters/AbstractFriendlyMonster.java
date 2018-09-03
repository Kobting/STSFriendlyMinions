package kobting.friendlyminions.monsters;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import kobting.friendlyminions.actions.ChooseAction;
import kobting.friendlyminions.actions.ChooseActionInfo;
import kobting.friendlyminions.cards.MonsterCard;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.TintEffect;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;

import java.util.ArrayList;

public abstract class AbstractFriendlyMonster extends AbstractMonster {

    @Deprecated
    private ArrayList<ChooseActionInfo> monsterMoves;

    private AbstractCard monsterCard;
    protected MinionMoveGroup moves;

    public AbstractFriendlyMonster(String name, String id, int maxHealth,float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY){
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.tint = new TintEffect();
        moves = new MinionMoveGroup(this.drawX, this.drawY);
        addToMoves();
    }

    @Deprecated
    //Recommend extending this and making your own constructor and use a lookup table for the hitbox values
    public AbstractFriendlyMonster(String name, String id, int maxHealth, ArrayList<ChooseActionInfo> monsterMoves, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.monsterMoves = monsterMoves;
        monsterCard = new MonsterCard();
        this.tint = new TintEffect();
    }

    @Deprecated
    public AbstractFriendlyMonster(String name, String id, int maxHealth, ArrayList<ChooseActionInfo> monsterMoves, AbstractCard monsterCard, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.monsterMoves = monsterMoves;
        this.monsterCard = monsterCard;
        this.tint = new TintEffect();

    }

    //Make abstract in the future
    public void addToMoves(){}

    @Override
    @Deprecated
    /*
        This only needs to be overriden if using old monster moves.
     */
    public void takeTurn() {

        //Keep support for older monsters for now.

        if(moves.getMoves().size() <= 0) {
            ChooseAction action = new ChooseAction(monsterCard,AbstractDungeon.getMonsters().getRandomMonster(), "Choose your minions move.");
            monsterMoves.forEach(move -> action.add(move.getName(), move.getDescription(), move.getAction()));
            AbstractDungeon.actionManager.addToBottom(action);
        } else {
            moves.getMoves().forEach(move -> {
                if(move.isSelected()) {
                    move.doMove();
                }
            });
        }

    }

    @Deprecated
    public void setMonsterMoves(ArrayList<ChooseActionInfo> monsterMoves) {
        this.monsterMoves = monsterMoves;
    }

    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
    }

    @Override
    public void die() {
        if(!(AbstractDungeon.player instanceof AbstractPlayerWithMinions)){
            BasePlayerMinionHelper.removeMinion(AbstractDungeon.player, this);
        } else {
            this.isDead = true;
            ((AbstractPlayerWithMinions)AbstractDungeon.player).removeMinion(this);
        }
        super.die(false);
    }

    @Override
    public void update() {
        super.update();
        moves.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        moves.render(sb);
    }

}
