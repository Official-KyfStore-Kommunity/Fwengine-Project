package src;
// Imports

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import res.__info__;

import java.net.*;

public class OpenProjectLauncher extends JFrame implements ActionListener{

    // JFrame Object Requires
    JFrame frame = new JFrame();
    boolean cbPressed = false;
    boolean obPressed = false;
    
    // Screen Args
    int screenWidth = 735;
    int screenHeight = 490;
    String screenTitle = "Fwengine Project Launcher";
    
    // Constants

    public static final String DefaultData = """
            SceneName = "SampleScene"
            SceneSprites = "None"
            SceneScripts = "None"
            """;

    String version = new __info__().version;
    String description = new __info__().description;

    // Actual Launcher
    public OpenProjectLauncher()
    {

        JMenuBar TopMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        //JMenu editMenu = new JMenu("Edit");
        TopMenu.add(fileMenu);
        TopMenu.add(helpMenu);
        //TopMenu.add(editMenu);
        JMenuItem createProjectButton = new JMenuItem("Create Empty Project");
        fileMenu.add(createProjectButton);
        JMenuItem openProjectButton = new JMenuItem("Open Existing Project");
        fileMenu.add(openProjectButton);
        JMenuItem deleteProjectButton = new JMenuItem("Delete Existing Project");
        fileMenu.add(deleteProjectButton);
        JMenuItem quitLauncherButton = new JMenuItem("Quit Launcher");
        fileMenu.add(quitLauncherButton);

        JMenuItem versionButton = new JMenuItem("Version");
        helpMenu.add(versionButton);
        JMenuItem descriptionButton = new JMenuItem("Fwengine Description");
        helpMenu.add(descriptionButton);
        JMenuItem websiteButton = new JMenuItem("Learn More");
        helpMenu.add(websiteButton);

        versionButton.addActionListener(e -> optionVersionPanel());
        websiteButton.addActionListener(e -> openDOCSWebsite());
        descriptionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, description, "Fwengine Description", JOptionPane.INFORMATION_MESSAGE);
        });

        createProjectButton.addActionListener(e -> createProject());
        openProjectButton.addActionListener(e -> openProject());
        deleteProjectButton.addActionListener(e -> deleteProject());
        quitLauncherButton.addActionListener(e -> closeApp());

        SpritePanel logoPanel = new SpritePanel();
        logoPanel.setBounds(screenWidth / 2 - 270, screenHeight / 2 - 275, 512, 512);
        logoPanel.setBackground(Color.darkGray);
        logoPanel.addSpriteAtCenter("assets/images/fwengineLogo.png");
        logoPanel.revalidate();
        logoPanel.repaint();


        //JMenuItem preferencesMenu = new JMenuItem("Preferences");
        //editMenu.add(preferencesMenu);

        //preferencesMenu.addActionListener(e -> OpenPreferences());
        
        frame.setJMenuBar(TopMenu);

        frame.add(logoPanel);

        // Frame Args
        ImageIcon imageIcon = new ImageIcon("assets/images/fwengineLogo.png");
        Image image = imageIcon.getImage();
        frame.setIconImage(image);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenWidth, screenHeight);
        frame.setResizable(false);
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setBackground(Color.darkGray);
        frame.setTitle(screenTitle);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

    }

    public void closeApp()
    {
        frame.dispose();
        System.exit(0);
    }

    public void optionVersionPanel()
    {
        JOptionPane.showMessageDialog(null, "You Are Currently Running Fwengine Version: " + version, "Version Info", JOptionPane.INFORMATION_MESSAGE);
    }
    public void openDOCSWebsite()
    {
        openURIinBrowser("https://www.patreon.com/collection/598855?view=expanded");
    }
    public static void openURIinBrowser(String uri) {
        try {
            // Create a URI object from the provided string
            URI url = new URI(uri);
            
            // Check if Desktop is supported by the current platform
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                
                // Check if the browse action is supported (i.e., default browser functionality)
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(url);
                } else {
                    System.out.println("Browse action is not supported on this platform.");
                }
            } else {
                System.out.println("Desktop is not supported on this platform.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public void OpenPreferences()
    //{
    //    PreferencesDialog preferencesBox = new PreferencesDialog(frame);
    //    preferencesBox.setVisible(true);
    //}

    //private class PreferencesDialog extends JDialog {
    //    public PreferencesDialog(Frame owner) {
    //        super(owner, "Preferences", true);
//
    //        setLayout(new BorderLayout());
    //        setSize(400, 200);
    //        setLocationRelativeTo(owner);
//
    //        setBackground(Color.darkGray);
//
    //        // Add preference options here
    //        JPanel preferencesPanel = new JPanel();
    //        preferencesPanel.setLayout(new GridLayout(0, 2));
//
    //        JLabel CodeELabel = new JLabel("Code Editor: ");
    //        CodeELabel.setForeground(Color.white);
    //        preferencesPanel.add(CodeELabel);
    //        JComboBox<String> codeEComboBox = new JComboBox<>(new String[]{"Notepad", "Notepad++", "Sublime Text", "VSCode"});
    //        preferencesPanel.add(codeEComboBox);
    //        if (Files.exists(Paths.get()))
    //        {
//
    //        }
//
    //        preferencesPanel.setBackground(Color.darkGray);
//
    //        // Add Save and Cancel buttons
    //        JPanel buttonPanel = new JPanel();
    //        buttonPanel.setBackground(Color.darkGray);
    //        JButton saveButton = new JButton("Save");
    //        JButton cancelButton = new JButton("Cancel");
//
    //        buttonPanel.add(saveButton);
    //        buttonPanel.add(cancelButton);
//
    //        saveButton.addActionListener(e -> {
    //            String selectedEditor = (String) codeEComboBox.getSelectedItem();
    //            // Implement This: applyCodeEditor(selectedEditor);
    //            dispose();
    //        });
//
    //        cancelButton.addActionListener(e -> dispose());
//
    //        add(preferencesPanel, BorderLayout.CENTER);
    //        add(buttonPanel, BorderLayout.SOUTH);
    //    }
    //}

    public void BinaryData(String projectDataFile, File folderName)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/binaryData.py", projectDataFile, folderName.getAbsolutePath());

            // Redirect error stream to capture error messages
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void DeleteFileWithStringToPath(String fileString)
    {
        Path filePathFixed = Paths.get(fileString);
        try
        {
            Files.delete(filePathFixed);
            //System.out.println("Successfully deleted file!");
        } catch (NoSuchFileException e) {
            //System.err.println("No such file/directory exists");
        } catch (DirectoryNotEmptyException e) {
            //System.err.println("Directory is not empty.");
        } catch (IOException e) {
            //System.err.println("Invalid permissions.");
        }
    }

    public void DeleteFile(String fileName)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/deleteFileInCurrentDir.py", fileName);

            // Redirect error stream to capture error messages
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void DebinaryData(String projectDataFile, File filePath)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/debinaryData.py", projectDataFile, filePath.getAbsolutePath());

            // Redirect error stream to capture error messages
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void CreateDefaultFwengineProject(database db, File folderName)
    {
        int index = 1;
        String projectName = "newproject" + index + ".fedb";
        //File file = new File(projectName);
        File file = new File(folderName + File.separator + projectName);
        boolean hasCreatedFile = false;
        while (!hasCreatedFile)
        {
            if (!file.exists())
            {
                //String filePath = file.getAbsolutePath();
                Path projectPath = Paths.get(file.toString());
                db.dbWrite(DefaultData, projectPath);
                BinaryData(projectName, folderName);
                OpenProject(projectName, folderName);
                hasCreatedFile = true;
                //DeleteFile(projectName);
                break;
            } else {
                index += 1;
                projectName = "newproject" + index + ".fedb";
                file = new File(folderName.getAbsolutePath() + File.separator + projectName);
            }
        }
    }

    public void OpenProject(String NameOfProject, File filePath)
    {
        DebinaryData(NameOfProject, filePath);
        ProjectDB ofp = new ProjectDB();
        Path directoryPath = Paths.get(filePath.toString());
        String contents = ofp.openDBFile(NameOfProject, directoryPath);
        OpenProjectWithDB opwdb = new OpenProjectWithDB();
        opwdb.OpenProject(contents, filePath.toString(), NameOfProject);
        BinaryData(NameOfProject, filePath);
    }

    public void createProject()
    {
        database defaultDB = new database();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Open Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            CreateDefaultFwengineProject(defaultDB, selectedFolder);
            frame.dispose();
        } else if (result == JFileChooser.CANCEL_OPTION) {  }
    }

    public void deleteProject()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Delete Fwengine Project");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Project", "fedb");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            String absoluteFile = selectedFile.getAbsolutePath();
            String parentFolder = selectedFile.getParentFile().toString() + File.separator + "bin";
            Path binPath = Paths.get(parentFolder);
            DeleteFileWithStringToPath(absoluteFile);
            DeleteFolder(binPath);
        }
    }

    public void openProject()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Open Fwengine Project");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Project", "fedb");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            OpenProject(selectedFile.getName(), selectedFile.getParentFile());
            frame.dispose();
        }
    }

    // Button Actions
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    public void DeleteFolder(Path folderPath)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/deleteFolder.py", folderPath.toString());

            // Redirect error stream to capture error messages
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
