package src;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class SpriteDrawer extends JPanel {
    private BufferedImage sprite;

    public SpriteDrawer(String filePath) {
        try {
            sprite = ImageIO.read(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sprite != null) {
            // Draw the image at (x, y) coordinates (50, 50) for this example
            g.drawImage(sprite, 50, 50, this);
        }
    }
}
