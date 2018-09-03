package kobting.friendlyminions.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class MinionMoveGroup {

    private ArrayList<MinionMove> moves;
    private Color selectedColor = Color.GREEN;
    private static final float IMAGE_SIZE = 96.0F;
    protected static int currentIndex = 0;
    protected float xStart;
    protected float yStart;

    public MinionMoveGroup(float xStart, float yStart) {
        this.moves = new ArrayList<>();
        this.xStart = xStart;
        this.yStart = yStart;
    }

    public MinionMoveGroup(ArrayList<MinionMove> moves, float xStart, float yStart){
        this.moves = moves;
        this.xStart = (float)Settings.WIDTH * .075F + xStart * Settings.scale;
        this.yStart = yStart * Settings.scale;
    }

    public MinionMoveGroup(ArrayList<MinionMove> moves, float xStart, float yStart, Color selectedColor) {
        this.moves = moves;
        this.selectedColor = selectedColor;
        this.xStart = (float)Settings.WIDTH * .075F + xStart * Settings.scale;
        this.yStart = yStart * Settings.scale;
    }

    public void addMove(MinionMove move) {
        moves.add(move);
    }

    public ArrayList<MinionMove> getMoves(){
        return this.moves;
    }

    public MinionMove removeMove(String moveID) {

        Iterator<MinionMove> iterator = moves.iterator();

        while (iterator.hasNext()) {
            MinionMove move = iterator.next();
            if (move.getID().equals(moveID)) {
                moves.remove(move);
                return move;
            }
        }

        return null;

    }

    public void render(SpriteBatch sb) {
        currentIndex = 0;
        moves.forEach(move -> {
            if(move.isSelected()) {
                sb.setColor(selectedColor);
            } else {
                sb.setColor(Color.WHITE);
            }
            drawMoveImage(sb, move.getMoveImage(), currentIndex);
            currentIndex++;
        });
        currentIndex = 0;
    }

    public void update() {
        currentIndex = 0;
        moves.forEach(minionMove -> {

            //minionMove.getHitbox().update(xStart, yStart);

            float x = xStart + (IMAGE_SIZE * currentIndex * Settings.scale);
            minionMove.getHitbox().move(x - IMAGE_SIZE / 2, yStart - IMAGE_SIZE / 2);
            minionMove.getHitbox().update();
            if(minionMove.getHitbox().hovered) {
                TipHelper.renderGenericTip(x ,yStart, minionMove.getID(), minionMove.getMoveDescription());
            }

            if(minionMove.getHitbox().hovered && InputHelper.justClickedLeft) {
                onClick(minionMove);
            }
            currentIndex++;
        });
        currentIndex = 0;
    }

    public void setxStart(float xStart) {
        this.xStart = xStart;
    }

    public void setyStart(float yStart) {
        this.yStart = yStart;
    }

    public float getxStart() {
        return this.xStart;
    }

    public float getyStart() {
        return this.yStart;
    }

    protected void onClick(MinionMove move){
        move.setSelected(!move.isSelected());
        moves.forEach(movee -> {
            if(movee.isSelected() && !movee.getID().equals(move.getID())) {
                movee.setSelected(false);
            }
        });
    }

    protected void drawMoveImage(SpriteBatch sb, Texture moveImage, int currentIndex) {
        sb.draw(moveImage, xStart + (IMAGE_SIZE * currentIndex * Settings.scale) - IMAGE_SIZE, yStart - IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE, Settings.scale, Settings.scale, 0.0f, 0, 0, moveImage.getWidth(), moveImage.getHeight(), false, false);
    }
}
