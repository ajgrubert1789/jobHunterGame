package entities;

import main.Game;
import static utils.Constants.EnemyConstants.*;

public class Dave extends Enemy {

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private boolean facingRight = true;

    private static final String[] DIALOGUE_LINES = {
            "Oh! A human applicant. Those are rare these days.",
            "Hello, I'm Dave, the manager. I hear you're searching for a job.",
            "Let me review your résumé for a moment.",
            "[.]", "[..]", "[...]",
            "Not bad at all, but unfortunately you're still missing a few required skills.",
            "Come back once you've picked up the remaining qualifications."
    };

    private static final String[] COMPLETED_DIALOGUE = {
            "Ah, you’ve collected all the disks!",
            "Excellent. You now have all the required skills.",
            "Welcome aboard — you're hired!"
    };

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Dave(float x, float y, Game game) {
        super(x, y, game, DAVE_WIDTH, DAVE_HEIGHT, DAVE);

        hitboxOffsetX = 40 * Game.SCALE;
        hitboxOffsetY = 8 * Game.SCALE;

        initHitBox(
                x, y,
                (int) (48 * Game.SCALE),
                (int) (100 * Game.SCALE)
        );

        setEnemyState(WAVE_DAVE);
    }

    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    @Override
    public void update() {
        super.update();

        if (getEnemyState() == EXPLODE)
            return;

        updateSpeakingState();
        updateAnimationState();
        updateFacingPlayer();
    }

    private void updateSpeakingState() {
        // This method currently only checks state; kept for structure.
        boolean isTalking =
                game.getDialogueManager().isActive() &&
                        game.getDialogueManager().getCurrentSpeaker() == this;
    }

    public boolean isSpeaking() {
        return game.getDialogueManager().isActive()
                && game.getDialogueManager().getCurrentSpeaker() == this;
    }

    private void updateAnimationState() {
        if (getEnemyState() == VICTORY_DAVE)
            return;
        boolean isTalking = isSpeaking();
        int targetState = isTalking ? TALKING_DAVE : COFFEE_TIME;

        if (getEnemyState() != targetState)
            setEnemyState(targetState);
    }

    // ------------------------------------------------------------
    // Facing
    // ------------------------------------------------------------
    private void updateFacingPlayer() {
        float playerX = game.getPlaying().getPlayer().getHitbox().x;
        float daveX   = getHitbox().x;
        facingRight = playerX > daveX;
    }

    @Override
    public boolean isFacingRight() {
        return facingRight;
    }

    // ------------------------------------------------------------
    // Dialogue
    // ------------------------------------------------------------
    public String[] getDialogueLines() {
        return game.getItemManager().hasCollectedAllDisks()
                ? COMPLETED_DIALOGUE
                : DIALOGUE_LINES;
    }

    // ------------------------------------------------------------
    // EnemyManager Integration
    // ------------------------------------------------------------
    @Override
    public EnemyType getType() {
        return EnemyType.DAVE;
    }

    @Override
    public int getDrawWidth() {
        return DAVE_WIDTH;
    }

    @Override
    public int getDrawHeight() {
        return DAVE_HEIGHT;
    }
}
