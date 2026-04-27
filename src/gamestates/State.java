package gamestates;

import java.awt.event.MouseEvent;

import main.Game;
import ui.MenuButton;

public class State {

    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public boolean isIn(MouseEvent e, MenuButton mb) {
        double scaleX = game.getGamePanel().getWidth()  / (double) Game.GAME_WIDTH;
        double scaleY = game.getGamePanel().getHeight() / (double) Game.GAME_HEIGHT;

        int mouseX = (int)(e.getX() / scaleX);
        int mouseY = (int)(e.getY() / scaleY);

        return mb.getBounds().contains(mouseX, mouseY);

    }


    public Game getGame() {
        return game;
    }
}