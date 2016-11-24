package ircmine;

import java.io.*;

/**
 * Created by aman on 21/11/16.
 */
public class Reduce {
    public static void main(String[] args) throws IOException {
        BufferedWriter channelMembers = getFile("channelMembers");
        BufferedWriter memberData = getFile("memberData");
        for(int i=1;i<=183;i++) {
            System.out.println("processing " + i);
            BufferedReader br = new BufferedReader(new FileReader("chmem" + i));
            String line = br.readLine();
            while(line != null) {
                writeToFile(channelMembers, line);
                line = br.readLine();
            }
            br.close();
            BufferedReader br2 = new BufferedReader(new FileReader("memData" + i));
            String line2 = br2.readLine();
            while(line2 != null) {
                writeToFile(memberData, line2);
                line2 = br2.readLine();
            }
            br2.close();
        }
        close(channelMembers);
        close(memberData);
    }

    private static BufferedWriter getFile(String fileName) throws IOException {

        File file = new File(fileName);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    static void writeToFile(BufferedWriter writer, String st) {
        try {
            writer.write(st + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void close(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
