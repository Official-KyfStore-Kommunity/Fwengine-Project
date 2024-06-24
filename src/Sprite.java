package src;
import java.awt.image.BufferedImage;

public class Sprite {
    BufferedImage image;
    int x, y;

    public Sprite(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }
}
