package items;

import entities.Enemy;
import entities.Player;
import entities.Robot;
import levels.LevelManager;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.DAVE_HEIGHT_DEFAULT;
import static utils.Constants.EnemyConstants.DAVE_WIDTH_DEFAULT;
import static utils.Constants.itemConstants.*;

public class ItemManager {

    private final Game game;

    private BufferedImage[][] floppyDiskFrames;
    private final ArrayList<FloppyDisk>floppyDisks = new ArrayList<>();

    public boolean hasCollectedAllDisks() {
        return floppyDisks.isEmpty(); // all removed = all collected
    }


    public ItemManager(LevelManager levelManager, Game game) {
        this.game = game;

        loadFloppyDiskFrames();
        spawnFloppyDisks( levelManager);


    }

    public void update(Player player) {

        // 🔥 If player has not spoken to Dave, items do nothing
        if (!game.hasSpokenToDave()) {
            return;
        }


        for (FloppyDisk fd : floppyDisks) {
            fd.update();
            if (!fd.isCollected() && player.getHitbox().intersects(fd.getHitbox())) {

                fd.collect();
                player.addFloppyDisk();

                // 🔥 Stop movement immediately
                player.resetDirBooleans();

                // 🔥 ITEM-SPECIFIC DIALOGUE
                String[] lines = fd.getDialogueLines();
                if (lines != null) {
                    game.getDialogueManager().startDialogue(
                            null,
                            lines,
                            (int) fd.getX(),
                            (int) (fd.getY() - 32 * Game.SCALE),
                            null // items have no post-dialogue action
                    );

                }
            }
        }

        floppyDisks.removeIf(fd -> fd.isCollected());
    }

    private void spawnFloppyDisks(LevelManager levelManager) {
        int index = 0;
        for (Point p : levelManager.getFloppyDiskSpawns()) {

            String[] dialogue = switch (index) {
                case 0 -> new String[]{ "You learned HTML!" };
                case 1 -> new String[]{ "You learned CSS!" };
                case 2 -> new String[]{ "You learned JavaScript!" };
                default -> new String[]{ "You found a mysterious disk..." };
            };

            floppyDisks.add(new FloppyDisk(p.x, p.y, game, dialogue));
            index++;
        }
    }

    private void loadFloppyDiskFrames() {

        floppyDiskFrames = new BufferedImage[1][25];
        BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.FLOPPY_DISK_SPRITE);

        for (int row = 0; row < floppyDiskFrames.length; row++) {
            for (int col = 0; col < floppyDiskFrames[row].length; col++) {
                floppyDiskFrames[row][col] = atlas.getSubimage(
                        col * FLOPPY_DISK_WIDTH_DEFAULT,
                        row * FLOPPY_DISK_HEIGHT_DEFAULT,
                        FLOPPY_DISK_WIDTH_DEFAULT,
                        FLOPPY_DISK_HEIGHT_DEFAULT
                );
            }
        }
    }

    public void draw(Graphics g){
        if (!game.hasSpokenToDave()) return;
        drawFloppyDisks(g);


    }

    private void drawDebugHitbox(Graphics g, Rectangle2D.Float hb, float camX, float camY) {
        g.setColor(Color.GREEN);
        g.drawRect(
                (int) (hb.x - camX),
                (int) (hb.y - camY),
                (int) hb.width,
                (int) hb.height
        );
    }

    private void drawFloppyDisks(Graphics g) {

        float camX = game.getCamera().getX();
        float camY = game.getCamera().getY();

        for (FloppyDisk fd : floppyDisks) {

            // Draw the sprite
            drawItem(g, floppyDiskFrames, fd,
                    FLOPPY_DISK_WIDTH, FLOPPY_DISK_HEIGHT,
                    camX, camY);

            // Draw hitbox if enabled
            if (Game.DRAW_HITBOX) {
                drawDebugHitbox(g, fd.getHitbox(), camX, camY);
            }
        }
    }

    private void drawItem(Graphics g, BufferedImage[][] floppyDiskFrames, Item item, int floppyDiskWidth, int floppyDiskHeight, float camX, float camY) {

        int state = item.getItemState();
        int frame  = item.getAnimationIndex();

        int screenX = (int) (item.getX() - camX);
        int screenY = (int) (item.getY() - camY);

        g.drawImage(floppyDiskFrames[state][frame], screenX, screenY, floppyDiskWidth, floppyDiskHeight, null);


    }

    private boolean isPlayerWithinInteractionRange(Player player, Item item) {
        float dx = player.getHitbox().x - item.getHitbox().x;
        float dy = player.getHitbox().y - item.getHitbox().y;
        return Math.sqrt(dx * dx + dy * dy) < 120f;
    }
}
