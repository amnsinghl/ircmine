package ircmine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 21/11/16.
 */
public class UniqueMembers {
    public static void main(String[] args) throws IOException {
        BufferedReader memberData = FileHelper.getFileR("memberData");
        BufferedWriter memberDataUnique = FileHelper.getFileW("memberDataUnique");
        Map<String, String[]> map = new HashMap<>();
        for(String line = memberData.readLine(); line!= null;line = memberData.readLine()){
            String[] split = line.split(",");
            map.put(split[0], split);
        }
        for(Map.Entry<String, String[]> e: map.entrySet()) {
            FileHelper.writeToFile(memberDataUnique, e.getValue());
        }
        FileHelper.close(memberDataUnique);
        FileHelper.close(memberData);
    }
}
