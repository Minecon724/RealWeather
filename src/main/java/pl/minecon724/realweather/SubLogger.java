package pl.minecon724.realweather;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SubLogger {
    private static Logger logger;
    private String name;

    static void init(Logger loger) {
        logger = loger;
    }

    public SubLogger(String name) {
        this.name = name;
    }

    public void log(Level level, String format, Object... args) {
        Object[] combinedArgs = new Object[args.length + 1];
        combinedArgs[0] = name;
        System.arraycopy(args, 0, combinedArgs, 1, args.length);

        logger.log(level, String.format("[%s] " + format, combinedArgs));
    }

    public void info(String format, Object... args) {
        this.log(Level.INFO, format, args);
    }
}
