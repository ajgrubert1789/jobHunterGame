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

    private final List<BufferedImage> characters = new ArrayList<>();
    private final List<Point> characterPositions = new ArrayList<>();

    public Congratulations(Game game, Playing playing) {
        this.game = game;
        initClasses();
    }

    private void initClasses() {
        background = LoadSave.GetSpriteAtlas(LoadSave.CONGRATS_BACKGROUND);

        BufferedImage playerSheet = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        BufferedImage daveSheet   = LoadSave.GetSpriteAtlas(LoadSave.DAVE_SPRITE);
        BufferedImage robotSheet  = LoadSave.GetSpriteAtlas(LoadSave.ROBOT_SPRITE);

        int fw = 256;
        int fh = 256;

        BufferedImage playerFrame = playerSheet.getSubimage(0, 0, fw, fh);
        BufferedImage daveFrame   = daveSheet.getSubimage(0, 0, fw, fh);
        BufferedImage robotFrame  = robotSheet.getSubimage(0, 0, fw, fh);

        characters.add(playerFrame);
        characterPositions.add(new Point(100, 100));

        characters.add(daveFrame);
        characterPositions.add(new Point(150, 150));

        characters.add(robotFrame);
        characterPositions.add(new Point(200, 150));
    }

    @Override
    public void update() {
        // No animation needed
    }

    @Override
    public void draw(Graphics g) {

        // 1. Draw background
        g.drawImage(background, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        // 2. Draw characters (THIS is where the scaling code goes)
        for (int i = 0; i < characters.size(); i++) {
            BufferedImage img = characters.get(i);
            Point pos = characterPositions.get(i);

            double scale = 0.4; // 40% size (make them smaller)

            int w = (int)(img.getWidth() * scale);
            int h = (int)(img.getHeight() * scale);

            g.drawImage(img, pos.x, pos.y, w, h, null);
        }

        // 3. Draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("CONGRATULATIONS!", 100, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.drawString("You got the job!", 220, 250);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press E to return to the menu", 200, 350);
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
