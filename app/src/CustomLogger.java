import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class CustomLogger extends Logger {

    public CustomLogger(String loggerName) {
        super(loggerName, null);
        this.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                Date now = new Date(record.getMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String log = "";
                log = log
                        .concat("[")
                        .concat(dateFormat.format(now))
                        .concat("] - ")
                        .concat(record.getLoggerName())
                        .concat(" - ")
                        .concat(record.getLevel().toString())
                        .concat(" - ")
                        .concat(record.getMessage())
                        .concat("\n");
                return log;
            }
        });
        this.addHandler(handler);
    }
}
