package inputs;

import gamestates.GameState;
import main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KBInputs implements KeyListener {

    private final GamePanel gamePanel;

    public KBInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (GameState.state) {
            case PLAYING ->
                    gamePanel.getGame().getPlaying().keyPressed(e);

            case MENU ->
                    gamePanel.getGame().getMenu().keyPressed(e);

            case CONGRATULATIONS ->
                    gamePanel.getGame().getCongratulations().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (GameState.state) {
            case PLAYING ->
                    gamePanel.getGame().getPlaying().keyReleased(e);

            case MENU ->
                    gamePanel.getGame().getMenu().keyReleased(e);
        }
    }
}
