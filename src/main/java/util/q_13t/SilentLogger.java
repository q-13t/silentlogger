package util.q_13t;

import java.time.LocalDateTime;

public class SilentLogger {
    private static final String GENERIC_FORMAT = "%s\t%c [%s]\t\t\t: %s";
    private String className = "";

    private SilentLogger(Class<?> name) {
        setClassName(name);
    }

    private void setClassName(Class<?> name) {
        className = name.getSimpleName();
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
    }
}