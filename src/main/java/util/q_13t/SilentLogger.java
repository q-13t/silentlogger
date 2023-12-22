package util.q_13t;

import java.time.LocalDateTime;

/**
 * <p>
 * A Simple logging class that supports logging to the file.
 * </p>
 * <p>
 * It also allows to test the speed of code execution.
 * </p>
 * 
 * @see SilentLoggerManager
 * @see #startTimeTest()
 * @see #endTimeTest()
 * 
 * @author q_13t
 */
public class SilentLogger {
    /** A Class name that will be displayed during logging */
    private String className = "";
    /**
     * Boolean value that notes if log messages for this logger will be saved to
     * file
     */
    private boolean logToFile = false;
    /**
     * A generic format for log messages
     * 
     * @see SilentLoggerManager#getGENERIC_FORMAT()
     */
    private static String GENERIC_FORMAT = SilentLoggerManager.getGENERIC_FORMAT();
    /** A support variable for execution time testing */
    private long timeStart = 0;

    /**
     * A default constructor
     * 
     * @param name of class to which logger is mapped.
     */
    private SilentLogger(Class<?> name) {
        setClassName(name);
    }

    /**
     * Setts class name for this logger,
     * 
     * @param name of class to display during logging
     * @return self
     */
    private SilentLogger setClassName(Class<?> name) {
        className = name.getSimpleName();
        return this;
    }

    /**
     * Allows to set if logs for this logger will be forwarded to
     * {@link FileWriterThread} to be saved to file.
     * <p>
     * Default value is {@code false}
     * 
     * @param val {@code true} to save this logs to file, {@code false} otherwise.
     * @return self
     */
    public SilentLogger setLogToFile(boolean val) {
        logToFile = val;
        return this;
    }

    /**
     * Creates and returns new instance of logger.
     * <p>
     * If {@code null} is passed - logger will display {@code SilentLogger} class
     * name
     * during logging.
     * 
     * @param name of class to be displayed during logging
     * @return self
     */
    public static SilentLogger getLogger(Class<?> name) {
        if (name == null) {
            SilentLogger silentLogger = new SilentLogger(SilentLogger.class);
            silentLogger.debug("NULL Value Passed Logger Will Refer To Self");
            return silentLogger;
        } else {
            return new SilentLogger(name);
        }
    }

    /**
     * Creates and returns new instance of logger.
     * <p>
     * Logger will display {@code SilentLogger} class name
     * during logging.
     *
     * @return self
     */
    public static SilentLogger getLogger() {
        SilentLogger silentLogger = new SilentLogger(SilentLogger.class);
        silentLogger.debug("NULL Value Passed Logger Will Refer To Self");
        return silentLogger;
    }

    /**
     * Prints message with {@code D} mapping to console.
     * <p>
     * If {@link #setLogToFile(boolean)} is set to true fill forward message to
     * {@link FileWriterThread} to save it to file.
     * 
     * @see #GENERIC_FORMAT
     * 
     * @param message to be logged
     */
    public void debug(String message) {
        String debug;
        debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'D', className, message);
        System.out.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug);
    }

    /**
     * Prints message with {@code I} mapping to console.
     * <p>
     * If {@link #setLogToFile(boolean)} is set to true fill forward message to
     * {@link FileWriterThread} to save it to file.
     * 
     * @see #GENERIC_FORMAT
     * 
     * @param message to be logged
     */
    public void info(String message) {
        String debug;
        debug = String.format(GENERIC_FORMAT, LocalDateTime.now(), 'I', className, message);
        System.out.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug);
    }

    /**
     * Prints message with {@code E} mapping to console and error with cause
     * followed with stack trace.
     * <p>
     * If {@link #setLogToFile(boolean)} is set to {@code true} fill forward message
     * to
     * {@link FileWriterThread} to save it to file.
     * 
     * @see #GENERIC_FORMAT
     * 
     * @param message to be logged
     */
    public void error(String message, Throwable error) {
        StringBuilder debug = new StringBuilder();
        debug.append(
                String.format(GENERIC_FORMAT, LocalDateTime.now(), 'E', className, message) + error.getMessage()
                        + "\n");
        for (StackTraceElement throwable : error.getStackTrace()) {
            debug.append(throwable.getLineNumber() + " " + throwable.toString() + "\n");
        }
        System.err.println(debug);
        if (logToFile)
            SilentLoggerManager.informFileWriter(debug.toString());
    }

    /**
     * Memorizes milliseconds to test code execution speed.
     * 
     * @see #checkpointTimeTest()
     * @see #endTimeTest()
     * 
     * @return long value of milliseconds
     */
    public long startTimeTest() {
        timeStart = System.currentTimeMillis();
        return timeStart;
    }

    /**
     * <p>
     * Logs how many milliseconds passed since {@link #startTimeTest()} was called
     * or last {@link #checkpointTimeTest()} was called.
     * </p>
     * <p>
     * Unlike {@link #endTimeTest()} this function will reset {@code timeStart} to
     * current time for further chain of checkpoints.
     * </p>
     * 
     * @see #startTimeTest()
     * @see #endTimeTest()
     * 
     * @return long value of milliseconds
     */
    public long checkpointTimeTest() {
        long timeElapsed = System.currentTimeMillis() - timeStart;
        timeStart = System.currentTimeMillis();
        this.debug("Time Elapsed: " + timeElapsed + " milliseconds");
        return timeElapsed;
    }

    /**
     * <p>
     * Logs how many milliseconds passed since {@link #startTimeTest()} was called
     * or last {@link #checkpointTimeTest()} was called.
     * </p>
     * <p>
     * Unlike {@link #checkpointTimeTest()} this function will {@code NOT} reset
     * {@code timeStart} so it can be used to track when each line was executed in
     * regard to trial start.
     * </p>
     * 
     * @see #startTimeTest()
     * @see #checkpointTimeTest()
     * 
     * @return long value of milliseconds
     */
    public long endTimeTest() {
        long timeElapsed = System.currentTimeMillis() - timeStart;
        this.debug("Time Elapsed: " + timeElapsed + " milliseconds");
        return timeElapsed;
    }
}
