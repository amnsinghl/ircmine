package ircmine;

import com.google.common.util.concurrent.RateLimiter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Location {
    public static void main(String[] args) throws IOException {
        RateLimiter rateLimiter = RateLimiter.create(2.4);
        BufferedWriter memberLocation = FileHelper.getFileW("memberLocation");
        BufferedReader memberData = FileHelper.getFileR("memberDataUnique");
        int i=0;
        for (String line = memberData.readLine(); line != null; line = memberData.readLine()) {
            System.out.println("processing " + i++);
            String[] split = line.split(",");
            String member = split[0];
            String location = split[1];
            String json = null;

            if(location.contains("/") || location.contains("\\"))
                continue;

            rateLimiter.acquire();
            try {
                json = UrlHelper.getHTML("http://ip-api.com/csv/" + location);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(json);

            if(json == null) {
                continue;
            } else {
                FileHelper.writeToFile(memberLocation, member, json);
//                FileHelper.writeToFile(memberLocation, member, obj.getString("countryCode"), obj.getString("region"),
//                        obj.getString("country"), obj.getString("regionName"),
//                        obj.getString("city"), String.valueOf(obj.getDouble("lat")), String.valueOf(obj.getDouble("lon")), obj.getString("zip"));
            }
        }
        FileHelper.close(memberLocation);
        FileHelper.close(memberData);
    }

}