package ircmine;

import java.io.*;

/**
 * Created by aman on 21/11/16.
 */
public class FileHelper {

    public static BufferedReader getFileR(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br;
    }

    public static BufferedWriter getFileW(String fileName) throws IOException {

        File file = new File(fileName);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    public static void writeToFile(BufferedWriter writer, String... st) {
        StringBuilder str = new StringBuilder();
        str.append(st[0]);
        for(int i=1;i<st.length;i++) {
            str.append(",").append(st[i]);
        }
        try {
            writer.write(str.toString() + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(BufferedReader writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
