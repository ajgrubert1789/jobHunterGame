package main;

public class Camera {

    private float xOffset;
    private float yOffset;

    private final int worldWidth;
    private final int worldHeight;
    private final int screenWidth;
    private final int screenHeight;

    public Camera(int worldWidth, int worldHeight, int screenWidth, int screenHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(float targetX, float targetY) {
        // Center camera on target
        xOffset = targetX - screenWidth / 2f;
        yOffset = targetY - screenHeight / 2f;

        clamp();
    }

    private void clamp() {
        if (xOffset < 0) xOffset = 0;
        if (yOffset < 0) yOffset = 0;

        float maxX = Math.max(0, worldWidth - screenWidth);
        float maxY = Math.max(0, worldHeight - screenHeight);

        if (xOffset > maxX) xOffset = maxX;
        if (yOffset > maxY) yOffset = maxY;
//        System.out.println("Clamp: yOffset=" + yOffset + " maxY=" + (worldHeight - screenHeight));
    }

    public float getX() { return xOffset; }
    public float getY() { return yOffset; }

}
