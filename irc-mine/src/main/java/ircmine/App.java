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

    public synchronized void execNext() {
        System.out.println("executing ");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==================== execution " + threads.size() + "===================================");
        Thread pop = threads.pop();
        pop.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        mineChannels();
        mineUserData();
    }

    private static Client getClient()
    {
        String nick = generateName();
        //        Client client = Client.builder().nick(nick).serverHost("irc.us.ircnet.net").serverPort(6667).secure(false).build();
        Client client = Client.builder().nick(nick).serverHost("irc.quakenet.org").serverPort(6667).secure(false).build();
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
            if (channels.size() == 50) {
                counter++;
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
                if(counter >= 0 && counter <= 100) {
                    pp.threads.add(thread);
                }
            }
            String[] split = line.split(",");
            channels.add(split[0]);
            line = br.readLine();
        }
        for(int ix =0;ix<20;ix++) {
            pp.execNext();
        }
    }

    public static String generateName() {
        String s = null;
        try {
            s = getHTML("https://randomuser.me/api/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("str  " +s);
        JSONObject obj = new JSONObject(s);
        JSONObject name = obj.getJSONArray("results").getJSONObject(0).getJSONObject("name");
        String nam = name.getString("first");
        return nam.replaceAll("[^a-zA-Z]", "");
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
