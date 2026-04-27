package gamestates;

import main.Game;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Congratulations implements StateMethods {

    private final Game game;

    public Congratulations(Game game) {
        this.game = game;
    }

    @Override
    public void update() {
        // No animation yet
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("CONGRATULATIONS!", 100, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.drawString("You got the job!", 260, 260);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press E to return to the menu", 220, 340);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            game.resetGame();
            GameState.state = GameState.MENU;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
