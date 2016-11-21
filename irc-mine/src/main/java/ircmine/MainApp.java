package ircmine;

import org.kitteh.irc.client.library.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by aman on 16/11/16.
 */
public class MainApp {
    private final Client _client;
    private List<Command> _commands = new ArrayList<>();
    public Consumer<Void> isReady;
    final Object _lock = new Object();

    public MainApp(Client client) {
        _client = client;
        init();
    }

    private void init() {
        _client.setInputListener(s -> {
//            new Thread(() -> {
                System.out.println("inp " +  s);
            if(s.contains("End of MOTD command")) {
                isReady.accept(null);
            }
            synchronized (_lock) {
                List<Command> remCom = new ArrayList<>();
                for(Command cm : _commands) {
                    if(cm != null) {
                        if(cm.isEndString(s)) {
                            remCom.add(cm);
                        } else {
                            cm.consume(s);
                        }
                    }
                }
                for(Command cm: remCom) {
                    _commands.remove(cm);
                }
                for(Command cm: remCom) {
                    cm.end();
                }
            }
//            }).start();


//            if(state == 0) {
//
//                String[] split = s.split(_client.getNick());
//                if(split.length >= 2) {
//                    if(split[1].trim().startsWith("#")) {
//                        String channel = split[1].trim().split(" ")[0];
//                        _channels.add(channel);
//                    }
//                }
//                if(s.contains("End of /LIST")) {
//                    state = 1;
//                    System.out.println(_channels.size());
//                    for(String channel: _channels) {
//                        _client.sendRawLine("JOIN " + channel);
//                        _client.sendRawLine("WHO " + channel);
//                        _client.sendRawLine("PART " + channel);
//
//                    }
//                }
//            }
//            if(state == 1 ) {
//                String[] split = s.split(_client.getNick());
//                if(split.length>=2) {
//                    if(split[1].trim().startsWith("#")) {
//                        String s1 = split[1].trim().split(" ")[1];
//                        st.add(s1);
//                        System.out.println("user found " + s1 + " " + st.size());
//                    }
//                }
//            }
        });
        _client.setOutputListener(s -> {
            System.out.println("out: " + s);
        });
    }

    public void execute(Command command) {
        synchronized (_lock) {
            _commands.add(command);
            new Thread(() -> {
                _client.sendRawLine(command.getCommand());
            }).start();
        }
    }


    public void executeWait(final Command command) {
        execute(command);
        CountDownLatch latch = new CountDownLatch(1);
        command.end2 = aVoid ->  {
            latch.countDown();
        };
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
