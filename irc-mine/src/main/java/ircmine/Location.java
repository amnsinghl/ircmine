package ircmine;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Location {
    //    private static final String IPADDRESS_PATTERN =
//            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    static String IPADDRESS_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    public static void main(String[] args) throws IOException {
        RateLimiter rateLimiter = RateLimiter.create(100);
        BufferedWriter memberLocation = FileHelper.getFileW("memberLocation");
        BufferedReader memberData = FileHelper.getFileR("memberDataUnique");
        List<String> members = new ArrayList<>();
        List<String> ips = new ArrayList<>();
//        String regex = "[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}";
        int i = 0;
        for (String line = memberData.readLine(); line != null; line = memberData.readLine()) {
            System.out.println("processing " + i++);
            String[] split = line.split(",");
            String member = split[0];
            String location = split[1];
            String replace = location.replace("-", ".");
            System.out.println(replace);
            String ip = getIp(replace);
            if (Strings.isNullOrEmpty(ip)) {
                ip = runShell(location).trim();
            }
            if (!Strings.isNullOrEmpty(ip)) {
                members.add(member);
                ips.add(ip.split("\n")[0]);
            }
            System.out.println(ip);
            if (members.size() == 100) {
                fetch(memberLocation, members, ips);
            }
        }
        fetch(memberLocation, members, ips);
        FileHelper.close(memberLocation);
        FileHelper.close(memberData);
    }

    private static void fetch(BufferedWriter memberLocation, List<String> members, List<String> ips) throws IOException {
        JSONArray joa = new JSONArray();
        for (int j = 0; j < members.size(); j++) {
            JSONObject job = new JSONObject();
            job.put("query", ips.get(j));
            joa.put(job);
        }

//                rateLimiter.acquire();
        System.out.println(joa.toString());
        String s = UrlHelper.makePost("http://ip-api.com/batch", joa.toString());
        if (s != null) {
            System.out.println(s);
        }

        JSONArray job = new JSONArray(s);
        for (int k = 0; k < job.length(); k++) {
            JSONObject obj = job.getJSONObject(k);
            try {
                FileHelper.writeToFile(memberLocation, members.get(k), obj.getString("countryCode"), obj.getString("region"),
                        obj.getString("country"), obj.getString("regionName"),
                        obj.getString("city"), String.valueOf(obj.getDouble("lat")), String.valueOf(obj.getDouble("lon")), obj.getString("zip"));
            } catch (Exception e) {

            }
        }
        members.clear();
        ips.clear();
    }

    private static String getIp(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
//        str = str.replace("\\.", "#");
//        str = str.replace("[^0-9]", ";");
//        str = str.replace("#", ".");
//        str = repl(str);
//        System.out.println("hurr  " + str);
//        String fin = "";
//        for(int i=0;i<str.length();i++) {
//            for(int j=i;j<str.length();j++){
//                String substring = str.substring(i, j);
//                if(pattern.matcher(substring).matches()) {
//                    if(fin.length() <= substring.length())
//                        fin = substring;
//                }
//            }
//        }
//        System.out.println(fin);
//        return fin;
    }

    private static String repl(String st) {
        StringBuilder stb = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < st.length(); i++) {
            if (st.charAt(i) == ';') {
                if (!flag)
                    stb.append(";");
                flag = true;
            } else {
                flag = false;
                stb.append(st.charAt(i));
            }
        }
        return stb.toString();
    }

    private static String runShell(String host) {

        ShellCommand shell = new ShellCommand();

        String result = shell.run("dig +short " + host);
//        System.out.println("ddd "+result);
        return result;
    }

}