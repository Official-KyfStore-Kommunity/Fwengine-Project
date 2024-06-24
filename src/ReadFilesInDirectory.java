package src;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class ReadFilesInDirectory {

    // Function to read and return file contents
    public String readFilesInDirectory(String folderPath) {
        StringBuilder output = new StringBuilder();
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return "The specified path is not a valid directory: " + folderPath;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return "Error listing files in directory: " + folderPath;
        }

        for (File file : files) {
            if (file.isFile()) {
                output.append(file.getName()).append(":").append(readFile(file)).append("|");
            }
        }

        // Remove the trailing delimiter if present
        if (output.length() > 0) {
            output.setLength(output.length() - 1);
        }

        return output.toString();
    }

    // Helper function to read a single file and return its content
    public static String readFile(File file) {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            fileContent.append("Error reading file ").append(file.getName()).append(": ").append(e.getMessage());
        }
        return fileContent.toString();
    }

    // Function to process each file's content and return a HashMap
    public HashMap<String, Integer> processFileContents(String contents) {
        HashMap<String, Integer> fileLengths = new HashMap<>();

        // Split the output string by the delimiter
        String[] filesContents = contents.split("\\|");

        // Loop through each part of the split string and process it
        for (String fileContent : filesContents) {
            // Split each entry into file name and content
            String[] parts = fileContent.split(":", 2);
            if (parts.length == 2) {
                String fileName = parts[0];
                String content = parts[1];
                fileLengths.put(fileName, content.length());
            }
        }

        return fileLengths;
    }
}
