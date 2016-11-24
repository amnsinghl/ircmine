package ircmine;

import org.json.JSONObject;
import org.kitteh.irc.client.library.Client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class App {

    ConcurrentLinkedDeque<Thread> threads = new ConcurrentLinkedDeque<>();
    boolean flag = false;

    public synchronized void execNext() {
        System.out.println("executing ");
        if (flag) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        flag = true;
        System.out.println("==================== execution " + threads.size() + "===================================");
        Thread pop = threads.pop();
        pop.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        mineChannels();
        mineUserData();
    }

    private static Client getClient() {
//        JSONObject name = getRealName();
        String nick = generateName();
//                Client client = Client.builder().nick(nick).realName(name.getString("first")).serverHost("eris.us.ircnet.net").serverPort(6667).secure(false).build();
        Client client = Client.builder().nick(nick).serverHost("irc.rizon.net").serverPort(6667).secure(false).build();
//        Client client = Client.builder().nick(nick).serverHost("2a01:60:45:1000::304").serverPort(6667).secure(false).build();
        return client;
    }

    public static void mineChannels() {
        Client client = getClient();
        MainApp app = new MainApp(client);
        Mine mine = new Mine(app, client);
        mine._end = aVoid -> {
            client.shutdown();
        };
        try {
            mine.mineChannels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mineUserData() throws IOException, InterruptedException {
        // Calling build() starts connecting.
        List<String> channels = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("channels"));
//        ExecutorService exe = Executors.newFixedThreadPool(3);

        Random rand = new SecureRandom();
        App pp = new App();
        String line = br.readLine();
        int counter = 0;
        while (line != null) {
            if (channels.size() == 30) {
                counter++;
                schedule(channels, rand, pp, counter);
            }
            String[] split = line.split(",");
            if(Integer.parseInt(split[1]) > 1)
                channels.add(split[0]);
            line = br.readLine();
        }
        schedule(channels, rand, pp, counter);
        for (int ix = 0; ix < 5; ix++) {
            pp.execNext();
        }
    }

    private static void schedule(List<String> channels, Random rand, App pp, int counter) {
        System.out.println("dd "+ counter);
        final Integer cnt = counter;
        List<String> lt = new ArrayList<>(channels);
        channels.clear();
        Thread thread = new Thread(() -> {
            int random = rand.nextInt(lt.size());
//                    String nick = lt.get(random).replaceAll("[^a-zA-Z]", "");
            String nick = generateName();
            Client client = getClient();
            MainApp app = new MainApp(client);
            Mine mine = new Mine(app, client);
            mine._end = aVoid -> {
                client.shutdown();
                pp.execNext();
            };
            try {
                mine.mine(lt, cnt);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        if (counter >= 244 && counter <= 10000) {
            pp.threads.add(thread);
        }
    }

    public static String generateName() {
        JSONObject name = getRealName();
        String nam = name.getString("first") + name.getString("last");
        return nam.replaceAll("[^a-zA-Z]", "");
    }

    private static JSONObject getRealName() {
        String s = null;
        try {
            s = getHTML("https://randomuser.me/api/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("str  " + s);
        JSONObject obj = new JSONObject(s);
        return obj.getJSONArray("results").getJSONObject(0).getJSONObject("name");
    }

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}
