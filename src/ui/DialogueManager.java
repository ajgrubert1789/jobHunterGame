package ui;

import entities.Dialogue;
import entities.Enemy;
import ui.SpeechBubble;
import java.awt.*;

public class DialogueManager {

    private SpeechBubble uiBubble;
    private Dialogue currentDialogue;
    private boolean active;
    private Runnable onDialogueFinished;

    // NEW: Track which NPC is speaking
    private Enemy currentSpeaker;

    private int autoCloseTimer = 0;
    private boolean temporary = false;

    // ------------------------------------------------------------
    // Start dialogue for a specific NPC
    // ------------------------------------------------------------
    public void startDialogue(Enemy speaker, String[] lines, int x, int y, Runnable onFinish) {
        this.currentSpeaker = speaker;
        this.currentDialogue = new Dialogue(lines);
        this.onDialogueFinished = onFinish;

        String firstLine = currentDialogue.getNextLine();
        if (firstLine != null) {
            this.uiBubble = new SpeechBubble(firstLine, x, y);
            this.active = true;
        }
    }

    // ------------------------------------------------------------
    // Advance dialogue
    // ------------------------------------------------------------
    public void nextLine() {
        if (!active || currentDialogue == null) return;

        String next = currentDialogue.getNextLine();
        if (next != null) {
            uiBubble.setText(next);
        } else {
            active = false;
            uiBubble = null;

            if (onDialogueFinished != null)
                onDialogueFinished.run();

            currentSpeaker = null;
            onDialogueFinished = null;
        }
    }

    // ------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------
    public void draw(Graphics g, float camX, float camY) {
        if (active && uiBubble != null) {
            uiBubble.draw(g, camX, camY);
        }
    }

    public void update() {
        if (!active) return;


    }

    // ------------------------------------------------------------
    // Query state
    // ------------------------------------------------------------
    public boolean isActive() {
        return active;
    }

    public Enemy getCurrentSpeaker() {
        return currentSpeaker;
    }

    public void showTemporaryMessage(String text, float worldX, float worldY) {
        this.currentSpeaker = null;
        this.currentDialogue = new Dialogue(new String[]{ text });

        this.uiBubble = new SpeechBubble(text, (int) worldX, (int) worldY);
        this.active = true;
    }


}
