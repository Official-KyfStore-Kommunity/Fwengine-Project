package src;
public class OpenProjectWithDB {

    // Default Vars

    String SceneName = "";
    String SceneContents = "";
    String SceneSprites = "";
    String SceneScripts = "";

    public String getSceneName(String DBinput) {
            String[] lines = DBinput.split("\n");
            for (String line : lines) {
                if (line.startsWith("SceneName")) {
                    SceneName = line.split("=")[1].trim().replace("\"", "");
                    break;
                }
            }
        return SceneName;
    }

    public String getSceneSprites(String DBinput) {
        String[] lines = DBinput.split("\n");
        for (String line : lines) {
            if (line.startsWith("SceneSprites")) {
                SceneSprites = line.split("=")[1].trim().replace("\"", "");
                break;
            }
        }
        return SceneSprites;
    }

    public String getSceneScripts(String DBinput) {
        String[] lines = DBinput.split("\n");
        for (String line : lines) {
            if (line.startsWith("SceneScripts")) {
                SceneScripts = line.split("=")[1].trim().replace("\"", "");
                break;
            }
        }
        return SceneScripts;
    }

    public void OpenProject(String dbContentStringForm, String filePath, String fileName)
    {
        getSceneName(dbContentStringForm);
        getSceneSprites(dbContentStringForm);
        getSceneScripts(dbContentStringForm);
        OpenProjectWithSceneArgs opsArgs = new OpenProjectWithSceneArgs();
        opsArgs.OpenProject(SceneName, SceneSprites, SceneScripts, filePath, fileName);
    }
}
