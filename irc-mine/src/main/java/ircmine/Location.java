package ircmine;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Location {
    public static void main(String[] args) throws IOException {
        BufferedWriter memberLocation = FileHelper.getFileW("memberLocation");
        BufferedReader memberData = FileHelper.getFileR("memberData");
        FileHelper.writeToFile(memberLocation, "nickName", "countryCode", "region",
                "country", "regionName", "city", "lat", "lon", "zip");
        int i=0;
        for (String line = memberData.readLine(); line != null; line = memberData.readLine()) {
            System.out.println("processing " + i++);
            String[] split = line.split(",");
            String member = split[0];
            String location = split[1];
            String json = null;
            try {
                json = UrlHelper.getHTML("http://ip-api.com/json/" + location);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(json);
            if(json == null) {
                continue;
            }
            JSONObject obj = new JSONObject(json);
            if (obj.has("message")) {
//                FileHelper.writeToFile(memberLocation, member, "unknown");
            } else {
                FileHelper.writeToFile(memberLocation, member, obj.getString("countryCode"), obj.getString("region"),
                        obj.getString("country"), obj.getString("regionName"),
                        obj.getString("city"), String.valueOf(obj.getDouble("lat")), String.valueOf(obj.getDouble("lon")), obj.getString("zip"));
            }
        }
        FileHelper.close(memberLocation);
        FileHelper.close(memberData);
    }
}