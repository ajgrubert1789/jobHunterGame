package utils;

import main.Game;

import java.awt.image.BufferedImage;

public class HelpMethods {

    Game game;

    public HelpMethods(Game game) {
        this.game = game;
    }

    private boolean isSolidAt(float px, float py) {
        return game.getPlaying().getLevelManager().isSolidPixel(px, py);
    }
}
