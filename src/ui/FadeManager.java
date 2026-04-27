package ui;

import java.awt.*;

public class FadeManager {

    private float alpha = 0f;
    private boolean fadingOut = false;
    private boolean fadingIn = false;
    private Runnable onFadeComplete;

    private static final float SPEED = 0.02f;

    // ------------------------------------------------------------
    // Fade Control
    // ------------------------------------------------------------
    public void startFadeOut(Runnable onComplete) {
        fadingOut = true;
        fadingIn = false;
        onFadeComplete = onComplete;
    }

    public void startFadeIn() {
        fadingIn = true;
        fadingOut = false;
        onFadeComplete = null;
    }

    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    public void update() {

        if (fadingOut) {
            alpha += SPEED;

            if (alpha >= 1f) {
                alpha = 1f;
                fadingOut = false;

                if (onFadeComplete != null)
                    onFadeComplete.run();
            }
        }

        if (fadingIn) {
            alpha -= SPEED;

            if (alpha <= 0f) {
                alpha = 0f;
                fadingIn = false;
            }
        }
    }

    // ------------------------------------------------------------
    // Draw
    // ------------------------------------------------------------
    public void draw(Graphics g, int width, int height) {
        if (alpha <= 0f)
            return;

        Graphics2D g2 = (Graphics2D) g;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // Reset composite
        g2.setComposite(AlphaComposite.SrcOver);
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    public boolean isFading() {
        return fadingOut || fadingIn;
    }

    public float getAlpha() {
        return alpha;
    }
}
