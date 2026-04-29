package entities;

import gamestates.GameState;
import levels.LevelManager;
import main.Game;
import ui.DialogueManager;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

    private final Game game;
    private final Map<Class<? extends Enemy>, BufferedImage[][]> enemySprites = new HashMap<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();

    public EnemyManager(LevelManager levelManager, Game game) {
        this.game = game;
        loadAllEnemyAssets();
        spawnAllEnemies(levelManager);
    }

    private void loadAllEnemyAssets() {
        // Adding a new enemy is now just one line here
        enemySprites.put(Robot.class, loadFrames(LoadSave.ROBOT_SPRITE, 4, 10, ROBOT_WIDTH_DEFAULT, ROBOT_HEIGHT_DEFAULT));
        enemySprites.put(Dave.class, loadFrames(LoadSave.DAVE_SPRITE, 9, 25, DAVE_WIDTH_DEFAULT, DAVE_HEIGHT_DEFAULT));
    }

    private BufferedImage[][] loadFrames(String path, int rows, int cols, int w, int h) {
        BufferedImage atlas = LoadSave.GetSpriteAtlas(path);
        BufferedImage[][] frames = new BufferedImage[rows][cols];
        for (int j = 0; j < rows; j++)
            for (int i = 0; i < cols; i++)
                frames[j][i] = atlas.getSubimage(i * w, j * h, w, h);
        return frames;
    }

    private void spawnAllEnemies(LevelManager lm) {
        lm.getRobotSpawns().forEach(p -> enemies.add(new Robot(p.x, p.y, game)));
        lm.getDaveSpawns().forEach(p -> enemies.add(new Dave(p.x, p.y, game)));
    }

    public void update() {
        for (Enemy e : enemies) {
            // Specific behavior like robots turning to face you stays here
            if (e instanceof Robot r) r.updateFacingPlayer();
            e.update();
        }
        enemies.removeIf(e -> !e.isAlive());
    }

    public void draw(Graphics g) {
        float camX = game.getCamera().getX();
        float camY = game.getCamera().getY();

        for (Enemy e : enemies) {
            BufferedImage[][] frames = enemySprites.get(e.getClass());

            // CLEAN: Using the methods you just added to the Enemy classes
            drawEntity(g, frames, e, e.getDrawWidth(), e.getDrawHeight(), camX, camY);

            if (Game.DRAW_HITBOX)
                drawDebugHitbox(g, e.getHitbox(), camX, camY);
        }
    }

    public void tryInteract() {
        Player player = game.getPlayer();
        DialogueManager dm = game.getDialogueManager();

        for (Enemy e : enemies) {
            if (isPlayerWithinInteractionRange(player, e)) {
                // Get dialogue position (centered above hitbox)
                int cx = (int) (e.getHitbox().x + e.getHitbox().width / 2);
                int cy = (int) (e.getHitbox().y - 40);

                // Dave has a unique victory callback, others don't
                if (e instanceof Dave dave) {
                    boolean allCollected = game.getItemManager().hasCollectedAllDisks();
                    game.setHasSpokenToDave(true);
                    dm.startDialogue(dave, dave.getDialogueLines(), cx, cy,
                            allCollected ? () -> triggerVictorySequence(dave) : null);
                } else {
                    dm.startDialogue(e, e.getDialogueLines(), cx, cy, null);
                }
                return;
            }
        }
    }

    public void checkPlayerHit(Player player) {
        if (!player.isAttacking()) return;
        Rectangle attackBox = player.getAttackBox();

        for (Enemy e : enemies) {
            // Only robots are destructible in current design
            if (e instanceof Robot r && r.getHitbox().intersects(attackBox)) {
                r.onHitByPlayer();
            }
        }
    }

    private void drawEntity(Graphics g, BufferedImage[][] frames, Enemy e, int dw, int dh, float camX, float camY) {
        int state = e.getEnemyState();
        int frame = e.getAnimationIndex();
        int screenX = (int) (e.getX() - camX);
        int screenY = (int) (e.getY() - camY);

        if (e.isFacingRight()) {
            g.drawImage(frames[state][frame], screenX, screenY, dw, dh, null);
        } else {
            g.drawImage(frames[state][frame], screenX + dw, screenY, -dw, dh, null);
        }
    }

    private void drawDebugHitbox(Graphics g, Rectangle2D.Float hb, float camX, float camY) {
        g.setColor(Color.MAGENTA);
        g.drawRect((int) (hb.x - camX), (int) (hb.y - camY), (int) hb.width, (int) hb.height);
    }

    public void triggerVictorySequence(Dave dave) {
        dave.setEnemyState(VICTORY_DAVE);
        game.getPlaying().getPlayer().setVictory();
        dave.resetAniTick();

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                game.getFadeManager().startFadeOut(() -> {
                    game.setGameState(GameState.CONGRATULATIONS);
                    game.getFadeManager().startFadeIn();
                });
            }
        }, 2000);
    }

    private boolean isPlayerWithinInteractionRange(Player player, Enemy e) {
        float dx = (player.getHitbox().x + player.getHitbox().width / 2) - (e.getHitbox().x + e.getHitbox().width / 2);
        float dy = (player.getHitbox().y + player.getHitbox().height / 2) - (e.getHitbox().y + e.getHitbox().height / 2);
        return (dx * dx + dy * dy) < (120f * 120f * Game.SCALE * Game.SCALE);
    }
}
