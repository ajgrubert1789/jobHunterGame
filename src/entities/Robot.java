package entities;

import main.Game;
import static utils.Constants.EnemyConstants.*;

public class Robot extends Enemy {

    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    public static final float TALK_DISTANCE = 120f * Game.SCALE;

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private boolean facingRight = true;

    private static final String[] DIALOGUE_LINES = {
            "Greetings, human. I understand you are seeking employment.",
            "A position may be available for an individual with sufficient capability.",
            "Proceed to speak with Dave, the manager located on the lowest level."
    };

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Robot(float x, float y, Game game) {
        super(x, y, game, ROBOT_WIDTH, ROBOT_HEIGHT, ROBOT);

        hitboxOffsetX = 17 * Game.SCALE;
        hitboxOffsetY = 10 * Game.SCALE;

        initHitBox(x, y,
                (int) (30 * Game.SCALE),
                (int) (50 * Game.SCALE)
        );

        setEnemyState(IDLE);
    }

    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    @Override
    public void update() {
        super.update();
        if (getEnemyState() == EXPLODE)
            return;

        updateAnimationState();
    }

    private void updateAnimationState() {
        boolean isTalking = isSpeaking();
        int targetState = isTalking ? TALK : IDLE;

        if (getEnemyState() != targetState)
            setEnemyState(targetState);
    }

    // ------------------------------------------------------------
    // Speaking (computed, no stored boolean)
    // ------------------------------------------------------------
    public boolean isSpeaking() {
        return game.getDialogueManager().isActive()
                && game.getDialogueManager().getCurrentSpeaker() == this;
    }
    @Override
    public boolean isSolid() {
        return true;
    }

    // ------------------------------------------------------------
    // Facing
    // ------------------------------------------------------------
    public void updateFacingPlayer() {
        float playerX = game.getPlaying().getPlayer().getHitbox().x;
        float robotX  = getHitbox().x;

        facingRight = playerX > robotX;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    // ------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------
    public void onHitByPlayer() {
        setEnemyState(EXPLODE);
    }

    // ------------------------------------------------------------
    // Dialogue
    // ------------------------------------------------------------
    public String[] getDialogueLines() {
        return DIALOGUE_LINES;
    }

    // ------------------------------------------------------------
    // EnemyManager Integration
    // ------------------------------------------------------------
    @Override
    public EnemyType getType() {
        return EnemyType.ROBOT;
    }

    @Override
    public int getDrawWidth() {
        return ROBOT_WIDTH;
    }

    @Override
    public int getDrawHeight() {
        return ROBOT_HEIGHT;
    }
}
