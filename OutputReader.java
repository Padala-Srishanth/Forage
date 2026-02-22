
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class OutputReader {
    public static void main(String[] args) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("task5_output_cmd.txt"));
            // Try different charsets if needed, but PowerShell > is usually UTF-16LE
            boolean printing = false;
            for (String line : lines) {
                if (line.contains("---begin output ---")) {
                    printing = true;
                }
                if (printing) {
                    System.out.println(line);
                }
                if (line.contains("---end output ---")) {
                    printing = false;
                    break;
                }
            }
        } catch (IOException e) {
            try {
                // Fallback to default or UTF-8
                List<String> lines = Files.readAllLines(Paths.get("task5_output.txt"));
                for (String line : lines) {
                    if (line.contains("Balance {")) {
                        System.out.println(line);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
