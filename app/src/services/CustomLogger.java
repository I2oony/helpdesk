package services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class CustomLogger extends Logger {

    private static Formatter formatter = new Formatter() {
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
    };

    private static FileHandler fileHandler = createFileHandler(formatter);

    public CustomLogger(String loggerName) {
        super(loggerName, null);
        this.setLevel(Level.INFO);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(formatter);
        this.addHandler(handler);
        this.addHandler(fileHandler);
    }

    public static FileHandler createFileHandler(Formatter formatter) {
        try {
            FileHandler fileHandler = new FileHandler("logs_%u.%g.log", 100000, 20, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(formatter);
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
