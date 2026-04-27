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

import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private final Game game;

    private BufferedImage[][] robotFrames;
    private BufferedImage[][] daveFrames;

    private final ArrayList<Robot> robots = new ArrayList<>();
    private final ArrayList<Dave>  daves  = new ArrayList<>();


    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public EnemyManager(LevelManager levelManager, Game game) {
        this.game = game;

        loadRobotFrames();
        loadDaveFrames();

        spawnRobots(levelManager);
        spawnDaves(levelManager);
    }


    // ------------------------------------------------------------
    // Update
    // ------------------------------------------------------------
    public void update() {
        Player player = game.getPlayer();

        updateRobots(player);
        updateDaves();

        robots.removeIf(r -> !r.isAlive());
        daves.removeIf(d -> !d.isAlive());
    }

    private void updateRobots(Player player) {
        for (Robot robot : robots) {
            robot.updateFacingPlayer();
            robot.update();
        }
    }

    private void updateDaves() {
        for (Dave dave : daves) {
            dave.update();
        }
    }


    // ------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------
    public void draw(Graphics g) {
        drawRobots(g);
        drawDaves(g);
    }

    private void drawRobots(Graphics g) {
        float camX = game.getCamera().getX();
        float camY = game.getCamera().getY();

        for (Robot robot : robots) {
            drawEntity(g, robotFrames, robot, ROBOT_WIDTH, ROBOT_HEIGHT, camX, camY);

            if (Game.DRAW_HITBOX)
                drawDebugHitbox(g, robot.getHitbox(), camX, camY);
        }
    }

    private void drawDaves(Graphics g) {
        float camX = game.getCamera().getX();
        float camY = game.getCamera().getY();

        for (Dave dave : daves) {
            drawEntity(g, daveFrames, dave, DAVE_WIDTH, DAVE_HEIGHT, camX, camY);

            if (Game.DRAW_HITBOX)
                drawDebugHitbox(g, dave.getHitbox(), camX, camY);
        }
    }

    private void drawEntity(Graphics g, BufferedImage[][] frames, Enemy e,
                            int drawW, int drawH, float camX, float camY) {

        int state = e.getEnemyState();
        int frame = e.getAnimationIndex();

        int screenX = (int) (e.getX() - camX);
        int screenY = (int) (e.getY() - camY);

        if (e.isFacingRight()) {
            g.drawImage(frames[state][frame], screenX, screenY, drawW, drawH, null);
        } else {
            g.drawImage(frames[state][frame], screenX + drawW, screenY, -drawW, drawH, null);
        }
    }

    private void drawDebugHitbox(Graphics g, Rectangle2D.Float hb, float camX, float camY) {
        g.setColor(Color.MAGENTA);
        g.drawRect(
                (int) (hb.x - camX),
                (int) (hb.y - camY),
                (int) hb.width,
                (int) hb.height
        );
    }


    // ------------------------------------------------------------
    // Loading
    // ------------------------------------------------------------
    private void loadRobotFrames() {
        robotFrames = new BufferedImage[4][10];
        BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.ROBOT_SPRITE);

        for (int row = 0; row < robotFrames.length; row++) {
            for (int col = 0; col < robotFrames[row].length; col++) {
                robotFrames[row][col] = atlas.getSubimage(
                        col * ROBOT_WIDTH_DEFAULT,
                        row * ROBOT_HEIGHT_DEFAULT,
                        ROBOT_WIDTH_DEFAULT,
                        ROBOT_HEIGHT_DEFAULT
                );
            }
        }
    }

    private void loadDaveFrames() {
        daveFrames = new BufferedImage[9][25];
        BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.DAVE_SPRITE);

        for (int row = 0; row < daveFrames.length; row++) {
            for (int col = 0; col < daveFrames[row].length; col++) {
                daveFrames[row][col] = atlas.getSubimage(
                        col * DAVE_WIDTH_DEFAULT,
                        row * DAVE_HEIGHT_DEFAULT,
                        DAVE_WIDTH_DEFAULT,
                        DAVE_HEIGHT_DEFAULT
                );
            }
        }
    }


    // ------------------------------------------------------------
    // Spawning
    // ------------------------------------------------------------
    private void spawnRobots(LevelManager levelManager) {
        for (Point p : levelManager.getRobotSpawns())
            robots.add(new Robot(p.x, p.y, game));
    }

    private void spawnDaves(LevelManager levelManager) {
        for (Point p : levelManager.getDaveSpawns())
            daves.add(new Dave(p.x, p.y, game));
    }


    // ------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------
    public void tryInteract() {
        Player player = game.getPlayer();
        DialogueManager dm = game.getDialogueManager();

        // Robot interaction
        for (Robot robot : robots) {
            if (isPlayerWithinInteractionRange(player, robot)) {
                dm.startDialogue(
                        robot,
                        robot.getDialogueLines(),
                        (int) (robot.getHitbox().x + robot.getHitbox().width / 2),
                        (int) (robot.getHitbox().y - 40),
                        null
                );
                return;
            }
        }

        // Dave interaction
        for (Dave dave : daves) {
            if (isPlayerWithinInteractionRange(player, dave)) {

                boolean allCollected = game.getItemManager().hasCollectedAllDisks();
                game.setHasSpokenToDave(true);

                dm.startDialogue(
                        dave,
                        dave.getDialogueLines(),
                        (int) (dave.getHitbox().x + dave.getHitbox().width / 2),
                        (int) (dave.getHitbox().y - 40),
                        allCollected ? () -> triggerVictorySequence(dave) : null
                );

                return;
            }
        }
    }


    // ------------------------------------------------------------
    // Victory Sequence
    // ------------------------------------------------------------
    public void triggerVictorySequence(Dave dave) {

        dave.setEnemyState(VICTORY_DAVE);
        game.getPlaying().getPlayer().setVictory();
        dave.resetAniTick();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        game.getFadeManager().startFadeOut(() -> {
                            game.setGameState(GameState.CONGRATULATIONS);
                            game.getFadeManager().startFadeIn();
                        });
                    }
                },
                2000
        );
    }


    // ------------------------------------------------------------
    // Combat
    // ------------------------------------------------------------
    public void checkPlayerHit(Player player) {
        if (!player.isAttacking()) return;

        Rectangle attackBox = player.getAttackBox();

        for (Robot robot : robots) {
            if (robot.getHitbox().intersects(attackBox))
                robot.onHitByPlayer();
        }
    }


    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    private boolean isPlayerWithinInteractionRange(Player player, Enemy e) {
        float dx = player.getHitbox().x - e.getHitbox().x;
        float dy = player.getHitbox().y - e.getHitbox().y;
        return Math.sqrt(dx * dx + dy * dy) < 120f;
    }
}
