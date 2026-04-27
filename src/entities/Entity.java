package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x;
    protected float y;

    protected Game game;
    protected Rectangle2D.Float hitbox;

    public Entity(float x, float y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    // ------------------------------------------------------------
    // Position Accessors
    // ------------------------------------------------------------
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // ------------------------------------------------------------
    // Hitbox Management
    // ------------------------------------------------------------
    protected void initHitBox(float x, float y, int width, int height) {
        hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    protected void updateHitBox(float offsetX, float offsetY) {
        hitbox.x = x + offsetX;
        hitbox.y = y + offsetY;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
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
