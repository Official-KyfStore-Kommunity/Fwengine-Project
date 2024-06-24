package src;

import javax.swing.SwingUtilities;

public class engine {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OpenProjectLauncher();
        });
    }
}
