package entities;

import levels.objects.Ladder;
import levels.objects.Platform;
import main.Game;

import static utils.Constants.EnemyConstants.*;
import static utils.Constants.EnemyConstants.getSpriteAmount;

public abstract class Enemy extends Entity{


    private int animationIndex, enemyState, enemyType;
    private int animationTick, animationSpeed = 25;

    private boolean alive = true;

    // Scaled Offsets
    protected float hitboxOffsetX = 0 * Game.SCALE;
    protected float hitboxOffsetY = -16 * Game.SCALE;

    public abstract EnemyType getType();
    public abstract int getDrawWidth();
    public abstract int getDrawHeight();


    // -------------------------
    // Physics
    // -------------------------
    protected float xSpeed = 0f;
    protected float ySpeed = 0f;

    protected final float gravity = 0.3f * Game.SCALE;
    protected final float maxFallSpeed = 6f * Game.SCALE;

    protected boolean onGround = false;

    // Patrol direction: -1 = left, +1 = right
    protected int direction = 1;

    // Scaled Movement speed
    protected float walkSpeed = 1.0f * Game.SCALE;

    // -------------------------
// Facing Direction
// -------------------------
    protected boolean facingRight = true;

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }







    public Enemy(float x, float y, Game game, int width, int height, int enemyType) {
        super(x, y, game);
        this.enemyType = enemyType;
        initHitBox(x ,y,width,height);
    }

    private void updateAnimationTick(){

        animationTick++;
        if(animationTick>=animationSpeed){
            animationTick = 0;
            animationIndex++;
            if(animationIndex >= getSpriteAmount(enemyType,enemyState)){
                animationIndex = 0;
            }
        }
        if (enemyState == EXPLODE) {
            if (animationIndex == getSpriteAmount(enemyType, EXPLODE) - 1) {
                alive = false;
            }
        }

    }

    protected void updatePhysics() {

        // Gravity
        ySpeed += gravity;
        if (ySpeed > maxFallSpeed)
            ySpeed = maxFallSpeed;

        // Horizontal movement
        xSpeed = direction * walkSpeed;

//        moveHorizontal();
        moveVertical();
    }

    public void update(){

        updatePhysics();
        updateHitBox(hitboxOffsetX, hitboxOffsetY); // or your offsets if needed
        updateAnimationTick();

    }

    protected void moveHorizontal() {
        float newX = x + xSpeed;
        float buffer = 3 * Game.SCALE; // Scaled buffer

        // Compute hitbox world positions AFTER movement
        float leftX  = newX + hitboxOffsetX + buffer;
        float rightX = newX + hitboxOffsetX + hitbox.width - buffer;

        float topY    = hitbox.y + 5;
        float midY    = hitbox.y + hitbox.height / 2f;
        float bottomY = hitbox.y + hitbox.height - 5;

        boolean blocked =
                isSolid(leftX, topY) ||
                        isSolid(leftX, midY) ||
                        isSolid(leftX, bottomY) ||
                        isSolid(rightX, topY) ||
                        isSolid(rightX, midY) ||
                        isSolid(rightX, bottomY);

        if (!blocked) {
            x = newX;
        } else {
            direction *= -1; // turn around
        }
    }

    protected void moveVertical() {
        float newY = y + ySpeed;
        float buffer = 3 * Game.SCALE; // Scaled buffer

        float leftX  = hitbox.x + buffer;
        float rightX = hitbox.x + hitbox.width - buffer;

        float topY    = newY + hitboxOffsetY;
        float bottomY = newY + hitboxOffsetY + hitbox.height;

        if (ySpeed > 0) {
            // Falling
            if (!isSolid(leftX, bottomY) && !isSolid(rightX, bottomY)) {
                y = newY;
                onGround = false;
            } else {
                float tileY = (float) Math.floor(bottomY / Game.TILES_SIZE);
                y = tileY * Game.TILES_SIZE - hitbox.height - hitboxOffsetY;
                ySpeed = 0;
                onGround = true;
            }

        } else {
            // Moving upward
            if (!isSolid(leftX, topY) && !isSolid(rightX, topY)) {
                y = newY;
            } else {
                ySpeed = 0;
            }
        }
    }
    public boolean isSolid() {
        return false; // default: enemies are not solid
    }

    protected boolean isSolid(float px, float py) {
        return game.getPlaying().getLevelManager().isSolidPixel(px, py);
    }

    public int getAnimationIndex() {
        return animationIndex;
    }

    public int getEnemyState(){
        return enemyState;
    }

    public void setEnemyState(int newState) {
        this.enemyState = newState;
        this.animationIndex = 0; // reset animation when state changes
    }

    public boolean isAlive() {
        return alive;
    }
    public void resetAniTick() {
        animationTick = 0;
        animationIndex = 0;
    }
}
