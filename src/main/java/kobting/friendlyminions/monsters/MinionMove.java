package kobting.friendlyminions.monsters;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;


public class MinionMove {

    private String ID;
    private String moveDescription;
    private Texture moveImage;
    private Runnable moveActions;
    private Hitbox hitbox;
    private float hb_w;
    private float hb_h;
    private boolean selected = false;

    public MinionMove(String ID, Texture moveImage, String moveDescription, Runnable moveActions) {
        this.moveImage = moveImage;
        this.moveActions = moveActions;
        this.hb_w = 64.0F * Settings.scale;
        this.hb_h = 64.0F * Settings.scale;
        this.hitbox = new Hitbox(hb_w, hb_h);
        this.ID = ID;
        this.moveDescription = moveDescription;
    }

    public void doMove() {
        if(moveActions != null) {
            moveActions.run();
        } else {
            BaseMod.logger.info("MinionMove: " + this.ID + " had no actions!");
        }
    }

    public Hitbox getHitbox() {
        return this.hitbox;
    }

    public Texture getMoveImage(){
        return this.moveImage;
    }

    public void setMoveImage(Texture moveImage){
        this.moveImage = moveImage;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getID(){
        return this.ID;
    }

    public String getMoveDescription(){
        return this.moveDescription;
    }
}
