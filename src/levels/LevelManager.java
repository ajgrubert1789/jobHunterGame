package levels;

import levels.objects.Ladder;
import levels.objects.Platform;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    /* -------------------------------------------------------------
       Constants
       ------------------------------------------------------------- */
    public static final int LEVEL_WIDTH = 64;
    public static final int LEVEL_HEIGHT = 64;

    /* -------------------------------------------------------------
       Fields
       ------------------------------------------------------------- */
    private final Game game;
    private BufferedImage backgroundImage;
    private int[][] levelData;

    private List<Point> robotSpawns = new ArrayList<>();
    private ArrayList<Point> daveSpawns = new ArrayList<>();
    private ArrayList<Point> floppyDiskSpawns = new ArrayList<>();
    private ArrayList<Ladder> ladders = new ArrayList<>();
    private ArrayList<Platform> platforms = new ArrayList<>();

    /* -------------------------------------------------------------
       Constructor
       ------------------------------------------------------------- */
    public LevelManager(Game game) {
        this.game = game;

        loadBackground();
        loadCollisionLayer();
        loadEnemySpawns();
        loadDaveSpawns();
        loadItemSpawns();
        loadLadders();
        loadPlatforms();
    }

    /* -------------------------------------------------------------
       Loading Logic
       ------------------------------------------------------------- */
    private void loadBackground() {
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_BACKGROUND);
    }

    private String extractCSV(String tmx) {
        final String tag = "<data encoding=\"csv\">";
        int start = tmx.indexOf(tag) + tag.length();
        int end = tmx.indexOf("</data>", start);
        if (start == -1 || end == -1) return "";
        return tmx.substring(start, end).trim();
    }

    private void loadCollisionLayer() {
        String file = LoadSave.GetLevelData("Level.tmx");
        String csv = extractCSV(file);
        if (csv.isEmpty()) return;

        String[] rows = csv.split("\n");
        int height = rows.length;
        levelData = new int[height][LEVEL_WIDTH];

        for (int y = 0; y < height; y++) {
            String[] cols = rows[y].replace("\r", "").trim().split(",");
            for (int x = 0; x < LEVEL_WIDTH; x++) {
                if (x < cols.length && !cols[x].trim().isEmpty()) {
                    levelData[y][x] = Integer.parseInt(cols[x].trim());
                } else {
                    levelData[y][x] = 0;
                }
            }
        }
    }

    private void loadEnemySpawns() {
        parseObjects("robot", robotSpawns);
    }

    private void loadDaveSpawns() {
        parseObjects("dave", daveSpawns);
    }

    private void loadItemSpawns() {
        parseObjects("floppy disk", floppyDiskSpawns);
    }

    // Helper to avoid repeating TMX parsing logic
    private void parseObjects(String name, List<Point> list) {
        String tmx = LoadSave.GetLevelData("Level.tmx");
        int searchPos = 0;
        while (true) {
            int start = tmx.indexOf("<object ", searchPos);
            if (start == -1) break;
            int end = tmx.indexOf("/>", start);
            if (end == -1) end = tmx.indexOf("</object>", start);

            String obj = tmx.substring(start, end);
            if (obj.toLowerCase().contains("name=\"" + name.toLowerCase() + "\"")) {
                try {
                    int x = (int) (Float.parseFloat(extractAttribute(obj, "x=\"")) * Game.SCALE);
                    int y = (int) (Float.parseFloat(extractAttribute(obj, "y=\"")) * Game.SCALE);
                    list.add(new Point(x, y));
                } catch (Exception e) {
                    System.out.println("Error parsing object: " + name);
                }
            }
            searchPos = end;
        }
    }

    private String extractAttribute(String xml, String attr) {
        int start = xml.indexOf(attr) + attr.length();
        int end = xml.indexOf("\"", start);
        return xml.substring(start, end);
    }

    private void loadLadders() { /* Implement TMX object parsing for ladders if needed */ }
    private void loadPlatforms() { /* Implement TMX object parsing for platforms if needed */ }

    /* -------------------------------------------------------------
       Drawing
       ------------------------------------------------------------- */
    public void draw(Graphics g, float camX, float camY) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage,
                    (int) -camX,
                    (int) -camY,
                    (int) (backgroundImage.getWidth() * Game.SCALE),
                    (int) (backgroundImage.getHeight() * Game.SCALE),
                    null);
        }


    }

    public void drawGrid(Graphics g, float camX, float camY) {
        g.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
        int scaledTileSize = (int) (16 * Game.SCALE);

        // Draw vertical lines
        for (int x = 0; x <= LEVEL_WIDTH; x++) {
            int xPos = (int) (x * scaledTileSize - camX);
            g.drawLine(xPos, (int)-camY, xPos, (int)(LEVEL_HEIGHT * scaledTileSize - camY));
        }

        // Draw horizontal lines
        for (int y = 0; y <= LEVEL_HEIGHT; y++) {
            int yPos = (int) (y * scaledTileSize - camY);
            g.drawLine((int)-camX, yPos, (int)(LEVEL_WIDTH * scaledTileSize - camX), yPos);
        }

        // Optional: Highlight solid tiles (collision blocks)
        g.setColor(new Color(0, 255, 0, 80)); // Semi-transparent green
        for (int y = 0; y < levelData.length; y++) {
            for (int x = 0; x < levelData[y].length; x++) {
                if (levelData[y][x] != 0) {
                    g.fillRect(
                            (int) (x * scaledTileSize - camX),
                            (int) (y * scaledTileSize - camY),
                            scaledTileSize,
                            scaledTileSize
                    );
                }
            }
        }
    }

    public boolean isSolidPixel(float x, float y) {
        // 1. Convert pixel coordinates to grid coordinates (tiles)
        // We divide by (TILE_SIZE * Game.SCALE) because levelData is based on the 64x64 grid
        float scaledTileSize = 16 * Game.SCALE;

        int xIndex = (int) (x / scaledTileSize);
        int yIndex = (int) (y / scaledTileSize);

        // 2. Check if the point is outside the level boundaries
        if (xIndex < 0 || xIndex >= LEVEL_WIDTH) return true;
        if (yIndex < 0 || yIndex >= LEVEL_HEIGHT) return true;

        // 3. Check the value in levelData
        // Assuming 0 is empty space and anything else is a wall
        int value = levelData[yIndex][xIndex];
        return value != 0;
    }


    /* -------------------------------------------------------------
       Getters
       ------------------------------------------------------------- */
    public int[][] getLevelData() { return levelData; }
    public List<Point> getRobotSpawns() { return robotSpawns; }
    public ArrayList<Point> getDaveSpawns() { return daveSpawns; }
    public ArrayList<Point> getFloppyDiskSpawns() { return floppyDiskSpawns; }
    public ArrayList<Ladder> getLadders() { return ladders; }
    public ArrayList<Platform> getPlatforms() { return platforms; }

    public void update() {
    }
}
