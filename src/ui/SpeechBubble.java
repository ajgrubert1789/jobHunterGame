package ui;

import main.Game;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeechBubble {

    private String text;
    private List<String> lines = new ArrayList<>();

    private int x, y;
    private final Font font;

    private Area cachedShape;
    private int drawX, drawY, padding;
    private int bubbleWidth, bubbleHeight;

    private int maxWidth; // wrapping limit
    public SpeechBubble(String text, int x, int y) {
        this.x = x;
        this.y = y;

        float s = Game.SCALE;
        this.font = new Font("Monospaced", Font.BOLD, (int)(14 * s));
        this.padding = (int)(10 * s);
        this.maxWidth = (int)(200 * s); // max bubble width before wrapping

        setText(text);
    }

    public void setText(String text) {
        if (text != null && text.equals(this.text)) return;
        this.text = text;
        this.cachedShape = null; // force rebuild
    }

    private void wrapText(FontMetrics fm) {
        lines.clear();

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String w : words) {
            String test = current.length() == 0 ? w : current + " " + w;

            if (fm.stringWidth(test) > maxWidth) {
                lines.add(current.toString());
                current = new StringBuilder(w);
            } else {
                current = new StringBuilder(test);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }
    }

    private void updateGeometry(FontMetrics fm) {
        wrapText(fm);

        int lineHeight = fm.getHeight();

        bubbleHeight = (lineHeight * lines.size()) + padding * 2;

        bubbleWidth = 0;
        for (String line : lines) {
            bubbleWidth = Math.max(bubbleWidth, fm.stringWidth(line));
        }
        bubbleWidth += padding * 2;

        float s = Game.SCALE;
        int radius = (int)(15 * s);
        int arrowSize = (int)(15 * s);
        int arrowHalfWidth = (int)(8 * s);

        drawX = x - (bubbleWidth / 2);
        drawY = y - bubbleHeight - arrowSize;

        RoundRectangle2D rect = new RoundRectangle2D.Double(
                drawX, drawY, bubbleWidth, bubbleHeight, radius, radius
        );

        int arrowBaseX = drawX + (bubbleWidth / 2);

        Polygon arrow = new Polygon();
        arrow.addPoint(arrowBaseX - arrowHalfWidth, drawY + bubbleHeight - 1);
        arrow.addPoint(arrowBaseX + arrowHalfWidth, drawY + bubbleHeight - 1);
        arrow.addPoint(arrowBaseX, drawY + bubbleHeight + arrowSize);

        cachedShape = new Area(rect);
        cachedShape.add(new Area(arrow));
    }

    public void draw(Graphics g, float camX, float camY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        if (cachedShape == null) {
            updateGeometry(fm);
        }

        g2.translate(-camX, -camY);

        g2.setColor(Color.WHITE);
        g2.fill(cachedShape);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f * Game.SCALE));
        g2.draw(cachedShape);

        int lineHeight = fm.getHeight();
        int textY = drawY + padding + fm.getAscent();

        for (String line : lines) {
            g2.drawString(line, drawX + padding, textY);
            textY += lineHeight;
        }

        g2.translate(camX, camY);
    }
}
