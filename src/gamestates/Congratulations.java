package gamestates;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Congratulations implements StateMethods {

    private final Game game;

    private BufferedImage background;

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 20;
    private int[] characterFrameCounts = {24, 25, 10};
    private double[] characterScales = {0.4, 0.4, 0.2}; // Player, Dave, Robot


//    private final List<BufferedImage> characters = new ArrayList<>();
    private final List<Point> characterPositions = new ArrayList<>();

    public Congratulations(Game game) {
        this.game = game;
        initClasses();
    }

    private void initClasses() {
        background = LoadSave.GetSpriteAtlas(LoadSave.CONGRATS_BACKGROUND);
        animations = new BufferedImage[3][25]; // Max frames is 25

        // 1. Player (VICTORY state = row 7)
        BufferedImage playerSheet = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        for (int i = 0; i < 24; i++)
            animations[0][i] = playerSheet.getSubimage(i * 256, 6 * 256, 256, 256);

        // 2. Dave (VICTORY_DAVE state = row 7)
        BufferedImage daveSheet = LoadSave.GetSpriteAtlas(LoadSave.DAVE_SPRITE);
        for (int i = 0; i < 25; i++)
            animations[1][i] = daveSheet.getSubimage(i * 256, 5 * 256, 256, 256);

        // 3. Robot (TALK state = row 2)
        BufferedImage robotSheet = LoadSave.GetSpriteAtlas(LoadSave.ROBOT_SPRITE);
        for (int i = 0; i < 10; i++)
            animations[2][i] = robotSheet.getSubimage(i * 256, 2 * 256, 256, 256);

        // Set Positions (Spaced out for the screen)
        characterPositions.add(new Point(225, 68)); // Player
        characterPositions.add(new Point(255, 68)); // Dave
        characterPositions.add(new Point(225, 110)); // Robot
    }

    @Override
    public void update() {
        // No animation needed
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 25) aniIndex = 0; // Reset based on the longest animation
        }
    }

    @Override
    public void draw(Graphics g) {

        g.drawImage(background, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        for (int i = 0; i < 3; i++) {
            // Use modulo to ensure the character loops within its own frame count
            int frame = aniIndex % characterFrameCounts[i];
            BufferedImage img = animations[i][frame];

            Point pos = characterPositions.get(i);
            int w = (int)(256 * characterScales[i] * Game.SCALE);
            int h = (int)(256 * characterScales[i] * Game.SCALE);

            g.drawImage(img, pos.x, pos.y, w, h, null);
        }


        // 3. Draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("CONGRATULATIONS!", 70, 50);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.drawString("You got the job!", 150, 250);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press E to return to the menu", 70, 200);
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
