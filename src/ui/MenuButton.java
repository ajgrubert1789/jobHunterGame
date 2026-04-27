package ui;

import gamestates.GameState;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;

public class MenuButton {

    // ------------------------------------------------------------
    // UI Scaling
    // ------------------------------------------------------------
    private static final float UI_SCALE = 0.6f;

    // ------------------------------------------------------------
    // Position & State
    // ------------------------------------------------------------
    private final int xPos;
    private final int yPos;
    private final int rowIndex;
    private final GameState state;

    private boolean mouseOver = false;
    private boolean mousePressed = false;

    // ------------------------------------------------------------
    // Graphics
    // ------------------------------------------------------------
    private final BufferedImage[] imgs = new BufferedImage[3];
    private int index = 0;

    // ------------------------------------------------------------
    // Size & Hitbox
    // ------------------------------------------------------------
    private final int w;
    private final int h;
    private final Rectangle bounds;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public MenuButton(int xPos, int yPos, int rowIndex, GameState state) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;

        this.w = (int) (B_WIDTH * UI_SCALE);
        this.h = (int) (B_HEIGHT * UI_SCALE);

        loadImages();
        this.bounds = new Rectangle(xPos - w / 2, yPos - h / 2, w, h);
    }

    // ------------------------------------------------------------
    // Load Button Frames
    // ------------------------------------------------------------
    private void loadImages() {
        BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);

        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = atlas.getSubimage(
                    i * B_WIDTH_DEFAULT,
                    rowIndex * B_HEIGHT_DEFAULT,
                    B_WIDTH_DEFAULT,
                    B_HEIGHT_DEFAULT
            );
        }
    }

    // ------------------------------------------------------------
    // Draw
    // ------------------------------------------------------------
    public void draw(Graphics g) {
        int drawX = xPos - w / 2;
        int drawY = yPos - h / 2;

        g.drawImage(imgs[index], drawX, drawY, w, h, null);
    }

    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    public void update() {
        bounds.x = xPos - w / 2;
        bounds.y = yPos - h / 2;

        index = mousePressed ? 2 : mouseOver ? 1 : 0;
    }

    // ------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------
    public Rectangle getBounds() {
        return bounds;
    }

    public void applyGamestate() {
        GameState.state = state;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }
}
