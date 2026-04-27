package items;

import main.Game;
import utils.Constants.itemConstants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.Constants.itemConstants.*;


public abstract class Item {

    // ------------------------------------------------------------
    // Position & Game Reference
    // ------------------------------------------------------------
    protected float x;
    protected float y;
    protected Game game;

    // ------------------------------------------------------------
    // Hitbox
    // ------------------------------------------------------------
    protected Rectangle2D.Float hitbox;

    // Scaled offsets
    protected float hitboxOffsetX = 0;
    protected float hitboxOffsetY = 0;

    // ------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------
    private int animationIndex;
    private int animationTick;
    private int animationSpeed;

    private int itemState;
    private int itemType;

    // ------------------------------------------------------------
    // State
    // ------------------------------------------------------------
    private boolean visible = true;
    private boolean collected = false;

    // ------------------------------------------------------------
    // Physics
    // ------------------------------------------------------------
    protected float xSpeed = 0f;
    protected float ySpeed = 0f;

    protected final float gravity = 0.3f * Game.SCALE;
    protected final float maxFallSpeed = 6f * Game.SCALE;

    protected boolean onGround = false;


    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Item(float x, float y, Game game, int width, int height, int itemType) {
        this.x = x;
        this.y = y;
        this.game = game;
        this.itemType = itemType;

        this.animationSpeed = 6;
        this.itemState = IDLE;   // <-- REQUIRED

        initHitBox(x, y, width, height);
    }
    public String[] getDialogueLines() {
        return null; // default: no dialogue
    }



    // ------------------------------------------------------------
    // Initialisation
    // ------------------------------------------------------------
    public void initHitBox(float x, float y, int width, int height) {
        hitbox = new Rectangle2D.Float(x, y, width, height);
        this.x = x;
        this.y = y;

    }


    // ------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------
    void updateAnimationTick() {
        animationTick++;

        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;

            // Wrap for idle animation
            if (itemState == IDLE) {
                int max = getSpriteAmount(itemType, IDLE);
                if (animationIndex >= max) {
                    animationIndex = 0;
                }
            }

            // Handle collected animation
            if (itemState == COLLECTED) {
                if (animationIndex >= getSpriteAmount(itemType, COLLECTED) - 1) {
                    collected = true;
                }
            }
        }
    }

    public void update(){

        updateHitBox(hitboxOffsetX, hitboxOffsetY); // or your offsets if needed
        updateAnimationTick();

    }

    public int getAnimationIndex() {
        return animationIndex;
    }




    // ------------------------------------------------------------
    // Hitbox
    // ------------------------------------------------------------
    protected void updateHitBox(float offsetX, float offsetY) {
        hitbox.x = x + offsetX;
        hitbox.y = y + offsetY;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }


    // ------------------------------------------------------------
    // State
    // ------------------------------------------------------------
    public void setItemState(int newState) {
        this.itemState = newState;
        this.animationIndex = 0; // reset animation when state changes
    }

    public boolean isCollected() {
        return collected;
    }

    public int getItemState() {
        return itemState;
    }

    // ------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------
    public float getX() { return x; }
    public float getY() { return y; }
}
