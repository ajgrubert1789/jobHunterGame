package levels.objects;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Ladder {

    private Rectangle2D.Float box;

    public Ladder(float x, float y, float width, float height) {
        this.box = new Rectangle2D.Float(x, y, width, height);
    }

    public Rectangle2D.Float getBox() {
        return box;
    }


}
