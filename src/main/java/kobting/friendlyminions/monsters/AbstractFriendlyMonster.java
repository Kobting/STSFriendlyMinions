package kobting.friendlyminions.monsters;

import com.megacrit.cardcrawl.core.Settings;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.TintEffect;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;

public abstract class AbstractFriendlyMonster extends AbstractMonster {

    protected MinionMoveGroup moves;
    protected Texture[] attackIntents;
    private boolean takenTurn = false;

    public AbstractFriendlyMonster(String name, String id, int maxHealth,float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY){
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, null, offsetX, offsetY);
        this.img = new Texture(imgUrl);
        this.tint = new TintEffect();
        moves = new MinionMoveGroup(this.drawX - 15.0f * Settings.scale, this.drawY - 15 * Settings.scale);
    }

    public AbstractFriendlyMonster(String name, String id, int maxHealth,float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, Texture[] attackIntents){
        this(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.attackIntents = attackIntents;
    }


    public void addMove(MinionMove move){
        moves.addMove(move);
    }

    public void removeMove(String id){
        moves.removeMove(id);
    }

    public MinionMoveGroup getMoves(){
        return this.moves;
    }

    public void setMoves(MinionMoveGroup moves) {
        this.moves = moves;
    }

    public void clearMoves(){
        this.moves.clearMoves();
    }

    public boolean hasMove(String id) {
        return this.moves.hasMove(id);
    }

    public void setTakenTurn(boolean takenTurn) {
        this.takenTurn = takenTurn;
    }

    public boolean hasTakenTurn() {
        return takenTurn;
    }

    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
        this.takenTurn = false;
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


    public Texture[] getAttackIntents(){
        return this.attackIntents;
    }

    @Override
    public void update() {
        super.update();
        if(!this.takenTurn){
            moves.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(!this.takenTurn){
            moves.render(sb);
        }

    }

    //Overriding these to make them not show up when extended as they aren't used by minions
    @Override
    public void takeTurn() {}
    @Override
    protected void getMove(int i) {}

}


