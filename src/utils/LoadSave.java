package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadSave {

    public static final String PLAYER_ATLAS = "res/player_sprites.png";
    public static final String LEVEL_ATLAS = "res/level_1.png";
    public static final String ROBOT_SPRITE = "res/robot_sprites.png";
    public static final String DAVE_SPRITE = "res/dave_the_manager_sprites.png";

    public static final String MENU_BUTTONS = "res/button_atlas.png";

    public static final String LEVEL_BACKGROUND = "res/level_background.png";
    public static final String MENU_BACKGROUND = "res/menu_background.png";

    public static final String FLOPPY_DISK_SPRITE = "res/floppy_disk.png";





    public static BufferedImage GetSpriteAtlas(String filename){
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" +  filename);
        try {

            img = ImageIO.read(is);



        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite", e);
        }finally {
            try{
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return img;


    }

    public static String GetLevelData(String filename) {
        StringBuilder sb = new StringBuilder();

        try (InputStream is = LoadSave.class.getResourceAsStream("/res/" + filename)) {

            if (is == null) {
                throw new RuntimeException("Level file not found: " + filename);
            }

            int data;
            while ((data = is.read()) != -1) {
                sb.append((char) data);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load level data: " + filename, e);
        }

        return sb.toString();
    }

}
