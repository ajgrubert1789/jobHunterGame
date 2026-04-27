package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Game;
import ui.MenuButton;
import utils.LoadSave;

import static utils.Constants.UI.Buttons.B_HEIGHT;

public class Menu extends State implements StateMethods {

    // ------------------------------------------------------------
    // Background
    // ------------------------------------------------------------
    private static final float BG_SCALE = 0.4f;

    private BufferedImage backgroundImg;
    private int bgW, bgH;
    private int scaledBgW, scaledBgH;
    private int scaledBgX, scaledBgY;

    // ------------------------------------------------------------
    // Buttons
    // ------------------------------------------------------------
    private final MenuButton[] buttons = new MenuButton[2];

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Menu(Game game) {
        super(game);
        loadBackground();
        computeScaledBackground();
        loadButtons();
    }

    // ------------------------------------------------------------
    // Background Loading & Scaling
    // ------------------------------------------------------------
    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        bgW = backgroundImg.getWidth();
        bgH = backgroundImg.getHeight();
    }

    private void computeScaledBackground() {
        scaledBgW = (int) (bgW * BG_SCALE);
        scaledBgH = (int) (bgH * BG_SCALE);

        scaledBgX = (Game.GAME_WIDTH - scaledBgW) / 2;
        scaledBgY = (Game.GAME_HEIGHT - scaledBgH) / 2;
    }

    // ------------------------------------------------------------
    // Buttons
    // ------------------------------------------------------------
    private void loadButtons() {
        int centerX = scaledBgX + scaledBgW / 2;

        // Independent vertical placement for each button
        int playY = scaledBgY + (int) (scaledBgH * 0.48f);
        int quitY = scaledBgY + (int) (scaledBgH * 0.78f);

        buttons[0] = new MenuButton(centerX, playY, 0, GameState.PLAYING);
        buttons[1] = new MenuButton(centerX, quitY, 2, GameState.QUIT);
    }

    // ------------------------------------------------------------
    // Update & Draw
    // ------------------------------------------------------------
    @Override
    public void update() {
        for (MenuButton mb : buttons)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, scaledBgX, scaledBgY, scaledBgW, scaledBgH, null);

        for (MenuButton mb : buttons)
            mb.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    // ------------------------------------------------------------
    // Mouse Input
    // ------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton mb : buttons)
            if (isIn(e, mb))
                mb.setMousePressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton mb : buttons)
            if (isIn(e, mb) && mb.isMousePressed())
                mb.applyGamestate();

        resetButtons();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButton mb : buttons)
            mb.setMouseOver(false);

        for (MenuButton mb : buttons)
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }
    }

    private void resetButtons() {
        for (MenuButton mb : buttons)
            mb.resetBools();
    }

    // ------------------------------------------------------------
    // Keyboard Input
    // ------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameState.state = GameState.PLAYING;
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
