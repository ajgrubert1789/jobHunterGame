    package levels;

    import levels.objects.Ladder;
    import levels.objects.Platform;
    import main.Game;
    import utils.LoadSave;

    import java.awt.*;
    import java.awt.geom.Rectangle2D;
    import java.awt.image.BufferedImage;
    import java.util.ArrayList;

    public class LevelManager {

        /* -------------------------------------------------------------
           Constants
           ------------------------------------------------------------- */

        private static final int TILE_SIZE = 16;   // TMX tile size
        public static final int LEVEL_WIDTH = 64;
        public static final int LEVEL_HEIGHT = 64;

        private static final int BACKGROUND_OFFSET_Y = 0;



        private java.util.List<Point> robotSpawns = new java.util.ArrayList<>();
        private ArrayList<Point> daveSpawns = new ArrayList<>();
        private ArrayList<Point> floppyDiskSpawns = new ArrayList<>();




        /* -------------------------------------------------------------
           Fields
           ------------------------------------------------------------- */

        private final Game game;

        private BufferedImage[] levelSprites;
        private BufferedImage backgroundImage;
        private int[][] levelData;


        private ArrayList<Ladder> ladders = new ArrayList<>();

        private ArrayList<Platform> platforms = new ArrayList<>();

        public ArrayList<Ladder> getLadders() {
            return ladders;
        }



        /* -------------------------------------------------------------
           Constructor
           ------------------------------------------------------------- */

        public LevelManager(Game game) {
            this.game = game;

            loadBackground();
            loadTileAtlas();
            loadCollisionLayer();
            loadEnemySpawns();
            loadDaveSpawns();
            loadItemSpawns();
            loadLadders();
            loadPlatforms();





        }

        /* -------------------------------------------------------------
           Loading
           ------------------------------------------------------------- */

        private void loadBackground() {

//            backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);

            backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_BACKGROUND);
        }

        private void loadTileAtlas() {
            BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);



            System.out.println("Background size = " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());

            int tilesPerRow = atlas.getWidth() / TILE_SIZE;
            int tilesPerCol = atlas.getHeight() / TILE_SIZE;

            levelSprites = new BufferedImage[tilesPerRow * tilesPerCol];

            int index = 0;
            for (int y = 0; y < tilesPerCol; y++) {
                for (int x = 0; x < tilesPerRow; x++) {
                    levelSprites[index++] = atlas.getSubimage(
                            x * TILE_SIZE,
                            y * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                }
            }


        }

        public ArrayList<Platform> getPlatforms() {
            return platforms;
        }

        private String extractCSV(String tmx) {
            final String tag = "<data encoding=\"csv\">";
            int start = tmx.indexOf(tag) + tag.length();
            int end = tmx.indexOf("</data>", start);
            return tmx.substring(start, end).trim();
        }

        private void loadCollisionLayer() {
            String file = LoadSave.GetLevelData("Level.tmx");
            String csv = extractCSV(file);

            String[] rows = csv.split("\n");
            int height = rows.length;

            levelData = new int[height][LEVEL_WIDTH];

            for (int y = 0; y < height; y++) {
                String[] cols = rows[y].replace("\r", "").trim().split(",");

                for (int x = 0; x < LEVEL_WIDTH; x++) {
                    if (x >= cols.length || cols[x].trim().isEmpty()) {
                        levelData[y][x] = 0;
                    } else {
                        levelData[y][x] = Integer.parseInt(cols[x].trim());
                    }
                }
            }
            System.out.println("Loaded level height = " + height);
        }

        private void loadEnemySpawns() {
            String tmx = LoadSave.GetLevelData("Level.tmx");
            robotSpawns.clear();

            int searchPos = 0;
            while (true) {
                int groupStart = tmx.indexOf("<objectgroup", searchPos);
                if (groupStart == -1) break;

                int groupEnd = tmx.indexOf("</objectgroup>", groupStart);
                if (groupEnd == -1) break;

                String groupContent = tmx.substring(groupStart, groupEnd);

                String[] objects = groupContent.split("<object");
                for (String obj : objects) {
                    if (!obj.contains("name=\"robot\"")) continue;

                    int xStart = obj.indexOf("x=\"") + 3;
                    int xEnd = obj.indexOf("\"", xStart);
                    int yStart = obj.indexOf("y=\"") + 3;
                    int yEnd = obj.indexOf("\"", yStart);

                    if (xStart < 3 || yStart < 3 || xEnd == -1 || yEnd == -1) continue;

                    int x = (int) (Float.parseFloat(obj.substring(xStart, xEnd)) * Game.SCALE);
                    int y = (int) (Float.parseFloat(obj.substring(yStart, yEnd)) * Game.SCALE);


                    robotSpawns.add(new Point(x, y));
                }

                searchPos = groupEnd + "</objectgroup>".length();
            }
        }

        private void loadDaveSpawns() {
            String tmx = LoadSave.GetLevelData("Level.tmx");
            daveSpawns.clear();

            int searchPos = 0;
            while (true) {
                int groupStart = tmx.indexOf("<objectgroup", searchPos);
                if (groupStart == -1) break;

                int groupEnd = tmx.indexOf("</objectgroup>", groupStart);
                if (groupEnd == -1) break;

                String groupContent = tmx.substring(groupStart, groupEnd);

                String[] objects = groupContent.split("<object");
                for (String obj : objects) {
                    if (!obj.toLowerCase().contains("name=\"dave\"")) continue;

                    int xStart = obj.indexOf("x=\"") + 3;
                    int xEnd = obj.indexOf("\"", xStart);
                    int yStart = obj.indexOf("y=\"") + 3;
                    int yEnd = obj.indexOf("\"", yStart);

                    if (xStart < 3 || yStart < 3 || xEnd == -1 || yEnd == -1) continue;

                    // Inside loadEnemySpawns / loadDaveSpawns
                    int x = (int) (Float.parseFloat(obj.substring(xStart, xEnd)) * Game.SCALE);
                    int y = (int) (Float.parseFloat(obj.substring(yStart, yEnd)) * Game.SCALE);

                    daveSpawns.add(new Point(x, y));
                }

                searchPos = groupEnd + "</objectgroup>".length();
            }
        }

        private void loadItemSpawns() {
            String tmx = LoadSave.GetLevelData("Level.tmx");
            floppyDiskSpawns.clear();

            int searchPos = 0;
            while (true) {
                int groupStart = tmx.indexOf("<objectgroup", searchPos);
                if (groupStart == -1) break;

                int groupEnd = tmx.indexOf("</objectgroup>", groupStart);
                if (groupEnd == -1) break;

                String groupContent = tmx.substring(groupStart, groupEnd);

                String[] objects = groupContent.split("<object");
                for (String obj : objects) {
                    if (!obj.toLowerCase().contains("name=\"floppy disk\"")) continue;

                    int xStart = obj.indexOf("x=\"") + 3;
                    int xEnd = obj.indexOf("\"", xStart);
                    int yStart = obj.indexOf("y=\"") + 3;
                    int yEnd = obj.indexOf("\"", yStart);

                    if (xStart < 3 || yStart < 3 || xEnd == -1 || yEnd == -1) continue;

                    // Inside loadEnemySpawns / loadDaveSpawns
                    int x = (int) (Float.parseFloat(obj.substring(xStart, xEnd)) * Game.SCALE);
                    int y = (int) (Float.parseFloat(obj.substring(yStart, yEnd)) * Game.SCALE);

                    floppyDiskSpawns.add(new Point(x, y));
                }

                searchPos = groupEnd + "</objectgroup>".length();
            }
        }

        private void loadLadders() {
            String tmx = LoadSave.GetLevelData("Level.tmx");
            ladders.clear();

            int searchPos = 0;
            while (true) {
                int groupStart = tmx.indexOf("<objectgroup", searchPos);
                if (groupStart == -1) break;

                int groupEnd = tmx.indexOf("</objectgroup>", groupStart);
                if (groupEnd == -1) break;

                String groupContent = tmx.substring(groupStart, groupEnd);

                if (!groupContent.contains("name=\"Ladder\"")) {
                    searchPos = groupEnd + "</objectgroup>".length();
                    continue;
                }

                String[] objects = groupContent.split("<object");
                for (String obj : objects) {
                    if (!obj.contains("x=\"") || !obj.contains("y=\"")) continue;

                    int xStart = obj.indexOf("x=\"") + 3;
                    int xEnd = obj.indexOf("\"", xStart);
                    int yStart = obj.indexOf("y=\"") + 3;
                    int yEnd = obj.indexOf("\"", yStart);
                    int wStart = obj.indexOf("width=\"") + 7;
                    int wEnd = obj.indexOf("\"", wStart);
                    int hStart = obj.indexOf("height=\"") + 8;
                    int hEnd = obj.indexOf("\"", hStart);

                    if (xEnd == -1 || yEnd == -1 || wEnd == -1 || hEnd == -1) continue;

                    // CRITICAL: Multiply by Game.SCALE here
                    float x = Float.parseFloat(obj.substring(xStart, xEnd)) * Game.SCALE;
                    float y = Float.parseFloat(obj.substring(yStart, yEnd)) * Game.SCALE;
                    float w = Float.parseFloat(obj.substring(wStart, wEnd)) * Game.SCALE;
                    float h = Float.parseFloat(obj.substring(hStart, hEnd)) * Game.SCALE;

                    ladders.add(new Ladder(x, y, w, h));
                }
                searchPos = groupEnd + "</objectgroup>".length();
            }
        }

        private void loadPlatforms() {
            String tmx = LoadSave.GetLevelData("Level.tmx");
            platforms.clear();

            int searchPos = 0;
            while (true) {
                int groupStart = tmx.indexOf("<objectgroup", searchPos);
                if (groupStart == -1) break;

                int groupEnd = tmx.indexOf("</objectgroup>", groupStart);
                if (groupEnd == -1) break;

                String groupContent = tmx.substring(groupStart, groupEnd);

                // Assuming your Tiled object group is named "Platform"
                if (!groupContent.contains("name=\"Platform\"")) {
                    searchPos = groupEnd + "</objectgroup>".length();
                    continue;
                }

                String[] objects = groupContent.split("<object");
                for (String obj : objects) {
                    if (!obj.contains("x=\"") || !obj.contains("y=\"")) continue;

                    int xStart = obj.indexOf("x=\"") + 3;
                    int xEnd = obj.indexOf("\"", xStart);
                    int yStart = obj.indexOf("y=\"") + 3;
                    int yEnd = obj.indexOf("\"", yStart);
                    int wStart = obj.indexOf("width=\"") + 7;
                    int wEnd = obj.indexOf("\"", wStart);
                    int hStart = obj.indexOf("height=\"") + 8;
                    int hEnd = obj.indexOf("\"", hStart);

                    if (xEnd == -1 || yEnd == -1 || wEnd == -1 || hEnd == -1) continue;

                    // CRITICAL: Multiply by Game.SCALE here
                    float x = Float.parseFloat(obj.substring(xStart, xEnd)) * Game.SCALE;
                    float y = Float.parseFloat(obj.substring(yStart, yEnd)) * Game.SCALE;
                    float w = Float.parseFloat(obj.substring(wStart, wEnd)) * Game.SCALE;
                    float h = Float.parseFloat(obj.substring(hStart, hEnd)) * Game.SCALE;

                    platforms.add(new Platform(x, y, w, h));
                }
                searchPos = groupEnd + "</objectgroup>".length();
            }
        }

        public void renderPlatforms(Graphics g, float camX, float camY) {
            g.setColor(Color.GREEN);

            for (Platform p : platforms) {
                Rectangle2D.Float box = p.getBox();

                g.drawRect(
                        (int)(box.x - camX),
                        (int)(box.y - camY),
                        (int)box.width,
                        (int)box.height
                );
            }
        }

        private void renderLadders(Graphics g, float camX, float camY) {
            g.setColor(Color.ORANGE);
            for (Ladder ladder : ladders) {
                Rectangle2D.Float box = ladder.getBox();
                g.drawRect((int)(box.x - camX), (int)(box.y - camY),
                        (int)box.width, (int)box.height);
            }
        }









        /* -------------------------------------------------------------
           Collision
           ------------------------------------------------------------- */

        public boolean isSolid(int tileX, int tileY) {
            if (tileX < 0 || tileY < 0 || tileX >= LEVEL_WIDTH || tileY >= LEVEL_HEIGHT)
                return true;

            return levelData[tileY][tileX] == 1;
        }

        public boolean isSolidPixel(float x, float y) {
            // Translate coordinate to tile index
            int tileX = (int) (x / Game.TILES_SIZE);
            int tileY = (int) (y / Game.TILES_SIZE);

            // Bounds check
            if (tileX < 0 || tileX >= LEVEL_WIDTH || tileY < 0 || tileY >= LEVEL_HEIGHT)
                return true;

            // Return true if the tile at this index is NOT empty (0)
            return levelData[tileY][tileX] != 0;
        }

        /* -------------------------------------------------------------
           Rendering
           ------------------------------------------------------------- */

        public void draw(Graphics g) {

            // 1. Clear screen to black
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            float camX = game.getCamera().getX();
            float camY = game.getCamera().getY();

            drawBackground(g, camX, camY);
            drawTiles(g, camX, camY); // Add this to see your world!

            if (Game.DRAW_HITBOX) {
                drawCollisionOverlay(g, camX, camY);
                renderLadders(g, camX, camY);
                renderPlatforms(g, camX, camY);
            }

//



        }

//        private void drawBackground(Graphics g, float camX, float camY) {
//            // Scales the background image to the full level size
//            g.drawImage(backgroundImage,
//                    (int)-camX, (int)-camY,
//                    LEVEL_WIDTH * Game.TILES_SIZE,
//                    LEVEL_HEIGHT * Game.TILES_SIZE, null);
//        }

        private void drawBackground(Graphics g, float camX, float camY) {
            int worldWidth  = LEVEL_WIDTH  * Game.TILES_SIZE;   // 64 * 48 = 3072
            int worldHeight = LEVEL_HEIGHT * Game.TILES_SIZE;   // 64 * 48 = 3072

            g.drawImage(backgroundImage,
                    (int)-camX,
                    (int)-camY - BACKGROUND_OFFSET_Y,
                    worldWidth,
                    worldHeight,
                    null);


        }

        private void drawTiles(Graphics g, float camX, float camY) {
            for (int j = 0; j < levelData.length; j++) {
                for (int i = 0; i < levelData[j].length; i++) {

                    int xPos = (int)(i * Game.TILES_SIZE - camX);
                    int yPos = (int)(j * Game.TILES_SIZE - camY);

                    if (Game.DRAW_HITBOX) {
                        // 2. Draw the Tile Border (Grid)
                        g.setColor(new Color(255, 255, 255, 50)); // Very faint white
                        g.drawRect(xPos, yPos, Game.TILES_SIZE, Game.TILES_SIZE);
                    }
                }
            }
        }



        private void drawCollisionOverlay(Graphics g, float camX, float camY) {
            g.setColor(new Color(255, 0, 0, 100)); // Transparent red
            for (int j = 0; j < levelData.length; j++) {
                for (int i = 0; i < levelData[j].length; i++) {
                    if (levelData[j][i] != 0) { // If it's a solid tile
                        g.fillRect((int)(i * Game.TILES_SIZE - camX),
                                (int)(j * Game.TILES_SIZE - camY),
                                Game.TILES_SIZE, Game.TILES_SIZE);
                    }
                }
            }
        }

        public java.util.List<Point> getRobotSpawns() {
            return robotSpawns;
        }

        public java.util.List<Point> getFloppyDiskSpawns() {
            return floppyDiskSpawns;
        }





        /* -------------------------------------------------------------
           Update
           ------------------------------------------------------------- */

        public void update() {}

        public java.util.List<Point> getDaveSpawns() {
            return daveSpawns;

        }


    }