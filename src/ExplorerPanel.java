package src;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ExplorerPanel extends JPanel {

    private JList<String> fileList;
    private DefaultListModel<String> listModel;

    public ExplorerPanel(String projectPath, String headerTitle) {
        setLayout(new BorderLayout());
        setBackground(Color.darkGray);

        JLabel titleLabel = new JLabel(headerTitle);
        titleLabel.setForeground(Color.white);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setBackground(Color.darkGray);
        fileList.setForeground(Color.white);
        fileList.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        loadProjectFiles(projectPath);
    }

    private void loadProjectFiles(String projectPath) {
        File projectDirectory = new File(projectPath);
        if (projectDirectory.exists() && projectDirectory.isDirectory()) {
            File[] files = projectDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.endsWith(".csx") || fileName.endsWith(".fesprite") || fileName.endsWith(".py"))
                        {
                            listModel.addElement(file.getName());
                        }
                    }
                }
            }
        }
    }

    public String getSelectedFile() {
        return fileList.getSelectedValue();
    }
}