package kobting.friendlyminions.monsters;

import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MinionMoveGroup {

    private ArrayList<MinionMove> moves;
    private static final float IMAGE_SIZE = 96.0F;
    protected static int currentIndex = 0;
    protected float xStart;
    protected float yStart;

    public MinionMoveGroup(float xStart, float yStart) {
        this(new ArrayList<MinionMove>(), xStart, yStart);
    }

    public MinionMoveGroup(ArrayList<MinionMove> moves, float xStart, float yStart){
        this.moves = moves;
        this.xStart = xStart;
        this.yStart = yStart;
        updatePositions();
    }

    public void updatePositions(){
        int currentIndex = 0;
        for (MinionMove move: this.moves) {
            move.getHitbox().x = xStart + (IMAGE_SIZE * currentIndex * Settings.scale) - IMAGE_SIZE * Settings.scale;
            move.getHitbox().y = yStart - IMAGE_SIZE * Settings.scale;
            move.setX(xStart + (IMAGE_SIZE * currentIndex * Settings.scale) - IMAGE_SIZE * Settings.scale);
            move.setY(yStart - IMAGE_SIZE * Settings.scale);
            currentIndex++;
        }
    }

    public void addMove(MinionMove move) {
        moves.add(move);
        updatePositions();
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

        updatePositions();
        return null;

    }

    public void render(SpriteBatch sb) {
        currentIndex = 0;
        moves.forEach(move -> {
            sb.setColor(Color.RED);
            move.getHitbox().render(sb);
            sb.setColor(Color.WHITE);
            drawMoveImage(move, sb, move.getMoveImage(), currentIndex);
            currentIndex++;
        });
        currentIndex = 0;
    }


    public void update() {
        moves.forEach(move -> {move.update();});
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

    protected void drawMoveImage(MinionMove move, SpriteBatch sb, Texture moveImage, int currentIndex) {
        sb.draw(moveImage, move.getHitbox().x, move.getHitbox().y, 48.0f, 48.0f, IMAGE_SIZE, IMAGE_SIZE, Settings.scale * 1.5f, Settings.scale * 1.5f, 0.0f, 0, 0, moveImage.getWidth(), moveImage.getHeight(), false, false);
    }
}
