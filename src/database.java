package src;
import java.nio.file.*;
import java.io.*;

public class database {
    public void dbWrite(String fileinput, Path filepath)
    {
        //try
        //{
        //    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        //    writer.write(fileinput);
        //    writer.close();
        //} catch (IOException e){
        //    e.printStackTrace();
        //}
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            writer.write(fileinput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String dbRead(Path filePath) {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }
    public void dbDelete(String filename, String deleteType)
    {
        try
        {
            if (deleteType == "clear")
            {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write("");
                writer.close();
            } else if (deleteType == "delete")
            {
                File file = new File(filename);
                file.delete();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
