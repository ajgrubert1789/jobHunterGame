package main;

import inputs.KBInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;

import static main.Game.GAME_WIDTH;
import static main.Game.GAME_HEIGHT;

public class GamePanel extends JPanel {

    private final Game game;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public GamePanel(Game game) {
        this.game = game;
        setupPanel();

        KBInputs kb = new KBInputs(this);
        MouseInputs mouse = new MouseInputs(this);

        addKeyListener(kb);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public Game getGame() {
        return game;
    }

    // ------------------------------------------------------------
    // Panel Setup
    // ------------------------------------------------------------
    private void setupPanel() {
        setBackground(Color.BLACK);
        setPreferredSize(null);   // fullscreen handled by GameWindow
        setFocusable(true);
    }

    // ------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        double scaleX = getWidth()  / (double) GAME_WIDTH;
        double scaleY = getHeight() / (double) GAME_HEIGHT;

        g2.scale(scaleX, scaleY);

        game.render(g2);
    }
}
