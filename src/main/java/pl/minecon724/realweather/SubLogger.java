package pl.minecon724.realweather;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SubLogger {
    // TODO TODO too many static
    private static Logger LOGGER;
    private static boolean ENABLED;
    private String name;

    /**
     * Initialize the SubLogger
     * @param logger parent logger, usually JavaPlugin#getLogger()
     * @param enabled is logging enabled
     */
    static void init(Logger logger, boolean enabled) {
        LOGGER = logger;
        ENABLED = enabled;
    }

    /**
     * Instantiate a SubLogger instance
     * @param name name, it will be prefixing messages
     */
    public SubLogger(String name) {
        this.name = name;
    }

    /**
     * Log a message
     * @param level
     * @param format message, formatted like {@link String#format(String, Object...)}
     * @param args args for formatting
     */
    public void log(Level level, String format, Object... args) {
        if (!ENABLED) return;

        Object[] combinedArgs = new Object[args.length + 1];
        combinedArgs[0] = name;
        System.arraycopy(args, 0, combinedArgs, 1, args.length);
    
        LOGGER.log(level, String.format("[%s] " + format, combinedArgs));
    }

    /**
     * Log an info message
     * see {@link SubLogger#log(Level, String, Object...)}
     * @param format message
     * @param args args
     */
    public void info(String format, Object... args) {
        this.log(Level.INFO, format, args);
    }

    public void info(String message) {
        this.log(Level.INFO, message, new Object[0]);
    }

    /**
     * Log a severe message
     * see {@link SubLogger#log(Level, String, Object...)}
     * @param format message
     * @param args args
     */
    public void severe(String format, Object... args) {
        this.log(Level.SEVERE, format, args);
    }

    public void severe(String message) {
        this.log(Level.SEVERE, message, new Object[0]);
    }

    /**
     * Log a warning message
     * see {@link SubLogger#log(Level, String, Object...)}
     * @param format message
     * @param args args
     */
    public void warning(String format, Object... args) {
        this.log(Level.WARNING, format, args);
    }

    public void warning(String message) {
        this.log(Level.WARNING, message, new Object[0]);
    }
}
