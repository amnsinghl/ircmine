package ircmine;

import java.util.function.Consumer;

/**
 * Created by aman on 16/11/16.
 */
public class Commands {
    public static class JoinCommand extends Command {
        String _channelName;
        public JoinCommand(String channelNam, Consumer<String> onData, Consumer<Void> onEnd) {
            super("JOIN " + channelNam, onData, onEnd);
            _channelName = channelNam;
        }

        @Override
        public boolean isEndString(String s) {
            return s.contains(_channelName + " :End of WHO") || s.contains(_channelName + ":Cannot join channel");
        }
    }

    public static class WhoCommand extends Command {
        String _channelName;

        public WhoCommand(String chName, Consumer<String> onData, Consumer<Void> onEnd) {
            super("WHO "+ chName, onData, onEnd);
            _channelName = chName;
        }

        @Override
        public boolean isEndString(String s) {
            return s.contains(_channelName + " :End of /WHO");
        }
    }
}