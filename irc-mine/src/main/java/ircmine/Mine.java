package ircmine;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Mine {
    private final MainApp _app;
    private final Client _client;
    public Consumer<Void> _end;
    //    private List<String> _channels = new ArrayList<>();
    public Random random = new Random();

    public Mine(MainApp app, Client client) {
        _app = app;
        _client = client;
    }

    public void mineChannels() throws IOException {
        BufferedWriter chFile = getFile("channels");
        _app.isReady = aVoid -> {
            _app.execute(new Command("LIST", data -> {
                String[] split = data.split(_client.getNick());
                if (split.length >= 2) {
                    if (split[1].trim().startsWith("#")) {
                        String[] info = split[1].trim().split(" ", 3);
                        writeToFile(chFile, info[0] + "," + info[1] + "," + info[2]);
                    }
                }
            }, end -> {
                close(chFile);
                _end.accept(null);
            }));
        };
    }

    public void mine(List<String> channels, int count) throws IOException, InterruptedException {
        BufferedWriter chmem = getFile("chmem" + count);
        BufferedWriter memDet = getFile("memData" + count);
        _app.isReady = aVoid -> {
            new Thread(() -> {
                for (int j = 0; j < channels.size(); j++) {
//                    try {
//                        Thread.sleep(random.nextInt(30000));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    System.out.println("processing channel " + count + "  " + j);
                    String ch = channels.get(j);
                    _app.executeWait(new Commands.JoinCommand(ch, null, bVoid -> {
                        Channel cha = _client.getChannel(ch).get();
                        List<User> users = cha.getUsers();
                        for (User u : users) {
                            if (!u.getNick().equals(_client.getNick())) {
                                writeToFile(chmem, ch + "," + u.getNick());
                                writeToFile(memDet, u.getNick() + "," + u.getHost() + "," + u.getRealName().orElse("") + "," + u.getServer().orElse(""));
                            }
                        }
                    }));
                }
                System.out.println("finishing up " + count);
                close(chmem);
                close(memDet);
                _end.accept(null);
            }).start();
        };

//        CountDownLatch cdl = new CountDownLatch(1);
//        _app.isReady = aVoid -> {
//            _app.execute(new Command("LIST", data -> {
//                String[] split = data.split(_client.getNick());
//                if (split.length >= 2) {
//                    if (split[1].trim().startsWith("#")) {
//                        String[] info = split[1].trim().split(" ", 3);
//                        _channels.add(info[0]);
//                        writeToFile(chFile,info[0] + "," + info[1] + "," + info[2]);
//
//                    }
//                }
//            }, end -> {
//                close(chFile);

//                System.out.println(channels.size());
//                int i=0;
//                _app.executeWait(new Commands.JoinCommand("hola", s -> {

//                    String[] split = s.split(_client.getNick());
//                    if(split[1].trim().startsWith("=")) {
//                        System.out.println("found names " + split[2]);
//                    }
//                    Channel cha = _client.getChannel(ch).get();
//                    List<User> users = cha.getUsers();
//                    for (User u : users) {
//                        if (u.getNick().equals(_client.getNick()))
//                            continue;
//                        writeToFile(chmem, ch + "," + u.getNick());
//                        writeToFile(memDet, u.getNick() + "," + u.getHost() + "," + u.getRealName().orElse("") + "," + u.getServer().orElse(""));
//                    }
//                    cha.part();
//                }, aVoid1 -> {
//                        _client.addChannel();

//                }));

//            cdl.countDown();
//            _client.shutdown();
//            }));
//        };
//        cdl.await();
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

    void writeToFile(BufferedWriter writer, String st) {
        try {
            writer.write(st + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
