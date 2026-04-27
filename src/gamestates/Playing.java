package gamestates;

import entities.EnemyManager;
import entities.Player;
import items.ItemManager;
import levels.LevelManager;
import main.Game;
import ui.DialogueManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements StateMethods {

    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private DialogueManager dialogueManager;
    private ItemManager itemManager;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public Playing(Game game) {
        super(game);
        initClasses();
    }

    // ------------------------------------------------------------
    // Initialisation
    // ------------------------------------------------------------
    private void initClasses() {
        dialogueManager = game.getDialogueManager();
        levelManager    = new LevelManager(game);
        itemManager     = new ItemManager(levelManager, game);
        enemyManager    = new EnemyManager(levelManager, game);
        player          = new Player(100, 100, game);
    }

    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    @Override
    public void update() {

        // Dialogue mode freezes gameplay
        if (dialogueManager.isActive()) {
            dialogueManager.update();
            return;
        }

        player.update();
        enemyManager.update();
        enemyManager.checkPlayerHit(player);

        game.getCamera().update(
                (int) player.getX(),
                (int) player.getY()
        );

        levelManager.update();
        itemManager.update(player);
        dialogueManager.update();
    }

    // ------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------
    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        itemManager.draw(g);
        enemyManager.draw(g);
        player.render(g);

        dialogueManager.draw(g, game.getCamera().getX(), game.getCamera().getY());
    }

    // ------------------------------------------------------------
    // Mouse Input
    // ------------------------------------------------------------
    @Override public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            player.setAttacking(true);
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    // ------------------------------------------------------------
    // Keyboard Input
    // ------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {

        // Dialogue mode takes priority
        if (dialogueManager.isActive()) {
            if (e.getKeyCode() == KeyEvent.VK_E ||
                    e.getKeyCode() == KeyEvent.VK_ENTER) {

                dialogueManager.nextLine();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W     -> player.setUp(true);
            case KeyEvent.VK_S     -> player.setDown(true);
            case KeyEvent.VK_A     -> player.setLeft(true);
            case KeyEvent.VK_D     -> player.setRight(true);
            case KeyEvent.VK_SPACE -> player.setJumping(true);
            case KeyEvent.VK_H     -> Game.DRAW_HITBOX = !Game.DRAW_HITBOX;
            case KeyEvent.VK_E     -> enemyManager.tryInteract();
            case KeyEvent.VK_ESCAPE -> {
                GameState.state = GameState.MENU;
                windowFocusLost();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Stop movement during dialogue
        if (dialogueManager.isActive()) {
            player.resetDirBooleans();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W     -> player.setUp(false);
            case KeyEvent.VK_S     -> player.setDown(false);
            case KeyEvent.VK_A     -> player.setLeft(false);
            case KeyEvent.VK_D     -> player.setRight(false);
            case KeyEvent.VK_SPACE -> player.setJumping(false);
        }
    }

    // ------------------------------------------------------------
    // Focus Handling
    // ------------------------------------------------------------
    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    // ------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------
    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
