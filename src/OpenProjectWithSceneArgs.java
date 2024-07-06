package src;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class OpenProjectWithSceneArgs extends JFrame implements ActionListener {
    
    JMenuBar TopMenu = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");

    JMenuItem saveProjectButton = new JMenuItem("Save All");
    JMenuItem exitProjectMenu = new JMenuItem("Exit Project");
    JMenuItem buildandrunProject = new JMenuItem("Build And Run Project");

    JMenuItem sceneNameChangeItem = new JMenuItem("Change Scene Name");
    JMenuItem preferencesMenu = new JMenuItem("Preferences");

    String globalCodeEditor = "Notepad";
    Path optionsPath2;

    String scriptFileExtension = ".csx";
    String spriteFileExtension = ".fesprite";

    String scriptsData2 = "";

    Log logger = new Log();

    JLabel spriteNameLabel = new JLabel();
    JLabel spriteXLabel = new JLabel();
    JLabel spriteYLabel = new JLabel();
    JTextField spriteXField = new JTextField(2);
    JTextField spriteYField = new JTextField(2);
    JButton applySpritePropsButton = new JButton("Apply Properties");

    Path envFile;
    String scriptingLanguage = "csharp";
    String spriteImg = "assets/images/defaultSprite1.png";

    public OpenProjectWithSceneArgs() {
        TopMenu.add(fileMenu);
        TopMenu.add(editMenu);
        fileMenu.add(saveProjectButton);
        fileMenu.add(buildandrunProject);
        fileMenu.add(exitProjectMenu);
        editMenu.add(sceneNameChangeItem);
        editMenu.add(preferencesMenu);

        saveProjectButton.addActionListener(e -> saveProject());
        exitProjectMenu.addActionListener(e -> closeApp());
        buildandrunProject.addActionListener(e -> buildAndRun());
        sceneNameChangeItem.addActionListener(e -> nameSceneChange());
        preferencesMenu.addActionListener(e -> OpenPreferences());
    }

    public void OpenPreferences()
    {
        PreferencesDialog preferencesBox = new PreferencesDialog(frame);
        preferencesBox.setVisible(true);
    }

    private class PreferencesDialog extends JDialog {
        public PreferencesDialog(Frame owner) {
            super(owner, "Preferences", true);
            setLayout(new BorderLayout());
            setSize(400, 200);
            setLocationRelativeTo(owner);
            setBackground(Color.darkGray);
            // Add preference options here
            JPanel preferencesPanel = new JPanel();
            preferencesPanel.setLayout(new GridLayout(0, 2));
            JLabel CodeELabel = new JLabel("Code Editor: ");
            CodeELabel.setForeground(Color.white);
            preferencesPanel.add(CodeELabel);
            JComboBox<String> codeEComboBox = new JComboBox<>(new String[]{"Notepad", "Notepad++", "SublimeText", "VSCode"});
            preferencesPanel.add(codeEComboBox);
            database db = new database();
            String contents = db.dbRead(optionsPath2);
            String codeEditor = getCodeEditor(contents);
            boolean found = false;
            for (int i = 0; i < codeEComboBox.getItemCount(); i++) {
                if (codeEComboBox.getItemAt(i).equals(codeEditor)) {
                    found = true;
                    break;
                }
            }
            
            // Print the result
            if (found) {
                logger.print("Item '" + codeEditor + "' exists in the JComboBox.");
                codeEComboBox.setSelectedItem(codeEditor);
                globalCodeEditor = codeEditor;
            } else {
                logger.error("Item '" + codeEditor + "' does not exist in the JComboBox.", 1);
                globalCodeEditor = "Notepad";
            }

            preferencesPanel.repaint();
            preferencesPanel.revalidate();
            
            preferencesPanel.setBackground(Color.darkGray);
            // Add Save and Cancel buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.darkGray);
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            saveButton.addActionListener(e -> {
                String selectedEditor = (String) codeEComboBox.getSelectedItem();
                globalCodeEditor = selectedEditor;
                applyCodeEditor(selectedEditor);
                dispose();
            });
            cancelButton.addActionListener(e -> dispose());
            add(preferencesPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        public void applyCodeEditor(String editorOfChoice)
        {
            Path optionsPath = Paths.get(filePath2 + File.separator + "bin" + File.separator + "options.txt");
            if (!Files.exists(optionsPath))
            {
                try {
                    optionsPath.toFile().createNewFile();
                } catch(Exception e) {
                    logger.error(e, 1);
                }
            }
            database db = new database();
            db.dbWrite("codeEditor: " + editorOfChoice, optionsPath);
        }
    }

    public void closeApp()
    {
        frame.dispose();
        System.exit(0);
    }

    JFrame frame = new JFrame();
    SpritePanel sceneWindowPanel = new SpritePanel();
    JPanel propertiesPanel = new JPanel();
    ExplorerPanel explorerPanel;
    ExplorerPanel scripterPanel;
    Color extraDarkGray = new Color(40, 40, 40);

    int screenWidth = 925;
    int screenHeight = 536;
    String screenTitle = "";
    String pathOfFile = "";
    String feDBString = "";
    String feDBOriginFolder = "";
    String fileName2 = "";

    String SceneName2 = "";
    String SceneSprites2 = "";
    String SceneScripts2 = "";

    String newFileName = "";
    String filePath2 = "";

    String spritesPath2 = "";
    String scriptsPath2 = "";
    String scriptDict2 = "";

    HashMap<String, Integer> allSprites = new HashMap<>();
    HashMap<String, String> allScripts = new HashMap<>();

    public static final String DefaultData = """
        SceneName = "SampleScene"
        SceneSprites = "None"
        SceneScripts = "None"
        """;
    
    public void OpenProject(String sceneName, String sceneSprites, String sceneScripts, String filePath, String fileName)
    {
        frame.setJMenuBar(TopMenu);
        screenTitle = "Fwengine: " + sceneName;
        feDBString = filePath + File.separator + fileName;
        feDBOriginFolder = filePath;
        fileName2 = fileName;
        filePath2 = filePath;

        Path binPath = Paths.get(filePath2 + File.separator + "bin");
        Path assetPath = Paths.get(binPath + File.separator + "assets");
        Path spritesPath = Paths.get(assetPath + File.separator + "sprites");
        Path scriptsPath = Paths.get(assetPath + File.separator + "scripts");
        Path optionsPath = Paths.get(binPath + File.separator + "options.txt");
        optionsPath2 = optionsPath;
        spritesPath2 = spritesPath.toString();
        scriptsPath2 = scriptsPath.toString();

        Path scriptsData = Paths.get(scriptsPath2 + File.separator + "scripts.dat");

        scriptsData2 = scriptsData.toString();

        if (!Files.exists(binPath))
        {
            binPath.toFile().mkdir();
        }
        if (!Files.exists(assetPath))
        {
            assetPath.toFile().mkdir();
        }
        if (!Files.exists(spritesPath))
        {
            spritesPath.toFile().mkdir();
        }
        if (!Files.exists(scriptsPath))
        {
            scriptsPath.toFile().mkdir();
        }
        if (!Files.exists(optionsPath))
        {
            try {
                optionsPath.toFile().createNewFile();
                database db = new database();
                db.dbWrite("codeEditor: Notepad", optionsPath);
            } catch (IOException e) {
                logger.error(e, 1);
            }
        } else {
            database db = new database();
            String optionContents = db.dbRead(optionsPath);
            globalCodeEditor = getCodeEditor(optionContents);
        }
        if (!Files.exists(scriptsData))
        {
            try {
                scriptsData.toFile().createNewFile();
                database db = new database();
                db.dbWrite("{}", scriptsData);
            } catch (IOException e) {
                logger.error(e, 1);
            }
        }

        database db = new database();

        envFile = Paths.get(filePath + File.separator + "project.env");
        HashMap<String, String> envMap = parseEnvFile(envFile.toFile().getAbsolutePath());
        scriptingLanguage = envMap.get("SL");
        spriteImg = envMap.get("SI");
        if (scriptingLanguage.equals("csharp"))
        {
            scriptFileExtension = ".csx";
        } else if (scriptingLanguage.equals("python"))
        {
            scriptFileExtension = ".py";
        }
        
        String scriptDictStr = db.dbRead(scriptsData);
        scriptDictStr = scriptDictStr.substring(1, scriptDictStr.length() - 1);

        // Split the string by comma followed by optional whitespace
        String[] pairs = scriptDictStr.split(",\\s*");

        // Iterate through pairs and add them to the HashMap
        for (String pair : pairs) {
            // Split each pair by '=' to separate key and value
            String[] keyValue = pair.split("=");

            // Ensure the pair is in correct format (key=value)
            if (keyValue.length == 2) {
                String key = keyValue[0].trim(); // trim to remove leading/trailing whitespace
                String value = keyValue[1].trim(); // trim to remove leading/trailing whitespace
                allScripts.put(key, value);
            }
        }

        scriptDictStr = removeLettersFromEnd(allScripts, 1);

        scriptDict2 = scriptDictStr;
        if (scriptDict2.equals("{"))
        {
            scriptDict2 = "{}";
        }
        
        explorerPanel = new ExplorerPanel(spritesPath.toString(), "Sprite Explorer");

        explorerPanel.revalidate();
        explorerPanel.repaint();

        scripterPanel = new ExplorerPanel(scriptsPath.toString(), "Script Explorer");

        scripterPanel.revalidate();
        scripterPanel.repaint();
        
        // Initialize ExplorerPanel(s) with project folder path

        SceneName2 = sceneName;
        SceneSprites2 = sceneSprites;
        SceneScripts2 = sceneScripts;

        //changeSceneName.setBounds(20, 20, 200, 40);
        //changeSceneName.setFocusable(false);
        //changeSceneName.addActionListener(this);
//
        //SaveAllButton.setBounds(240, 20, 200, 40);
        //SaveAllButton.setFocusable(false);
        //SaveAllButton.addActionListener(this);
//
        //ExitProjectButton.setBounds(460, 20, 200, 40);
        //ExitProjectButton.setFocusable(false);
        //ExitProjectButton.addActionListener(this);
//
        //BuildAndRunButton.setBounds(680, 20, 200, 40);
        //BuildAndRunButton.setFocusable(false);
        //BuildAndRunButton.addActionListener(this);

        sceneWindowPanel.setBackground(extraDarkGray);
        sceneWindowPanel.setBounds(20, 80, 675, 225);

        explorerPanel.setBackground(extraDarkGray);
        explorerPanel.setBounds(20, 320, 337, 165);

        scripterPanel.setBackground(extraDarkGray);
        scripterPanel.setBounds(367, 320, 337, 165);

        propertiesPanel.setBackground(extraDarkGray);
        propertiesPanel.setBounds(710, 80, 185, 405);

        ImageIcon imageIcon = new ImageIcon("assets/images/fwengineLogo.png");
        Image image = imageIcon.getImage();
        frame.setIconImage(image);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(sceneWindowPanel);
        frame.add(explorerPanel);
        frame.add(propertiesPanel);
        frame.add(scripterPanel);

        frame.setSize(screenWidth, screenHeight);
        frame.setResizable(false);
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setBackground(Color.darkGray);
        frame.setTitle(screenTitle);
        frame.setLayout(null);
        //frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        if (sceneName == "" || sceneSprites == "")
        {
            int option = JOptionPane.showConfirmDialog(null, "There has been an error :( Error Code: 1. Would you like to try to recover the missing data?", "Confirmation", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION)
            {
                File filePathToFile = new File(filePath);
                int index = 1;
                String projectName = "newproject" + index + ".fedb";
                //File file = new File(projectName);
                File oldFileToDelete = new File(filePath + File.separator + fileName);
                File file = new File(filePath + File.separator + projectName);
                boolean hasCreatedFile = false;
                while (!hasCreatedFile)
                {
                    if (!file.exists())
                    {
                        //String filePath = file.getAbsolutePath();
                        Path projectPath = Paths.get(file.toString());
                        db.dbWrite(DefaultData, projectPath);
                        //BinaryData(projectName, filePathToFile);
                        //DebinaryData(projectName, filePathToFile);
                        ProjectDB ofp = new ProjectDB();
                        Path directoryPath = Paths.get(filePathToFile.toString());
                        String contents = ofp.openDBFile(projectName, directoryPath);
                        OpenProjectWithDB opwdb = new OpenProjectWithDB();
                        opwdb.OpenProject(contents, filePathToFile.toString(), projectName);
                        BinaryData(projectName, filePathToFile);
                        hasCreatedFile = true;
                        DeleteFile(oldFileToDelete.toString());
                        break;
                    } else {
                        index += 1;
                        projectName = "newproject" + index + ".fedb";
                        file = new File(filePathToFile.getAbsolutePath() + File.separator + projectName);
                    }
                }
                frame.dispose();
            } else {
                frame.dispose();
            }
            return;
        }

        ReadFilesInDirectory reader = new ReadFilesInDirectory();
        String result = reader.readFilesInDirectory(spritesPath2.toString());
        HashMap<String, Integer> spritesMap = PaintSpritesWithBinaryData(result, reader, spritesPath);
        allSprites = spritesMap;
        for (String key: spritesMap.keySet())
        {
            Integer spritePos = spritesMap.get(key);
            int[] position = demanipulatePosition(spritePos.toString());
            sceneWindowPanel.addSprite(spriteImg, position[0], position[1]);
        }

        JPopupMenu propertyPopup = new JPopupMenu();
        JMenuItem createSprite = new JMenuItem("Create New Sprite");
        JMenuItem renameSprite = new JMenuItem("Rename Sprite");
        JMenuItem editSprite = new JMenuItem("Edit Sprite Properties");
        JMenuItem deleteSprite = new JMenuItem("Delete Sprite");

        propertyPopup.add(createSprite);
        propertyPopup.add(renameSprite);
        propertyPopup.add(editSprite);
        propertyPopup.add(deleteSprite);

        createSprite.addActionListener(e -> createNewSprite());
        deleteSprite.addActionListener(e -> deleteSprite());
        renameSprite.addActionListener(e -> renameSprite());
        editSprite.addActionListener(e -> editSprite());

        explorerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    propertyPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    propertyPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JPopupMenu scriptPopupMenu = new JPopupMenu();
        JMenuItem createScriptBItem = new JMenuItem("Create New Script");
        JMenuItem renameScript = new JMenuItem("Rename Script");
        JMenuItem editScript = new JMenuItem("Edit Script");
        JMenuItem deleteScript = new JMenuItem("Delete Script");

        scriptPopupMenu.add(createScriptBItem);
        scriptPopupMenu.add(renameScript);
        scriptPopupMenu.add(deleteScript);
        scriptPopupMenu.add(editScript);

        deleteScript.addActionListener(e -> deleteScript());
        renameScript.addActionListener(e -> renameScript());
        createScriptBItem.addActionListener(e -> createScript());
        editScript.addActionListener(e -> EditScriptWithPreferences());

        scripterPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    scriptPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    scriptPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public static HashMap<String, String> parseEnvFile(String filePath) {
        HashMap<String, String> envMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore comments and empty lines
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                // Split line into key and value
                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                    envMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return envMap;
    }

    public String removeLettersFromEnd(Object obj, int letterHash)
    {
        String str = String.valueOf(obj);
        String result = str.substring(0, str.length() - letterHash);
        return result;
    }

    public void renameScript() {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Rename Fwengine Script");
        File scriptFolderDir = new File(scriptsPath2.toString());
        fileChooser.setCurrentDirectory(scriptFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Script", scriptFileExtension.substring(1));
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedFileName = fileChooser.getSelectedFile().getName();
            File selectedFile = fileChooser.getSelectedFile();
            @SuppressWarnings("unused")
            String scriptName = removeSuffix(selectedFileName, scriptFileExtension);
            String userInput = manipulateString(JOptionPane.showInputDialog(null, "Enter the new script name:"));
            if (userInput == null || userInput.trim().isEmpty())
            {
                logger.print("Provided Input was Empty");
            } else {
                database db = new database();
                String contents = db.dbRead(Paths.get(selectedFile.getAbsolutePath()));
                File newFile = new File(selectedFile.getParentFile() + File.separator + userInput + scriptFileExtension);
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    logger.error(e, 1);
                }
                db.dbWrite(contents, Paths.get(newFile.toString()));
                HashMap<String, String> scriptDict = convertStringToHashMap(removeLettersFromEnd(allScripts, 1));
                scriptDict.remove(removeSuffix(selectedFileName, scriptFileExtension));
                scriptDict.put(removeSuffix(newFile.getName(), scriptFileExtension), newFile.getAbsolutePath());
                db.dbWrite(String.valueOf(scriptDict), Paths.get(scriptsData2));
                SceneScripts2 = String.valueOf(scriptDict);
                DeleteFile(selectedFile.toString());
                OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
                refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
                frame.dispose();
            }
        }
    }

    public static HashMap<String, String> convertStringToHashMap(String input) {
        HashMap<String, String> resultMap = new HashMap<>();

        // Remove the enclosing curly braces {}
        String content = input.substring(1, input.length() - 1);

        // Split by comma followed by optional whitespace
        String[] pairs = content.split(",\\s*");

        for (String pair : pairs) {
            // Split each pair by '='
            String[] keyValue = pair.split("=");

            // Trim any leading/trailing spaces from key and value
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            // Optionally, you can remove leading/trailing quotes from the value
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            resultMap.put(key, value);
        }

        return resultMap;
    }

    private static File selectedFile = null;
    private static String spriteNameMain = null;

    public void editSprite()
    {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Edit Fwengine Sprite");
        File spriteFolderDir = new File(spritesPath2.toString());
        fileChooser.setCurrentDirectory(spriteFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Sprite", "fesprite");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        //final String spriteNameMain = removeSuffix(fileChooser.getSelectedFile().getName(), spriteFileExtension);
        //final File selectedFile = fileChooser.getSelectedFile();
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            spriteNameMain = removeSuffix(selectedFile.getName(), spriteFileExtension);
        }
        if (selectedFile != null) {
            database db = new database();
            DebinaryData(selectedFile.getName(), selectedFile.getParentFile());
            String spriteContents = db.dbRead(selectedFile.toPath());
            BinaryData(selectedFile.getName(), selectedFile.getParentFile());
            spriteContents = String.valueOf(convertJSONString(spriteContents)).substring(0, String.valueOf(convertJSONString(spriteContents)).length() - 1);
            // New Vars
            HashMap<String, String> spriteArgs = convertStringToHashMap(spriteContents);
            String maniPos = spriteArgs.get(removeSuffix(selectedFile.getName(), spriteFileExtension));
            int[] spritePos = demanipulatePosition(maniPos);
            int spriteX = spritePos[0];
            int spriteY = spritePos[1];

            propertiesPanel.remove(spriteNameLabel);
            propertiesPanel.remove(spriteXLabel);
            propertiesPanel.remove(spriteYLabel);
            propertiesPanel.remove(spriteXField);
            propertiesPanel.remove(spriteYField);
            propertiesPanel.remove(applySpritePropsButton);

            Font LabelFont = new Font("Arial", Font.BOLD, 13);

            spriteNameLabel.setText(String.format("Sprite Name: %s", spriteNameMain));
            spriteNameLabel.setForeground(Color.WHITE);
            spriteNameLabel.setFont(LabelFont);
            spriteNameLabel.setBounds(5, 5, 200, 40);

            spriteXLabel.setText("Sprite X Position: ");
            spriteXLabel.setForeground(Color.WHITE);
            spriteXLabel.setFont(LabelFont);
            spriteXLabel.setBounds(0, 15, 200, 40);

            spriteXField.setText(String.valueOf(spriteX));
            spriteXField.setForeground(Color.WHITE);
            spriteXField.setBackground(extraDarkGray);
            spriteXField.setFont(LabelFont);
            spriteXField.setBounds(0, 30, 80, 10);

            spriteYLabel.setText("Sprite Y Position: ");
            spriteYLabel.setForeground(Color.WHITE);
            spriteYLabel.setFont(LabelFont);
            spriteYLabel.setBounds(0, 30, 200, 40);

            spriteYField.setText(String.valueOf(spriteY));
            spriteYField.setForeground(Color.WHITE);
            spriteYField.setBackground(extraDarkGray);
            spriteYField.setFont(LabelFont);
            spriteYField.setBounds(0, 45, 80, 10);

            applySpritePropsButton.setForeground(extraDarkGray);
            applySpritePropsButton.setBackground(Color.white);
            applySpritePropsButton.setFont(LabelFont);
            applySpritePropsButton.setBounds(50, 67, 200, 40);

            propertiesPanel.add(applySpritePropsButton);
            propertiesPanel.setComponentZOrder(applySpritePropsButton, 0);

            propertiesPanel.add(spriteYField);
            propertiesPanel.setComponentZOrder(spriteYField, 0);

            propertiesPanel.add(spriteYLabel);
            propertiesPanel.setComponentZOrder(spriteYLabel, 0);

            propertiesPanel.add(spriteXField);
            propertiesPanel.setComponentZOrder(spriteXField, 0);

            propertiesPanel.add(spriteXLabel);
            propertiesPanel.setComponentZOrder(spriteXLabel, 0);

            propertiesPanel.add(spriteNameLabel);
            propertiesPanel.setComponentZOrder(spriteNameLabel, 0);
            
            frame.revalidate();
            frame.repaint();
        }
        applySpritePropsButton.addActionListener(e -> {
            if (selectedFile == null) {
                return;
            }
            String newSpriteX = spriteXField.getText();
            String newSpriteY = spriteYField.getText();
            if (isStringInt(newSpriteX))
            {
                if (isStringInt(newSpriteY))
                {
                    HashMap<String, String> newSpriteDict = new HashMap<>();
                    int maxXValue = 506;   // Examples
                    int minXValue = 0;     // Examples
                    int maxYValue = 96;   // Examples
                    int minYValue = 0;     // Examples
                    if (Integer.parseInt(newSpriteX) > maxXValue || Integer.parseInt(newSpriteX) < minXValue)
                    {
                        JOptionPane.showMessageDialog(null, String.format("A Sprite's X Position may only be between values '%d' and '%d'", minXValue, maxXValue), "Sprite Position Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (Integer.parseInt(newSpriteY) > maxYValue || Integer.parseInt(newSpriteY) < minYValue) {
                            JOptionPane.showMessageDialog(null, String.format("A Sprite's Y Position may only be between values '%d' and '%d'", minYValue, maxYValue), "Sprite Position Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            database db = new database();
                            String newPos = String.valueOf(manipulatePosition(Integer.parseInt(newSpriteX), Integer.parseInt(newSpriteY)));
                            newSpriteDict.put(spriteNameMain, newPos);
                            DebinaryData(selectedFile.getName(), selectedFile.getParentFile());
                            db.dbWrite(convertHashmapHSSToString(newSpriteDict), Paths.get(selectedFile.getAbsolutePath()));
                            logger.debug(convertHashmapHSSToString(newSpriteDict));
                            BinaryData(selectedFile.getName(), selectedFile.getParentFile());
                            OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
                            refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
                            frame.dispose();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "A Sprite's Y Position may only be an integer(1, 2, 3, ect.) not a string.", "Sprite Position Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (isStringInt(newSpriteX) == false && isStringInt(newSpriteY) == false)
                {
                    JOptionPane.showMessageDialog(null, "A Sprite's X/Y Position may only be an integer(1, 2, 3, ect.) not a string.", "Sprite Position Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "A Sprite's X Position may only be an integer(1, 2, 3, ect.) not a string.", "Sprite Position Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public static HashMap<String, String> convertJSONString(String jsonString) {
        HashMap<String, String> resultMap = new HashMap<>();

        // Remove curly braces and split by comma to get key-value pairs
        String[] pairs = jsonString.substring(1, jsonString.length() - 1).split(",\\s*");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":\\s*");
            // Remove quotes around keys and values
            String key = keyValue[0].replaceAll("^\"|\"$", "");
            String value = keyValue[1].replaceAll("^\"|\"$", "");
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public void renameSprite() {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Rename Fwengine Sprite");
        File spriteFolderDir = new File(spritesPath2.toString());
        fileChooser.setCurrentDirectory(spriteFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Sprite", "fesprite");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedFileName = fileChooser.getSelectedFile().getName();
            File selectedFile = fileChooser.getSelectedFile();
            String spriteName = removeSuffix(selectedFileName, spriteFileExtension);
            String userInput = manipulateString(JOptionPane.showInputDialog(null, "Enter the new sprite name:"));
            if (userInput == null || userInput.trim().isEmpty())
            {
                logger.print("Provided Input was Emtpy");
            } else {
                HashMap<String, Integer> newSpriteValue = new HashMap<>();
                Integer oldValue = allSprites.get(spriteName);
                newSpriteValue.put(userInput, oldValue);
                String JSONspritevalue = convertToJson(newSpriteValue);
                DeleteFile(selectedFile.getAbsolutePath());
                File newFile = new File(selectedFile.getParentFile() + File.separator + userInput + spriteFileExtension);
                try
                {
                    newFile.createNewFile();
                    database db = new database();
                    db.dbWrite(JSONspritevalue, newFile.toPath());
                    BinaryData(userInput + spriteFileExtension, newFile.getParentFile());
                    OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
                    refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
                    frame.dispose();
                } catch (IOException e)
                {
                    logger.error(e, 1);
                }
            }
        }
    }

    public String removeSuffix(String str, String suffix)
    {
        if (str != null && suffix != null && str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
    }

    private void addDefaultSprite()
    {
        String spritePath = spriteImg;
        sceneWindowPanel.addSpriteAtCenter(spritePath);
        int spriteIndex = 1;
        boolean hasCreatedFile = true;
        File startSpriteFile = new File(filePath2 + File.separator + "bin" + File.separator + "assets" + File.separator + "sprites" + File.separator + "sprite" + spriteIndex + spriteFileExtension);
        while (hasCreatedFile)
        {
            if (!Files.exists(startSpriteFile.toPath()))
            {
                try {
                    startSpriteFile.createNewFile();
                    String spriteName = "sprite" + spriteIndex;
                    int spriteX = 253;
                    int spriteY = 47;
                    int mp = manipulatePosition(spriteX, spriteY);
                    //String mpStr = String.valueOf(mp);
                    SceneSprites2 += "'";
                    SceneSprites2 += spriteName;
                    SceneSprites2 += "'";
                    SceneSprites2 += ": ";
                    SceneSprites2 += mp;
                    SceneSprites2 += ", ";
                    Path feDBPath = Paths.get(feDBString);
                    String OriginalGivenData = String.format(
                "SceneName = \"%s\"\nSceneSprites = \"%s\"\nSceneScripts = \"%s\"",
                        SceneName2, SceneSprites2, SceneScripts2
                    );
                    HashMap<String, Integer> spriteDictionary = new HashMap<>();
                    spriteDictionary.put(spriteName, mp);
                    database db = new database();
                    db.dbWrite(OriginalGivenData, feDBPath);
                    db.dbWrite(convertToJson(spriteDictionary), startSpriteFile.toPath());
                    File feDBFolderFile = new File(feDBOriginFolder);
                    BinaryData(fileName2, feDBFolderFile);
                    BinaryData(startSpriteFile.getName(), startSpriteFile.getParentFile());
                } catch (IOException e) {
                    logger.error(e, 1);
                } finally {
                    hasCreatedFile = false;
                }
            } else {
                spriteIndex++;
                startSpriteFile = new File(filePath2 + File.separator + "bin" + File.separator + "assets" + File.separator + "sprites" + File.separator + "sprite" + spriteIndex + spriteFileExtension);
            }
        }
    }

    public static String convertToJson(HashMap<String, Integer> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (!first) {
                json.append(", ");
            }
            json.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
            first = false;
        }

        json.append("}");
        return json.toString();
    }

    public static String convertHashmapHSSToString(HashMap<String, String> map) {
        String resultString = "null";
        System.out.println(String.valueOf(map));
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String formattedEntry = String.format("{\"%s\": %s}", key, value);
            resultString = formattedEntry;
        }
        return resultString;
    }

    public void deleteScript()
    {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Delete Fwengine Script");
        File scriptFolderDir = new File(scriptsPath2.toString());
        fileChooser.setCurrentDirectory(scriptFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Script", scriptFileExtension.substring(1));
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            DeleteFile(selectedFile.toString());
            database db = new database();
            scriptDict2 = db.dbRead(Paths.get(scriptsData2));
            HashMap<String, String> scriptDict = new HashMap<>();
            String[] pairs = scriptDict2.split(",\\s*");

        // Iterate through pairs and add them to the HashMap
            for (String pair : pairs) {
                // Split each pair by '=' to separate key and value
                String[] keyValue = pair.split("=");

                // Ensure the pair is in correct format (key=value)
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim(); // trim to remove leading/trailing whitespace
                    String value = keyValue[1].trim(); // trim to remove leading/trailing whitespace
                    scriptDict.put(key, value);
                }
            }
            
            HashMap<String, String> scriptDict2Better = parseHashMap(String.valueOf(scriptDict));

            scriptDict2Better.remove(removeSuffix(selectedFile.getName(), scriptFileExtension));

            db.dbWrite(String.valueOf(parseMalformedString(String.valueOf(scriptDict2Better))), Paths.get(scriptsData2));

            SceneScripts2 = String.valueOf(parseMalformedString(String.valueOf(scriptDict2Better)));

            OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
            refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
            frame.dispose();
        }
    }

    public static HashMap<String, String> parseMalformedString(String input) {
        HashMap<String, String> resultMap = new HashMap<>();
        input = input.trim();

        // Remove leading and trailing braces if present
        if (input.startsWith("{")) {
            input = input.substring(1);
        }
        if (input.endsWith("}")) {
            input = input.substring(0, input.length() - 1);
        }

        // Split by commas to get individual key-value pairs
        String[] pairs = input.split("\\s*,\\s*");

        for (String pair : pairs) {
            // Check if the pair ends with '}' (indicating an incorrectly placed brace)
            if (pair.endsWith("}")) {
                pair = pair.substring(0, pair.length() - 1).trim(); // Remove the extra brace and trim whitespace
            } else {
                pair = pair.trim(); // Trim leading and trailing whitespace
            }

            // Split each pair by '=' to separate key and value
            String[] keyValue = pair.split("\\s*=\\s*", 2); // Limit to 2 splits to handle '=' in values

            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }

    public static HashMap<String, String> parseHashMap(String input) {
        // Remove outer curly braces if present
        if (input.startsWith("{{") && input.endsWith("}}")) {
            input = input.substring(2, input.length() - 2);
        }
        
        // Create a HashMap to store key-value pairs
        HashMap<String, String> hashMap = new HashMap<>();

        // Split the input by commas to get individual key-value pairs
        String[] pairs = input.split(",");

        // Iterate through pairs and add them to the HashMap
        for (String pair : pairs) {
            // Split each pair by '=' to separate key and value
            String[] keyValue = pair.split("=");

            // Ensure the pair is in correct format (key=value)
            if (keyValue.length == 2) {
                String key = keyValue[0].trim(); // trim to remove leading/trailing whitespace
                String value = keyValue[1].trim(); // trim to remove leading/trailing whitespace
                hashMap.put(key, value);
            }
        }

        return hashMap;
    }

    public String getCodeEditor(String input)
    {
        if (input.contains(": ")) {
            // Split the input string based on the pattern ": "
            String[] parts = input.split(": ", 2); // Limit the split to 2 parts
            // Check if there are at least 2 parts
            if (parts.length > 1) {
                // Trim and return the second part, which is the value
                return parts[1].trim();
            }
        }
        // Return an empty string or a default value if the pattern is not found
        return "";
    }
    
    public void EditScriptWithPreferences()
    {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Open Fwengine Script");
        File spritesFolderDir = new File(scriptsPath2.toString());
        fileChooser.setCurrentDirectory(spritesFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Script", scriptFileExtension.substring(1));
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (globalCodeEditor.equals("Notepad"))
            {
                try {
                    String[] command = {"notepad", "\"" + selectedFile + "\""};
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Sorry, but currently you do not have \"NOTEPAD\" installed on your system. Please install this to edit your script with this preference.", "Program Not Found", JOptionPane.ERROR_MESSAGE);
                    logger.error(e, 1);
                }
            } else if (globalCodeEditor.equals("Notepad++")) {
                try {
                    String[] command = {"notepad++", "\"" + selectedFile + "\""};
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Sorry, but currently you do not have \"NOTEPAD++\" installed on your system. Please install this to edit your script with this preference.", "Program Not Found", JOptionPane.ERROR_MESSAGE);
                    logger.error(e, 1);
                }
            } else if (globalCodeEditor.equals("SublimeText")) {
                try {
                    String[] command = {"subl", "\"" + selectedFile + "\""};
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                } catch (Exception f) {
                    try {
                        String[] command = {"sublime_text", "\"" + selectedFile + "\""};
                        Process process = Runtime.getRuntime().exec(command);
                        process.waitFor();
                    } catch (IOException | InterruptedException e) {
                        JOptionPane.showMessageDialog(null, "Sorry, but currently you do not have \"SUBLIME TEXT\" installed on your system. Please install this to edit your script with this preference.", "Program Not Found", JOptionPane.ERROR_MESSAGE);
                        logger.error(e, 1);
                    }
                }
            } else if (globalCodeEditor.equals("VSCode")) {
                try
                {
                    ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/openVSCode.py", selectedFile.toString());
                
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
                    logger.error(e, 1);
                }
            }
        }
    }

    public void createScript()
    {
        File newScriptFile = new File(scriptsPath2 + File.separator + "newscript1" + scriptFileExtension);
        int sIndex = 1;
        boolean fileExists = false;
        while (!fileExists)
        {
            if(!Files.exists(newScriptFile.toPath()))
            {
                fileExists = true;
                try{
                    CheckFwengineDLL();

                    newScriptFile.createNewFile();
                    String scriptData = "";
                    if (scriptingLanguage.equals("csharp"))
                    {
                        scriptData = """
                            #r \"Fwengine.dll\"
    
                            using Fwengine;
                                            
                            public class NewScript {
                                public static void Main(string[] args)
                                {
                                    FwengineCore.FEConsole console = new FwengineCore.FEConsole();
                                    console.Println("Hello, World!");
                                }
                            }
                            NewScript.Main(null);
    
                            """;
                    } else if (scriptingLanguage.equals("python")){
                        scriptData = """
                                from Fwengine import *

                                if __name__ == \"__main__\":
                                    console = FwengineCore.FEConsole()
                                    console.Println(\"Hello, World!\")
                                """;
                    }
                    
                    database db = new database();
                    allScripts.put(removeSuffix(newScriptFile.getName(), scriptFileExtension), newScriptFile.getAbsolutePath());
                    db.dbWrite(String.valueOf(allScripts), Paths.get(scriptsData2));
                    SceneScripts2 = String.valueOf(allScripts);
                    db.dbWrite(scriptData, newScriptFile.toPath());
                    OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
                    refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
                    frame.dispose();
                } catch(Exception e) {
                    logger.error(e, 1);
                }
            } else {
                sIndex++;
                newScriptFile = new File(scriptsPath2 + File.separator + "newscript" + sIndex + scriptFileExtension);
            }
        }
    }

    public String BinaryString(String input)
    {
        try {
            // Create a ProcessBuilder to run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/binaryString.py", input);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Check if the process exited successfully
            if (exitCode == 0) {
                // Interpret the output from the Python script
                String result = output.toString().trim();
                return result;
            } else {
                System.err.println("Python script exited with code: " + exitCode);
            }
        } catch (Exception e) {
            logger.error(e, 1);
        }
        return "";
    }

    public String DebinaryString(String input)
    {
        try {
            // Create a ProcessBuilder to run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/debinaryString.py", input);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Check if the process exited successfully
            if (exitCode == 0) {
                // Interpret the output from the Python script
                String result = output.toString().trim();
                return result;
            } else {
                System.err.println("Python script exited with code: " + exitCode);
            }
        } catch (Exception e) {
            logger.error(e, 1);
        }
        return "";
    }

    public void createNewSprite()
    {
        CheckBINFolder();
        addDefaultSprite();
        OpenProjectWithSceneArgs reopenProjectWithCorrectSprites = new OpenProjectWithSceneArgs();
        reopenProjectWithCorrectSprites.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
        frame.dispose();
    }
    public void deleteSprite()
    {
        CheckFwengineDLL();
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // Set your custom icon here
                dialog.setIconImage(new ImageIcon("assets/images/fwengineLogo.png").getImage());
                return dialog;
            }
        };
        fileChooser.setDialogTitle("Delete Fwengine Sprite");
        File spritesFolderDir = new File(spritesPath2.toString());
        fileChooser.setCurrentDirectory(spritesFolderDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fwengine Sprite", "fesprite");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            DeleteFile(selectedFile.toString());
            OpenProjectWithSceneArgs refreshProject = new OpenProjectWithSceneArgs();
            refreshProject.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
            frame.dispose();
        }
    }
    public int manipulatePosition(int xPos, int yPos)
    {
        int xpLen = String.valueOf(xPos).length();
        int ypLen = String.valueOf(yPos).length();
        String returnString = "";
        returnString += xpLen;
        returnString += xPos;
        returnString += ypLen;
        returnString += yPos;
        return Integer.parseInt(returnString);
    }
    public void CheckBINFolder() {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/setupdefaultsprite.py", feDBOriginFolder);

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
            logger.error(e, 1);
        }
    }

    public void nameSceneChange()
    {
        String userInput = JOptionPane.showInputDialog(null, "Enter the new scene name:");
        if (userInput == null || userInput.trim().isEmpty())
        {
            System.out.println("Provided Input was Emtpy");
        } else {
            frame.setTitle("Fwengine: " + userInput);
            //Path feDBPath = Paths.get(feDBString);
            SceneName2 = userInput;
            String newFileNameForFEDB = manipulateString(SceneName2);
            String newFileExtensionNameForFEDB = newFileNameForFEDB + ".fedb";
            String newFileLocation = filePath2 + File.separator + newFileExtensionNameForFEDB;
            Path newFilePath = Paths.get(newFileLocation);
            String OriginalGivenData = String.format(
        "SceneName = \"%s\"\nSceneSprites = \"%s\"\nSceneScripts = \"%s\"",
                SceneName2, SceneSprites2, SceneScripts2
            );
            database db = new database();
            //db.dbWrite(OriginalGivenData, feDBPath);
            db.dbWrite(OriginalGivenData, newFilePath);
            File feDBFolderFile = new File(feDBOriginFolder);
            BinaryData(newFileExtensionNameForFEDB, feDBFolderFile);
            DeleteFile(feDBFolderFile + File.separator + fileName2);
            fileName2 = newFileExtensionNameForFEDB;
            OpenProjectWithSceneArgs newProjectOpen = new OpenProjectWithSceneArgs();
            newProjectOpen.OpenProject(SceneName2, SceneSprites2, SceneScripts2, filePath2, fileName2);
            frame.dispose();
        }
    }

    public void saveProject()
    {
        Path feDBPath = Paths.get(feDBString);
        String OriginalGivenData = String.format(
        "SceneName = \"%s\"\nSceneSprites = \"%s\"\nSceneScripts = \"%s\"",
            SceneName2, SceneSprites2, SceneScripts2
        );
        database db = new database();
        db.dbWrite(OriginalGivenData, feDBPath);
        File feDBFolderFile = new File(feDBOriginFolder);
        BinaryData(fileName2, feDBFolderFile);
    }

    public void buildAndRun()
    {
        String directoryOutput = "bin";
        String assetFolder = "assets";
        String imageFolder = "images";
        Path directoryPath = Paths.get(filePath2 + File.separator + directoryOutput);
        Path assetPath = Paths.get(directoryPath.toString() + File.separator + assetFolder);
        Path imagePath = Paths.get(directoryPath.toString() + File.separator + assetFolder + File.separator + imageFolder);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
                System.out.println("Directory created successfully: " + directoryOutput);
            } catch (IOException f) {
                System.err.println("Failed to create directory: " + f.getMessage());
            }
        } else {
            System.out.println("Directory already exists: " + directoryOutput);
        }
        if (!Files.exists(assetPath))
        {
            try {
                Files.createDirectory(assetPath);
                System.out.println("Directory created successfully: " + assetFolder);
            } catch (IOException f) {
                System.err.println("Failed to create directory: " + f.getMessage());
            }
        } else {
            System.out.println("Directory already exists: " + assetFolder);
        }
        if (!Files.exists(imagePath))
        {
            try {
                Files.createDirectory(imagePath);
                System.out.println("Directory created successfully: " + imageFolder);
            } catch (IOException f) {
                System.err.println("Failed to create directory: " + f.getMessage());
            }
        } else {
            System.out.println("Directory already exists: " + imageFolder);
        }
        BuildProject(SceneName2, allSprites, SceneScripts2, scriptingLanguage);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        
    }

    public void CheckFwengineDLL()
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/checkFwengine.py", scriptsPath2);

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
            logger.error(e, 1);
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
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            logger.error(e, 1);
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
            logger.error(e, 1);
        }
    }
    public static String manipulateString(String input)
    {
        String noSpecialChars = input.replaceAll("[^a-zA-Z0-9\\s]", "");
        String lowercased = noSpecialChars.toLowerCase();
        String noSpaces = lowercased.replaceAll("\\s+", "");
        return noSpaces;
    }
    public void DeleteFile(String filePath)
    {
        Path filePathFixed = Paths.get(filePath);
        try
        {
            Files.delete(filePathFixed);
            logger.error("Successfully deleted file!", 1);
        } catch (NoSuchFileException e) {
            logger.error("No such file/directory exists", 1);
        } catch (DirectoryNotEmptyException e) {
            logger.error("Directory is not empty.", 1);
        } catch (IOException e) {
            logger.error("Invalid permissions.", 1);
        }
    }
    public void BuildProject(String nameOfScene, HashMap<String, Integer> spriteDict, String sceneScripts, String languageToScript)
    {
        CheckFwengineDLL();
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "python-scripts/buildProject.py", nameOfScene, convertToJson(spriteDict), filePath2 + File.separator + "bin" + File.separator + manipulateString(nameOfScene) + ".exe", filePath2 + File.separator + "bin" + File.separator + manipulateString(nameOfScene) + ".py", filePath2, manipulateString(nameOfScene), sceneScripts, languageToScript);

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
            logger.error(e, 1);
        }
    }
    public HashMap<String, Integer> PaintSpritesWithBinaryData(String valueHash, ReadFilesInDirectory reader, Path spriteFolderPath)
    {
        HashMap<String, Integer> spriteOutput = new HashMap<>();
        File[] spriteFiles = spriteFolderPath.toFile().listFiles();
        for (File file : spriteFiles) {
            if (file.isFile()) {
                DebinaryData(file.getName(), spriteFolderPath.toFile());
                database db = new database();
                String contents = db.dbRead(Paths.get(filePath2 + File.separator + "bin" + File.separator + "assets" + File.separator + "sprites" + File.separator + file.getName()));
                try {
                    HashMap<String, Integer> result = parseString(contents);
                    spriteOutput.putAll(result);
                } catch (IllegalArgumentException e) {
                    logger.error("Given Contents were Null or Emtpy", 1);
                }
                BinaryData(file.getName(), spriteFolderPath.toFile());
            } else if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                // If you want to recursively list files in subdirectories, you can call loopThroughFiles(file.getAbsolutePath());
            }
        }
        return spriteOutput;
    }
    public static HashMap<String, Integer> parseString(String input) {
        HashMap<String, Integer> resultMap = new HashMap<>();

        // Check if input string is null or empty
        if (input == null || input.isEmpty()) {
            // Handle empty input string
            throw new IllegalArgumentException("Input string is null or empty");
        }
        // Remove leading and trailing whitespace
        input = input.trim();
        // Check if input string starts with '{' and ends with '}'
        if (!input.startsWith("{") || !input.endsWith("}")) {
            // Handle invalid input format
            throw new IllegalArgumentException("Invalid input format: " + input);
        }
        // Remove leading '{' and trailing '}'
        input = input.substring(1, input.length() - 1);
        // Split the input string by comma
        String[] parts = input.split(",");
        for (String part : parts) {
            // Split each part by colon to separate key and value
            String[] keyValue = part.split(":");
            // Check if keyValue array has exactly two elements
            if (keyValue.length != 2) {
                // Handle invalid key-value pair format
                throw new IllegalArgumentException("Invalid key-value pair format: " + part);
            }
            // Remove leading and trailing whitespace from key and value
            String key = keyValue[0].trim().replaceAll("\"", "");
            int value;
            try {
                value = Integer.parseInt(keyValue[1].trim());
            } catch (NumberFormatException e) {
                // Handle invalid integer format
                throw new IllegalArgumentException("Invalid integer format for value: " + keyValue[1]);
            }
            // Add key-value pair to the HashMap
            resultMap.put(key, value);
        }
        return resultMap;
    }
    public int[] demanipulatePosition(String manipulatePosition)
    {
        if (manipulatePosition == null || manipulatePosition.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty.");
        }

        // Extract lengths from the first character
        int firstLength = Character.getNumericValue(manipulatePosition.charAt(0));
        @SuppressWarnings("unused")
        int secondLength = Character.getNumericValue(manipulatePosition.charAt(firstLength + 1));

        // Extract integers based on lengths
        int firstInt = Integer.parseInt(manipulatePosition.substring(1, firstLength + 1));
        int secondInt = Integer.parseInt(manipulatePosition.substring(firstLength + 2));

        return new int[]{firstInt, secondInt};
    }
}
