package levels.objects;

import java.awt.geom.Rectangle2D;

public class Platform {

    private Rectangle2D.Float box;

    public Platform(float x, float y, float width, float height) {
        this.box = new Rectangle2D.Float(x, y, width, height);
    }

    public Rectangle2D.Float getBox() {
        return box;
    }
}
