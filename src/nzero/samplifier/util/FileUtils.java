package nzero.samplifier.util;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileUtils {
    private FileUtils() {

    }

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace(); //todo: handle
        }
        return result;
    }

}
