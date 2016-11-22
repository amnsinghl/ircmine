package ircmine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCommand {

    public static void main(String args[]) {

        ShellCommand shell = new ShellCommand();

        String result = shell.run("dig +short mkyong.com");
        System.out.println(result);

    }

    public String run(String command) {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        Process p;

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}