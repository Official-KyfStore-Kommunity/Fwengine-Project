package src;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class SpritePanel extends JPanel {
    private List<Sprite> sprites = new ArrayList<>();

    public SpritePanel() {
        setBackground(Color.DARK_GRAY);
    }

    public void addSprite(String imagePath, int x, int y) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            sprites.add(new Sprite(image, x, y));
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSpriteAtCenter(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int centerX = (getWidth() / 2) - (image.getWidth() / 2);
            int centerY = (getHeight() / 2) - (image.getHeight() / 2);
            sprites.add(new Sprite(image, centerX, centerY));
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Point getSpriteLocation(int index) {
        if (index >= 0 && index < sprites.size()) {
            Sprite sprite = sprites.get(index);
            return new Point(sprite.x, sprite.y);
        } else {
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Sprite sprite : sprites) {
            g.drawImage(sprite.image, sprite.x, sprite.y, this);
        }
    }
}
