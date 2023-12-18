package util.q_13t;

import java.time.LocalDateTime;

public class SilentLogger {
    private String className = "";
    private boolean logToFile = false;
    private static String GENERIC_FORMAT = SilentLoggerManager.getGENERIC_FORMAT();

    private SilentLogger(Class<?> name) {
        setClassName(name);
    }

    private SilentLogger setClassName(Class<?> name) {
        className = name.getSimpleName();
        return this;
    }

    public SilentLogger setLogToFile(boolean val) {
        logToFile = val;
        return this;
    }

    public static SilentLogger getLogger(Class<?> name) {
        if (name == null) {
            SilentLogger silentLogger = new SilentLogger(SilentLogger.class);
            silentLogger.debug("NULL Value Passed Logger Will Refer To Self");
            return silentLogger;
        } else {
            return new SilentLogger(name);
        }
    }

    public static SilentLogger getLogger() {
        SilentLogger silentLogger = new SilentLogger(SilentLogger.class);
        silentLogger.debug("NULL Value Passed Logger Will Refer To Self");
        return silentLogger;
    }

    public void debug(String message) {
        String debug;
        if (className.isEmpty()) {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'D', getClass().getSimpleName(), message);
        } else {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'D', className, message);
        }
        System.out.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug);
    }

    public void info(String message) {
        String debug;
        if (className.isEmpty()) {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'I', getClass().getSimpleName(), message);
        } else {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'I', className, message);
        }
        System.out.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug);
    }

    public void error(String message, Throwable error) {
        String debug;
        if (className.isEmpty()) {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'E', getClass().getSimpleName(), message)
                    + error.getStackTrace();
        } else {
            debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'E', className, message)
                    + error.getStackTrace();
        }
        System.err.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug);
    }

}
