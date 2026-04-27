package entities;

import levels.objects.Ladder;
import levels.objects.Platform;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.LoadSave.PLAYER_ATLAS;

public class Player extends Entity {

    // ------------------------------------------------------------
    // Player State
    // ------------------------------------------------------------
    private int animationState = IDLE;

    private boolean moving = false;
    private boolean attacking = false;
    private boolean jumpRequested = false;

    private int floppyDisksCollected = 0;

    private boolean left, right, up, down;
    private boolean facingRight = true;

    private static final int SHOOT_SPEED = 6;
    private static final int RUN_SPEED   = 12;
    private static final int IDLE_SPEED  = 25;
    private static final int JUMP_SPEED  = 35;
    private static final int CLIMB_SPEED = 6;

    public boolean isAttacking() { return attacking; }

    private boolean victory = false;


    // ------------------------------------------------------------
    // Physics
    // ------------------------------------------------------------
    private float xSpeed = 0f;
    private float ySpeed = 0f;

    private boolean gravityEnabled = true;
    private final float gravity = 0.3f * Game.SCALE;
    private final float jumpStrength = -11f * Game.SCALE;

    private boolean onGround = false;
    private boolean inAir = false;

    private boolean onLadder = false;
    private boolean climbing = false;
    private Ladder currentLadder = null;

    private boolean onPlatform = false;
    private Platform currentPlatform = null;


    // ------------------------------------------------------------
    // Hitboxes
    // ------------------------------------------------------------
    private static final int HITBOX_WIDTH  = (int) (32 * Game.SCALE);
    private static final int HITBOX_HEIGHT = (int) (96 * Game.SCALE);
    private static final int HITBOX_OFFSET_X = (int) (48 * Game.SCALE);
    private static final int HITBOX_OFFSET_Y = (int) (16 * Game.SCALE);

    private Rectangle attackBox;
    private static final int ATTACK_WIDTH  = (int) (48 * Game.SCALE);
    private static final int ATTACK_HEIGHT = (int) (16 * Game.SCALE);
    private static final int ATTACK_OFFSET_X = (int) (0 * Game.SCALE);
    private static final int ATTACK_OFFSET_Y = (int) (24 * Game.SCALE);


    // ------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------
    private static final int ANI_SPEED = 25;
    private int aniTick = 0;
    private int aniIndex = 0;

    private BufferedImage spriteSheet;
    private BufferedImage[][] animationFrames;


    // ------------------------------------------------------------
    // Movement
    // ------------------------------------------------------------
    private final float playerSpeed = 2.5f * Game.SCALE;


    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Player(float x, float y, Game game) {
        super(x, y, game);

        loadAnimations();
        initHitBox((int)x + HITBOX_OFFSET_X, (int)y + HITBOX_OFFSET_Y, HITBOX_WIDTH, HITBOX_HEIGHT);

        attackBox = new Rectangle((int)x, (int)y, ATTACK_WIDTH, ATTACK_HEIGHT);
    }


    // ------------------------------------------------------------
    // Update & Render
    // ------------------------------------------------------------
    public void update() {
        updatePos();
        updateHitBox(HITBOX_OFFSET_X, HITBOX_OFFSET_Y);
        updateAttackBox();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g) {
        float camX = game.getCamera().getX();
        float camY = game.getCamera().getY();

        BufferedImage frame = animationFrames[animationState][aniIndex];

        // Scale the drawing size
        int drawW = (int) (128 * Game.SCALE);
        int drawH = (int) (128 * Game.SCALE);

        int screenX = (int)(x - camX);
        int screenY = (int)(y - camY);

        if (facingRight) {
            g.drawImage(frame, screenX, screenY, drawW, drawH, null);
        } else {
            g.drawImage(frame, screenX + drawW, screenY, -drawW, drawH, null);
        }

        if (Game.DRAW_HITBOX) {
            drawHitBox(g, camX, camY);
            if (attacking) drawAttackBox(g, camX, camY);
        }
    }


    // ------------------------------------------------------------
    // Attack Box
    // ------------------------------------------------------------
    private void updateAttackBox() {
        if (!attacking) return;

        int ax = (int) hitbox.x;
        int ay = (int) hitbox.y + ATTACK_OFFSET_Y;

        if (facingRight) {
            ax += HITBOX_WIDTH + ATTACK_OFFSET_X;
        } else {
            ax -= ATTACK_WIDTH + ATTACK_OFFSET_X;
        }

        attackBox.setBounds(ax, ay, ATTACK_WIDTH, ATTACK_HEIGHT);
    }

    public Rectangle getAttackBox() { return attackBox; }


    // ------------------------------------------------------------
    // Debug Drawing
    // ------------------------------------------------------------
    private void drawHitBox(Graphics g, float camX, float camY) {
        g.setColor(Color.BLUE);
        g.drawRect(
                (int)(hitbox.x - camX),
                (int)(hitbox.y - camY),
                (int)hitbox.width,
                (int)hitbox.height
        );
    }

    private void drawAttackBox(Graphics g, float camX, float camY) {
        g.setColor(Color.BLUE);
        g.drawRect(
                (int)(attackBox.x - camX),
                (int)(attackBox.y - camY),
                attackBox.width,
                attackBox.height
        );
    }

    public void setVictory() {
        this.victory = true;
        aniTick = 0;
        aniIndex = 0;
    }

    public boolean isInVictoryState() {
        return victory;
    }

    // ------------------------------------------------------------
    // Collision Helpers
    // ------------------------------------------------------------
    private Ladder getTouchingLadder() {
        for (Ladder ladder : game.getPlaying().getLevelManager().getLadders()) {
            if (ladder.getBox().intersects(hitbox)) return ladder;
        }
        return null;
    }

    private Platform getTouchingPlatform() {
        for (Platform platform : game.getPlaying().getLevelManager().getPlatforms()) {

            Rectangle2D.Float box = platform.getBox();

            float feetY = hitbox.y + hitbox.height;
            float feetXLeft  = hitbox.x + 5;
            float feetXRight = hitbox.x + hitbox.width - 5;

            boolean horizontallyOver =
                    feetXRight > box.x && feetXLeft < box.x + box.width;
            // Scale the tolerance (originally 8)
            float tolerance = 8f * Game.SCALE;
            boolean feetNearTop =
                    feetY >= box.y - tolerance && feetY <= box.y + tolerance;

            if (horizontallyOver && feetNearTop) return platform;
        }
        return null;
    }


    // ------------------------------------------------------------
    // Animation Logic
    // ------------------------------------------------------------
    private void updateAnimationTick() {
        aniTick++;

        int speed = switch (animationState) {
            case ATTACK -> SHOOT_SPEED;
            case RUN    -> RUN_SPEED;
            case JUMP   -> JUMP_SPEED;
            case CLIMB  -> CLIMB_SPEED;
            default     -> IDLE_SPEED;
        };

        if (aniTick >= speed) {
            aniTick = 0;
            aniIndex++;

            if (aniIndex >= getSpriteAmount(animationState)) {
                aniIndex = 0;
                if (animationState == ATTACK) attacking = false;
            }
        }
    }

    private void setAnimation() {
        int start = animationState;



        // Interrupt attack if player performs ANY other action
        boolean interrupt =
                moving ||
                        jumpRequested ||
                        climbing ||
                        inAir ||
                        !onGround;

        if (attacking && animationState != ATTACK && interrupt) {
            attacking = false;
        }

        if(victory){
            animationState = VICTORY;
        } else if (climbing)          animationState = CLIMB;
        else if (inAir)        animationState = JUMP;
        else if (attacking)    animationState = ATTACK;
        else if (moving)       animationState = RUN;
        else                   animationState = IDLE2;

        if (start != animationState) {
            aniTick = 0;
            aniIndex = 0;
        }
    }


    // ------------------------------------------------------------
    // Movement Logic
    // ------------------------------------------------------------
    private void updatePos() {

        if (victory) {
            xSpeed = 0;
            ySpeed = 0;
            moving = false;
            return;
        }

        moving = false;
        xSpeed = 0;

        // Horizontal input
        if (left && !right) {
            xSpeed = -playerSpeed;
            facingRight = false;
            moving = true;
        } else if (right && !left) {
            xSpeed = playerSpeed;
            facingRight = true;
            moving = true;
        }

        // Jump
        if (jumpRequested && onGround && !climbing) {
            ySpeed = jumpStrength;
            onGround = false;
            inAir = true;
            jumpRequested = false;
        }

        updatePlatformState();
        updateLadderState();
        applyGravity();

        moveHorizontal();
        moveVertical();
    }

    public void applyGravity() {
        if (!gravityEnabled) return;

        ySpeed += gravity;
        if (ySpeed > 10 * Game.SCALE) ySpeed = 10 * Game.SCALE;
    }


    // ------------------------------------------------------------
    // Horizontal Movement
    // ------------------------------------------------------------
    private void moveHorizontal() {
        if (xSpeed == 0) return;

        float newX = x + xSpeed;

        float leftX  = newX + HITBOX_OFFSET_X;
        float rightX = newX + HITBOX_OFFSET_X + HITBOX_WIDTH - 1;

        float topY    = y + HITBOX_OFFSET_Y + 5;
        float midY    = y + HITBOX_OFFSET_Y + HITBOX_HEIGHT / 2f;
        float bottomY = y + HITBOX_OFFSET_Y + HITBOX_HEIGHT - 5;
        // TILE COLLISION
        if (!isSolidAt(leftX, topY) &&
                !isSolidAt(leftX, midY) &&
                !isSolidAt(leftX, bottomY) &&
                !isSolidAt(rightX, topY) &&
                !isSolidAt(rightX, midY) &&
                !isSolidAt(rightX, bottomY)) {

            x = newX;
        }




    }




    // ------------------------------------------------------------
    // Platform Logic
    // ------------------------------------------------------------
    public void updatePlatformState() {
        Platform platform = getTouchingPlatform();
        boolean landing = false;

        if (platform != null && ySpeed >= 0) {

            float platformTop = platform.getBox().y;
            float feetY = hitbox.y + hitbox.height;

            if (feetY >= platformTop - 6 && feetY <= platformTop + 12) {

                landing = true;

                onPlatform = true;
                currentPlatform = platform;

                hitbox.y = platformTop - hitbox.height;
                y = hitbox.y - HITBOX_OFFSET_Y + 5;

                ySpeed = 0;
                inAir = false;
                onGround = true;
                gravityEnabled = false;
            }
        }

        if (!landing && onPlatform) {
            onPlatform = false;
            currentPlatform = null;
            gravityEnabled = true;
            inAir = true;
        }
    }


    // ------------------------------------------------------------
    // Ladder Logic
    // ------------------------------------------------------------
    public void updateLadderState() {
        Ladder ladder = getTouchingLadder();
        onLadder = (ladder != null);

        boolean climbInput = up || down;

        if (onLadder) {
            currentLadder = ladder;

            if (climbInput) {
                climbing = true;
                gravityEnabled = false;
                inAir = false;
                onGround = false;

                if (up)      ySpeed = -2f * Game.SCALE;
                else if (down) ySpeed = 2f * Game.SCALE;;

                float center = currentLadder.getBox().x + currentLadder.getBox().width / 2f;
                x = center - (HITBOX_WIDTH / 2f) - HITBOX_OFFSET_X;



            } else {
                climbing = false;
                gravityEnabled = true;
            }

        } else {
            climbing = false;
            gravityEnabled = true;
            currentLadder = null;
        }
    }


    // ------------------------------------------------------------
    // Vertical Movement
    // ------------------------------------------------------------
    private void moveVertical() {
        if (onPlatform) return;

        float newY = y + ySpeed;

        // ------------------------------------------------------------
// TOP-OF-LEVEL CLAMP (prevents white space above camera)
// ------------------------------------------------------------
        if (newY + HITBOX_OFFSET_Y < 0) {
            y = -HITBOX_OFFSET_Y;   // keep hitbox exactly at y = 0
            ySpeed = 0;
            return;
        }

        float leftX  = x + HITBOX_OFFSET_X + 3;
        float rightX = x + HITBOX_OFFSET_X + HITBOX_WIDTH - 3;

        float topY    = newY + HITBOX_OFFSET_Y;
        float bottomY = newY + HITBOX_OFFSET_Y + HITBOX_HEIGHT;

        if (ySpeed > 0) {
            // Falling
            if (!isSolidAt(leftX, bottomY) &&
                    !isSolidAt(rightX, bottomY)) {



                y = newY;
                onGround = false;
                inAir = true;

            } else {
                onGround = true;
                inAir = false;

                float tileY = (float)Math.floor(bottomY / Game.TILES_SIZE);
                y = tileY * Game.TILES_SIZE - HITBOX_HEIGHT - HITBOX_OFFSET_Y;

                ySpeed = 0;
            }

        } else {
            // Jumping upward
            if (!isSolidAt(leftX, topY) &&
                    !isSolidAt(rightX, topY)) {

                y = newY;

            } else {
                ySpeed = 0;
            }
        }
    }


    private boolean isSolidAt(float px, float py) {
        return game.getPlaying().getLevelManager().isSolidPixel(px, py);
    }


    // ------------------------------------------------------------
    // Animation Loading
    // ------------------------------------------------------------
    private void loadAnimations() {
        BufferedImage loaded = LoadSave.GetSpriteAtlas(PLAYER_ATLAS);

        spriteSheet = new BufferedImage(
                loaded.getWidth(),
                loaded.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = spriteSheet.createGraphics();
        g2d.drawImage(loaded, 0, 0, null);
        g2d.dispose();

        animationFrames = new BufferedImage[8][25];

        for (int r = 0; r < animationFrames.length; r++) {
            for (int c = 0; c < animationFrames[r].length; c++) {
                animationFrames[r][c] = spriteSheet.getSubimage(
                        c * 256,
                        r * 256,
                        256,
                        256
                );
            }
        }
    }


    // ------------------------------------------------------------
    // Input Setters
    // ------------------------------------------------------------
    public void setLeft(boolean left)   { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setUp(boolean up)       { this.up = up; }
    public void setDown(boolean down)   { this.down = down; }

    public void setMoving(boolean moving)             { this.moving = moving; }
//    public void setAttacking(boolean attacking)       { this.attacking = attacking; }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
        // Resetting these ensure the attack starts from frame 1 immediately
        if (attacking) {
            aniTick = 0;
            aniIndex = 0;
        }
    }
    public void setJumping(boolean jumpRequested)     { this.jumpRequested = jumpRequested; }

    public void resetDirBooleans() {
        left = right = up = down = false;
    }

    public void addFloppyDisk() {
        floppyDisksCollected++;
        System.out.println(floppyDisksCollected);
    }
}
