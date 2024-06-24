package src;
import java.io.*;
import java.nio.file.*;

public class ProjectDB {

    // This method reads the contents of the file and returns the lines as a single String
    private String getContentsFromFile(Path filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // This method calls getContentsFromFile and returns the content as a single String
    public String openDBFile(String nameOfFile, Path directoryPath) {
        Path filePath = directoryPath.resolve(nameOfFile);
        return getContentsFromFile(filePath);
    }
}
