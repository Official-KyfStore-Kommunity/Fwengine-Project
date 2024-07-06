package src;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.*;

import res.__info__;

import java.net.*;

public class OpenProjectLauncher extends JFrame implements ActionListener{
    static JFrame frame = new JFrame();
    boolean cbPressed = false;
    boolean obPressed = false;

    static int screenWidth = 735;
    static int screenHeight = 490;
    String screenTitle = "Fwengine Project Launcher";

    static String envSL = "csharp";
    static String envSI = "assets/images/defaultSprite1.png";

    static String createProjectEnvFile = String.format("""
            SL=%s
            SI=%s
            """, envSL, envSI);
    
    
    // Constants

    public static final String DefaultData = """
            SceneName = "SampleScene"
            SceneSprites = "None"
            SceneScripts = "None"
            """;

    String version = new __info__().version;
    String description = new __info__().description;

    static Log logger = new Log();
    static SpritePanel logoPanel = new SpritePanel();

    static String projectDependencies = "NONE";

    // Actual Launcher
    public OpenProjectLauncher()
    {

        JMenuBar TopMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenu pluginMenu = new JMenu("Plugins");
        //JMenu editMenu = new JMenu("Edit");
        TopMenu.add(fileMenu);
        TopMenu.add(helpMenu);
        TopMenu.add(pluginMenu);
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

        JMenuItem listPluginButton = new JMenuItem("Show All Plugins");
        pluginMenu.add(listPluginButton);
        JMenuItem seePluginInfo = new JMenuItem("See Plugin Info");
        pluginMenu.add(seePluginInfo);
        JMenuItem refreshPlugins = new JMenuItem("Refresh Plugins");
        pluginMenu.add(refreshPlugins);
        JMenuItem removePluginButton = new JMenuItem("Remove Plugin");
        pluginMenu.add(removePluginButton);

        Path pluginsDirectory = Paths.get("plugins");
        if (!Files.exists(Paths.get(pluginsDirectory.toFile().getAbsolutePath())))
        {
            pluginsDirectory.toFile().mkdir();
        }

        versionButton.addActionListener(e -> optionVersionPanel());
        websiteButton.addActionListener(e -> openDOCSWebsite());
        descriptionButton.addActionListener(e -> JOptionPane.showMessageDialog(null, description, "Fwengine Description", JOptionPane.INFORMATION_MESSAGE));

        listPluginButton.addActionListener(e -> listPlugins());
        refreshPlugins.addActionListener(e -> SwingUtilities.invokeLater(OpenProjectLauncher::new));
        removePluginButton.addActionListener(e -> removePlugin());
        seePluginInfo.addActionListener(e -> checkPluginInfo());

        createProjectButton.addActionListener(e -> createProject());
        openProjectButton.addActionListener(e -> openProject());
        deleteProjectButton.addActionListener(e -> deleteProject());
        quitLauncherButton.addActionListener(e -> closeApp());

        logoPanel.setBounds(screenWidth / 2 - 270, screenHeight / 2 - 275, 512, 512);
        logoPanel.setOpaque(false);
        logoPanel.addSpriteAtCenter("assets/images/fwengineLogo.png");
        logoPanel.revalidate();
        logoPanel.repaint();
        
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

        checkAndRunPlugins();
    }

    public void checkPluginInfo()
    {
        File pluginFile = new File(Paths.get("plugins").toFile().getAbsolutePath());
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Choose Fwengine Plugin");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(pluginFile);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFolder = fileChooser.getSelectedFile();
            if (isValidPluginFormat(selectedFolder) && isValidPluginCode(selectedFolder))
            {
                File pluginJsonFile = new File(selectedFolder + File.separator + "plugin.json");
                if (Files.exists(Paths.get(pluginJsonFile.toString())))
                {
                    database db = new database();
                    String jsonContent = db.dbRead(Paths.get(pluginJsonFile.toString()));
                    HashMap<String, String> jsonMap = parseJSON(jsonContent);
                    String pluginName = jsonMap.get("name");
                    String pluginAuthor = jsonMap.get("author");
                    String pluginDescription = jsonMap.get("description");
                    if (pluginDescription == null)
                    {
                        pluginDescription = "The Plugin's Description Could Not Be Found";
                    }
                    JOptionPane.showMessageDialog(null, String.format("""
                            Plugin Info:
                            \tPlugin Name: %s
                            \tPlugin Author: %s
                            \tPlugin Description: %s
                            """, pluginName, pluginAuthor, pluginDescription), "Plugin Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, String.format("Plugin File: \"%s\" has been compiled incorrectly. Please check the plugin is not outdated or no longer working.", pluginFile.getName()), "Plugin Parsing Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {  }
    }

    public static HashMap<String, String> parseJSON(String json) {
        HashMap<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1); // Remove the curly braces
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            boolean inKey = true;
            boolean inQuotes = false;
            boolean isEscaped = false;
            char currentQuote = ' ';

            for (char c : json.toCharArray()) {
                if (isEscaped) {
                    if (inKey) {
                        key.append(c);
                    } else {
                        value.append(c);
                    }
                    isEscaped = false;
                } else if (c == '\\') {
                    isEscaped = true;
                } else if (c == '"' || c == '\'') {
                    if (inQuotes) {
                        if (c == currentQuote) {
                            inQuotes = false;
                            if (!inKey) {
                                map.put(key.toString().trim(), value.toString().trim());
                                key = new StringBuilder();
                                value = new StringBuilder();
                                inKey = true;
                            }
                        } else {
                            if (inKey) {
                                key.append(c);
                            } else {
                                value.append(c);
                            }
                        }
                    } else {
                        inQuotes = true;
                        currentQuote = c;
                    }
                } else if (c == ':' && !inQuotes) {
                    inKey = false;
                } else if (c == ',' && !inQuotes) {
                    inKey = true;
                } else {
                    if (inKey) {
                        key.append(c);
                    } else {
                        value.append(c);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid JSON string");
        }
        return map;
    }

    public void checkAndRunPlugins()
    {
        File pluginFile = new File(Paths.get("plugins").toFile().getAbsolutePath());
        File[] plugins = pluginFile.listFiles();
        if (plugins != null && plugins.length != 0) {
            for (File plugin : plugins) {
                runPlugin(plugin);
            }
        } else {
            logger.print("No plugins found");
        }
    }

    public void runPlugin(File pluginFile)
    {
        if (isValidPluginFormat(pluginFile))
        {
            if (!isValidPluginCode(pluginFile))
            {
                JOptionPane.showMessageDialog(null, String.format("Plugin File: \"%s\" has been compiled incorrectly. Please check the plugin is not outdated or no longer working.", pluginFile.getName()), "Plugin Parsing Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void listPlugins()
    {
        File pluginFile = new File(Paths.get("plugins").toFile().getAbsolutePath());
        File[] plugins = pluginFile.listFiles();
        if (plugins != null && plugins.length != 0) {
            StringBuilder pluginList = new StringBuilder("Current Plugins:\n");
            boolean foundPluginDirectory = false;
            for (File plugin : plugins) {
                if (plugin.isDirectory()) {
                    if (isValidPluginFormat(plugin)) {
                        pluginList.append(plugin.getName()).append("\n");
                        foundPluginDirectory = true;
                    }
                }
            }
            if (foundPluginDirectory) {
                JOptionPane.showMessageDialog(null, pluginList.toString(), "Plugin Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Currently, no plugins are enabled on Fwengine.", "Plugin Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Currently, no plugins are enabled on Fwengine.", "Plugin Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void removePlugin()
    {
        File pluginFile = new File(Paths.get("plugins").toFile().getAbsolutePath());
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Remove Plugin");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(pluginFile);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            boolean deletedDirectory = deleteDirectory(fileChooser.getSelectedFile());
            if (!deletedDirectory)
            {
                JOptionPane.showMessageDialog(null, "Could not delete plugin: " + fileChooser.getSelectedFile().getName(), "Plugin Deletion Error", JOptionPane.ERROR_MESSAGE);
            } else {
                SwingUtilities.invokeLater(OpenProjectLauncher::new);
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {  }
    }

    public static boolean isValidPluginFormat(File plugin)
    {
        if (!plugin.isDirectory()) {
            return false;
        }
        File pluginJson = new File(plugin, "plugin.json");
        if (!pluginJson.isFile()) {
            return false;
        }
        File attributesDir = new File(plugin, "attributes");
        if (!attributesDir.isDirectory()) {
            return false;
        }
        File propertiesJson = new File(attributesDir, "properties.json");
        if (!propertiesJson.isFile())
        {
            return false;
        }
        File projectJson = new File(attributesDir, "project.json");
        if (!projectJson.isFile())
        {
            return false;
        }
        return true;
    }

    public static boolean isValidPluginCode(File plugin) {
        if (isValidPluginFormat(plugin)) {
            File jsonPluginFile = new File(plugin, "plugin.json");
            database db = new database();
            String jsonContent = db.dbRead(Paths.get(jsonPluginFile.toString()));
            
            String targetName = plugin.getName();
            String nameField = "\"name\":";
            String authorField = "\"author\":";
            
            int nameStartIndex = jsonContent.indexOf(nameField);
            int authorStartIndex = jsonContent.indexOf(authorField);
            
            if (nameStartIndex != -1 && authorStartIndex != -1) {
                int valueStartIndex = jsonContent.indexOf("\"", nameStartIndex + nameField.length()) + 1;
                int valueEndIndex = jsonContent.indexOf("\"", valueStartIndex);
                String nameValue = jsonContent.substring(valueStartIndex, valueEndIndex);
                
                valueStartIndex = jsonContent.indexOf("\"", authorStartIndex + authorField.length()) + 1;
                valueEndIndex = jsonContent.indexOf("\"", valueStartIndex);
                String authorValue = jsonContent.substring(valueStartIndex, valueEndIndex);
                
                boolean nameIsEqual = targetName.equals(nameValue);
    
                if (nameIsEqual && !authorValue.equals("")) {
                    HashMap<String, String> fwengineKeys = new HashMap<>();
                    fwengineKeys.put("background-img", "assets\\images\\fwengineLogo.png");
                    fwengineKeys.put("force-theme", "dark");
    
                    File propertiesJson = new File(plugin, "attributes\\properties.json");
                    jsonContent = db.dbRead(Paths.get(propertiesJson.toString()));
    
                    for (Map.Entry<String, String> entry : fwengineKeys.entrySet()) {
                        String key = entry.getKey();
                        String jsonField = "\"" + key + "\":";
            
                        int startIndex = jsonContent.indexOf(jsonField);
                        if (startIndex != -1) {
                            valueStartIndex = jsonContent.indexOf("\"", startIndex + jsonField.length()) + 1;
                            valueEndIndex = jsonContent.indexOf("\"", valueStartIndex);
                            String value = jsonContent.substring(valueStartIndex, valueEndIndex);
                            fwengineKeys.put(key, value);
                        }
                    }
                    parsePluginCode(fwengineKeys, plugin);
                    return true;
                } else {
                    return nameIsEqual;
                }
            }
            return false;
        } else {
            return false;
        }
    }    

    public static void parsePluginCode(HashMap<String, String> keysAndValues, File plugin)
    {
        for (Map.Entry<String, String> entry : keysAndValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("force-theme")) {
                if (value.equals("dark"))
                {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("light")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("red")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("blue")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("magenta")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("yellow")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("green")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("orange")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("dark-red")) {
                    setFrameBackgroundTheme(value);
                } else if (value.equals("dark-blue")) {
                    setFrameBackgroundTheme(value);
                }
            }
            if (key.equals("background-img"))
            {
                Path imageFilePath = Paths.get(value);
                if (!Files.exists(imageFilePath))
                {
                    JOptionPane.showMessageDialog(null, "The IMG Filepath, \"" + value + "\" does not exist.");
                } else {
                    frame.remove(logoPanel);
                    frame.revalidate();
                    frame.repaint();
                    logoPanel = new SpritePanel();
                    logoPanel.setBounds(screenWidth / 2 - 270, screenHeight / 2 - 275, 512, 512);
                    logoPanel.setOpaque(false);
                    logoPanel.addSpriteAtCenter(value);
                    logoPanel.revalidate();
                    logoPanel.repaint();
                    int[] givenImageSize = getImageSize(new File(value));
                    int sizeX = givenImageSize[0];
                    int sizeY = givenImageSize[1];
                    if (sizeX < 300 || sizeY < 300)
                    {
                        JOptionPane.showMessageDialog(null, "Image Size May Only Be 510x510 or bigger to display on screen.", "Image Size Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        frame.add(logoPanel);
                        frame.revalidate();
                        frame.repaint();
                    }
                }
            }
        }
        parsePluginProjectJSON(plugin);
    }

    public static void parsePluginProjectJSON(File plugin)
    {
        Path projectJSONPath = Paths.get(plugin.getAbsolutePath() + File.separator + "attributes" + File.separator + "project.json");
        if (isValidPluginFormat(plugin))
        {
            database db = new database();
            String jsonContent = db.dbRead(projectJSONPath);
            
            String targetName = "true";
            String nameField = "\"isBeingUsed\":";
            int nameStartIndex = jsonContent.indexOf(nameField);
            if (nameStartIndex != -1) {
                int valueStartIndex = jsonContent.indexOf("\"", nameStartIndex + nameField.length()) + 1;
                int valueEndIndex = jsonContent.indexOf("\"", valueStartIndex);
                String nameValue = jsonContent.substring(valueStartIndex, valueEndIndex);

                boolean nameIsEqual = targetName.equals(nameValue);
                if (nameIsEqual)
                {
                    HashMap<String, String> fwengineKeys = new HashMap<>();
                    fwengineKeys.put("scripting-language", "csharp");
                    fwengineKeys.put("sprite-img", "assets/images/defaultSprite1.png");

                    File propertiesJson = new File(plugin, "attributes\\project.json");
                    jsonContent = db.dbRead(Paths.get(propertiesJson.toString()));

                    for (Map.Entry<String, String> entry : fwengineKeys.entrySet()) {
                        String key = entry.getKey();
                        String jsonField = "\"" + key + "\":";
            
                        int startIndex = jsonContent.indexOf(jsonField);
                        if (startIndex != -1) {
                            valueStartIndex = jsonContent.indexOf("\"", startIndex + jsonField.length()) + 1;
                            valueEndIndex = jsonContent.indexOf("\"", valueStartIndex);
                            String value = jsonContent.substring(valueStartIndex, valueEndIndex);
                            fwengineKeys.put(key, value);
                        }
                    }
                    String newScriptingLanguage = fwengineKeys.get("scripting-language");
                    String newSpriteImage = fwengineKeys.get("sprite-img");
                    if (!newScriptingLanguage.equals("csharp"))
                    {
                        if (!newScriptingLanguage.equals(""))
                        {
                            if (newScriptingLanguage.equals("python"))
                            {
                                envSL = "python";
                                createProjectEnvFile = String.format("""
                                        SL=%s
                                        SI=%s
                                        """, envSL, envSI);
                            } else {
                                JOptionPane.showMessageDialog(null, String.format("Sorry, but the coding language: \"%s\" is not supported for Fwengine.", fwengineKeys.get("scripting-language")), "Scripting Language Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, String.format("Sorry, but the coding language may not be an empty string.", fwengineKeys.get("scripting-language")), "Scripting Language Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    if (Files.exists(Paths.get(newSpriteImage)))
                    {
                        envSI = newSpriteImage;
                        createProjectEnvFile = String.format("""
                                SL=%s
                                SI=%s
                                """, envSL, envSI);
                    }
                }
            }
        }
    }

    public static int[] getImageSize(File imageFile)
    {
        int[] imageDimenstions = new int[2];
        try {
            // Load the image
            BufferedImage image = ImageIO.read(imageFile);

            imageDimenstions[0] = image.getWidth();
            imageDimenstions[1] = image.getHeight();

        } catch (IOException e) {
            logger.error(e, 1);
        }
        return imageDimenstions;
    }

    public static void setFrameBackgroundTheme(String theme)
    {
        if (theme.equals("light"))
        {
            frame.getContentPane().setBackground(Color.WHITE);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("dark")){
            frame.getContentPane().setBackground(Color.darkGray);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("red")){
            frame.getContentPane().setBackground(Color.red);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("blue")){
            frame.getContentPane().setBackground(Color.blue);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("magenta")){
            frame.getContentPane().setBackground(Color.magenta);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("yellow")){
            frame.getContentPane().setBackground(Color.yellow);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("green")){
            frame.getContentPane().setBackground(Color.green);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("orange")){
            frame.getContentPane().setBackground(Color.orange);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("dark-red")){
            frame.getContentPane().setBackground(new Color(150, 6, 11));
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else if (theme.equals("dark-blue")){
            frame.getContentPane().setBackground(new Color(0, 43, 112));
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else {
            logger.print("Invalid Color Theme");
        }
    }

    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
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
                    logger.print("Browse action is not supported on this platform.");
                }
            } else {
                logger.print("Desktop is not supported on this platform.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                logger.print(line);
            }

            int exitCode = process.waitFor();
            logger.print("Exited with code: " + exitCode);

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
        } catch (NoSuchFileException e) {
        } catch (DirectoryNotEmptyException e) {
        } catch (IOException e) {
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
                logger.print(line);
            }

            int exitCode = process.waitFor();
            logger.print("Exited with code: " + exitCode);

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
                logger.print(line);
            }

            int exitCode = process.waitFor();
            logger.print("Exited with code: " + exitCode);

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
                Path ParentFolder = file.toPath().getParent();
                Path binDirectory = Paths.get(ParentFolder.toString() + File.separator + "bin");
                boolean hasDeleted = deleteDirectory(binDirectory.toFile());
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

    public static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                 .sorted(Comparator.reverseOrder())
                 .forEach(p -> {
                     try {
                         Files.delete(p);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
        }
    }

    public void OpenProject(String NameOfProject, File filePath)
    {
        DebinaryData(NameOfProject, filePath);
        ProjectDB ofp = new ProjectDB();
        Path directoryPath = Paths.get(filePath.toString());
        Path envFile = Paths.get(directoryPath.toFile().toString() + File.separator + "project.env");
        try 
        {
            Files.write(envFile, createProjectEnvFile.getBytes());
        } catch (IOException e)
        {
            logger.error(e, 1);
        }
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
                logger.print(line);
            }

            int exitCode = process.waitFor();
            logger.print("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
