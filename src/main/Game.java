package main;

import entities.Player;
import gamestates.Congratulations;
import gamestates.GameState;
import gamestates.Menu;
import gamestates.Playing;
import items.ItemManager;
import levels.LevelManager;
import ui.DialogueManager;
import ui.FadeManager;

import java.awt.*;

public class Game implements Runnable {

    // ------------------------------------------------------------
    // Window & Panel
    // ------------------------------------------------------------
    private final GameWindow gameWindow;
    private final GamePanel gamePanel;
    private Thread gameThread;

    // ------------------------------------------------------------
    // Timing
    // ------------------------------------------------------------
    private static final int FPS_SET = 120;
    private static final int UPS_SET = 200;

    // ------------------------------------------------------------
    // Game States
    // ------------------------------------------------------------
    private Playing playing;
    private Menu menu;
    private Congratulations congratulations;

    // ------------------------------------------------------------
    // Tile & Screen Size
    // ------------------------------------------------------------
    public static final float SCALE = 1.0f;
    public static final int TILES_DEFAULT_SIZE = 16;
    public static final int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);

    public static final int TILES_IN_WIDTH = 32;
    public static final int TILES_IN_HEIGHT = 18;

    public static final int GAME_WIDTH  = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    // ------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------
    private final DialogueManager dialogueManager;
    private final FadeManager fadeManager;

    // ------------------------------------------------------------
    // Camera
    // ------------------------------------------------------------
    private Camera camera;

    // ------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------
    public static boolean DRAW_HITBOX = false;

    // ------------------------------------------------------------
    // Flags
    // ------------------------------------------------------------
    private boolean hasSpokenToDave = false;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Game() {
        dialogueManager = new DialogueManager();
        fadeManager     = new FadeManager();

        initStates();
        initCamera();

        gamePanel  = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);

        gamePanel.requestFocus();
        startGameLoop();
    }

    // ------------------------------------------------------------
    // Initialisation
    // ------------------------------------------------------------
    private void initStates() {
        menu            = new Menu(this);
        playing         = new Playing(this);
        congratulations = new Congratulations(this);
    }

    private void initCamera() {
        int worldWidth  = LevelManager.LEVEL_WIDTH  * TILES_SIZE;
        int worldHeight = LevelManager.LEVEL_HEIGHT * TILES_SIZE;

        camera = new Camera(worldWidth, worldHeight, GAME_WIDTH, GAME_HEIGHT);
    }

    // ------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------
    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        final double timePerFrame  = 1_000_000_000.0 / FPS_SET;
        final double timePerUpdate = 1_000_000_000.0 / UPS_SET;

        long previousTime = System.nanoTime();
        double deltaU = 0;
        double deltaF = 0;

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        while (true) {

            long currentTime = System.nanoTime();
            long elapsed = currentTime - previousTime;
            previousTime = currentTime;

            deltaU += elapsed / timePerUpdate;
            deltaF += elapsed / timePerFrame;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    // ------------------------------------------------------------
    // Update & Render
    // ------------------------------------------------------------
    private void update() {
        switch (GameState.state) {
            case PLAYING         -> playing.update();
            case MENU            -> menu.update();
            case CONGRATULATIONS -> congratulations.update();
            case OPTIONS, QUIT   -> {System.exit(0);}
        }

        fadeManager.update();
    }

    public void render(Graphics g) {
        switch (GameState.state) {
            case PLAYING         -> playing.draw(g);
            case MENU            -> menu.draw(g);
            case CONGRATULATIONS -> congratulations.draw(g);
        }

        fadeManager.draw(g, GAME_WIDTH, GAME_HEIGHT);
    }

    // ------------------------------------------------------------
    // Reset Game
    // ------------------------------------------------------------
    public void resetGame() {
        hasSpokenToDave = false;

        playing = new Playing(this);
        initCamera();
    }

    // ------------------------------------------------------------
    // Focus Handling
    // ------------------------------------------------------------
    public void windowFocusLost() {
        if (GameState.state == GameState.PLAYING)
            playing.getPlayer().resetDirBooleans();
    }

    // ------------------------------------------------------------
    // Flags
    // ------------------------------------------------------------
    public boolean hasSpokenToDave() {
        return hasSpokenToDave;
    }

    public void setHasSpokenToDave(boolean value) {
        hasSpokenToDave = value;
    }

    // ------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------
    public Camera getCamera() {
        return camera;
    }

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public Player getPlayer() {
        return playing.getPlayer();
    }

    public Congratulations getCongratulations() {
        return congratulations;
    }

    public FadeManager getFadeManager() {
        return fadeManager;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public ItemManager getItemManager() {
        return playing.getItemManager();
    }


    public void setGameState(GameState gameState) {
        GameState.state = gameState;
    }
}
