package utils;

import java.awt.image.BufferedImage;

public class SpriteLoader {

    private SpriteLoader() {} // prevent instantiation

    public static BufferedImage[][] loadFrames(
            BufferedImage atlas,
            int w, int h,
            int rows, int cols
    ) {
        BufferedImage[][] arr = new BufferedImage[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                arr[r][c] = atlas.getSubimage(
                        c * w,
                        r * h,
                        w,
                        h
                );
            }
        }
        return arr;
    }
}
