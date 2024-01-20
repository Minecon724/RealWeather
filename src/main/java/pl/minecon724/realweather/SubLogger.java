package pl.minecon724.realweather;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SubLogger {
    private static Logger LOGGER;
    private static boolean ENABLED;
    private String name;

    static void init(Logger logger, boolean enabled) {
        LOGGER = logger;
        ENABLED = enabled;
    }

    public SubLogger(String name) {
        this.name = name;
    }

    public void log(Level level, String format, Object... args) {
        if (!ENABLED) return;

        Object[] combinedArgs = new Object[args.length + 1];
        combinedArgs[0] = name;
        System.arraycopy(args, 0, combinedArgs, 1, args.length);

        LOGGER.log(level, String.format("[%s] " + format, combinedArgs));
    }

    public void info(String format, Object... args) {
        this.log(Level.INFO, format, args);
    }
}
