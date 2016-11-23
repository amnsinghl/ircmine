package ircmine;

import java.util.function.Consumer;

/**
 * Created by aman on 16/11/16.
 */
public class Command {
    private final Consumer<String> _consumer;
    private final Consumer<Void> _onEnd;
    private final String _commandName;
    Consumer<Void> end2;

    public Command(String commandName, Consumer<String> onData, Consumer<Void> onEnd) {
        _consumer = onData;
        _onEnd = onEnd;
        _commandName = commandName;
    }

    void consume(String s) {
        new Thread(() -> {
            if(_consumer != null)
                _consumer.accept(s);
        }).start();
    }

    void end() {
        new Thread(() -> {
            if(_onEnd!= null)
                _onEnd.accept(null);
            if(end2 != null)
                end2.accept(null);
        }).start();
    }

    public boolean isEndString(String s) {
        return s.contains("End of " + _commandName);
    }

    String getCommand() {
        return _commandName;
    }
}
