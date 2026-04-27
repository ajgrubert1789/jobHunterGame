package items;

import main.Game;

import java.awt.*;

import static utils.Constants.itemConstants.*;
import entities.Dialogue;

public class FloppyDisk extends Item {

    private boolean collected = false;

    private final String[] dialogueLines;

    public FloppyDisk(float x, float y, Game game, String[]dialogueLines) {
        super(x, y, game, FLOPPY_DISK_WIDTH, FLOPPY_DISK_HEIGHT, FLOPPY_DISK);
        this.dialogueLines = dialogueLines; // <-- save it
        setItemState(IDLE);
    }

    @Override
    public void update() {
        if (collected)
            return;

        // Base class handles hitbox + animation
        super.update();
    }

    @Override
    public String[] getDialogueLines() {
        return dialogueLines; // <-- return the unique dialogue
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    // ------------------------------------------------------------
    // Debug Rendering
    // ------------------------------------------------------------
    protected void drawHitBox(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawRect(
                (int) hitbox.x,
                (int) hitbox.y,
                (int) hitbox.width,
                (int) hitbox.height
        );
    }




}
