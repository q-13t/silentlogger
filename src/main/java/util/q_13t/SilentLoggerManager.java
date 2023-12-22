package util.q_13t;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A manager class for logging.
 * <p>
 * It support logging of application's memory usage
 * {@link #displayMemoryStatus()}, and logging to file.
 * 
 * @see #enableFileLogger(String)
 * @see SilentLogger
 * @author q_13t
 */
public class SilentLoggerManager {
    /** {@link SilentLogger} for this SilentLoggerManager */
    private static SilentLogger logger = SilentLogger.getLogger(SilentLoggerManager.class);
    /**
     * A generic log format that is of format :
     * <p>
     * {@code YYYY-MM-DD:HH:MM:SS.ms OPTION [Class]: Message}
     */
    private static String GENERIC_FORMAT = "%s\t%c [%s]\t\t\t: %s";
    /** A queue of log messages to be saved to file */
    private static Queue<String> logQueue = new LinkedList<String>();
    /** A lock for {@link FileWriterThread} synchronization */
    private static Object FileWriterLock = new Object();
    /** Thread that saves log messages to file */
    private static FileWriterThread FWT = null;
    /** Background Thread that periodically logs applications memory status */
    private static MemoryLoggerThread MLT = null;

    /**
     * Class that allows for log messages to be saved to file
     * 
     * @see SilentLoggerManager#enableFileLogger(String)
     * @see SilentLoggerManager#enableFileLogger(boolean)
     * @see SilentLogger#setLogToFile(boolean)
     * @see FileWriterThread#run()
     * @author q_13t
     */
    private static class FileWriterThread extends Thread {
        /** boolean that keeps this thread alive */
        private static boolean threadIsAlive = true;
        /** Current logging file */
        private static File logFile;
        /** A path to logs directory */
        private static String logsPath = "";

        /**
         * Stops thread from execution
         * <p>
         * Some logging messages may be lost if called before application ended
         */
        public static void stopThread() {
            synchronized (FileWriterLock) {
                threadIsAlive = false;
                FileWriterLock.notify();
            }
        }

        /**
         * A constructor of thread. It will either log to existing file for today or
         * create new file.
         * </p>
         * Logging files are created based on {@code day} of execution of code. For each
         * day a new logging file is created.
         * </p>
         * Log file has name like:
         * {@code YYYY-MM-DD.txt}
         * 
         * @see #updateLogFile()
         * 
         * @param directory where logs folder will be saved.
         */
        public FileWriterThread(String directory) {
            setDaemon(true);
            logsPath = directory;
            updateLogFile();
        }

        /**
         * Changes current logging file based on {@code day} of execution of code. For
         * each day a new logging file is created.
         * 
         * @see #checkDir(String)
         */
        private void updateLogFile() {
            logFile = new File(checkDir(logsPath), getDate() + ".txt");
            if (!logFile.exists()) {
                try {
                    if (logFile.createNewFile())
                        logger.info("Created new Log File");

                } catch (IOException e) {
                    logger.error("Exception At creating new File", e);
                }
            }
        }

        /**
         * @return String containing date in format {@code YYYY-MM-DD}
         */
        public static String getDate() {
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH);
        }

        /**
         * Checks whether {@code logs} directory exists, if not creates new in location
         * of {@code directory} passed. If {@code directory} is null or empty string
         * logs directory will be created at the top most directory of the project.
         * 
         * @param directory where logs folder will be saved
         * @return logs directory
         */
        private File checkDir(String directory) {
            File logDir;
            if (directory == null || directory.isEmpty()) {
                logDir = new File("logs");
            } else {
                logDir = new File(directory + "/logs");
            }
            if (!logDir.exists() || !logDir.isDirectory()) {
                logDir.mkdir();
            }
            return logDir;
        }

        /**
         * Main function that {@code APPENDS} logs of current session to log file
         * specified in {@link SilentLoggerManager#enableFileLogger(String)}.
         * It will automatically check if current date corresponds to log file date.
         * 
         * @see #updateLogFile()
         * @see SilentLoggerManager#enableFileLogger(String)
         * @see SilentLogger#setLogToFile(boolean)
         */
        @Override
        public void run() {
            synchronized (FileWriterLock) {
                try {
                    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
                    while (threadIsAlive) {
                        if (!logFile.getName().equals(getDate() + ".txt")) {
                            updateLogFile();
                            pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
                        }
                        if (!logQueue.isEmpty()) {
                            pw.println(logQueue.poll());
                            pw.flush();
                        }
                        if (logQueue.isEmpty())
                            FileWriterLock.wait();
                    }
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A background {@code Daemon} thread that periodically logs memory status of
     * application in format:
     * <p>
     * {@code YYYY-MM-DD:HH:MM:SS.ms I SilentLoggerManager: APP Info: Memory In Use: X kilobytes | Free Memory: X kilobytes}
     * 
     * @see SilentLoggerManager#setPeriodicallyDisplayMemoryStatus(int)
     * @author q_13t
     */
    private static class MemoryLoggerThread extends Thread {
        /** A queue of log messages to be saved to file */
        private static SilentLogger logger = SilentLogger.getLogger(MemoryLoggerThread.class).setLogToFile(true);
        /** Interval between memory status logs */
        private static int messageInterval = 10_000;
        /** boolean that keeps this thread alive */
        private static boolean threadIsAlive = true;

        /**
         * Constructor for this thread.
         * 
         * @see SilentLoggerManager#setPeriodicallyDisplayMemoryStatus(int)
         * 
         * @param interval between memory status logs
         */
        public MemoryLoggerThread(int interval) {
            setDaemon(true);
            messageInterval = interval;
        }

        /**
         * Stops this thread if it is not sleeping
         */
        public static void stopThread() {
            threadIsAlive = false;
        }

        /**
         * Main logging loop that displays memory status of application in format:
         * <p>
         * {@code YYYY-MM-DD:HH:MM:SS.ms I SilentLoggerManager: APP Info: Memory In Use: X kilobytes | Free Memory: X kilobytes}
         */
        @Override
        public void run() {
            try {
                while (threadIsAlive) {
                    long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
                    long useMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                            / 1024;

                    logger.info("APP Info: Memory In Use: "
                            + useMemory + " kilobytes | Free Memory: "
                            + freeMemory + " kilobytes");
                    sleep(messageInterval);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Defines if logs from SilentLoggerManager will be saved to file.
     * 
     * @see MemoryLoggerThread
     * @see FileWriterThread
     * @see SilentLogger#setLogToFile(boolean)
     * @return self
     */
    public SilentLoggerManager setLogToFile(Boolean val) {
        logger.setLogToFile(val);
        return this;
    }

    /**
     * Enable logging to file.
     * 
     * @see FileWriterThread
     * @see FileWriterThread#checkDir(String)
     * @return self
     */
    public SilentLoggerManager enableFileLogger() {
        if (FWT == null || !FWT.isAlive()) {
            FWT = new FileWriterThread(null);
            FWT.start();
        }
        return this;
    }

    /**
     * Controls if logs from this application will be saved to file. If so
     * {@link FileWriterThread} will be started.
     * </p>
     * Default value is {@code false}
     * 
     * @see FileWriterThread#FileWriterThread(String)
     *      * @see FileWriterThread#checkDir(String)
     * @param dir where logs dir will be created
     * @return self
     */
    public SilentLoggerManager enableFileLogger(String dir) {
        if (FWT == null || !FWT.isAlive()) {
            FWT = new FileWriterThread(dir);
            FWT.start();
        }
        return this;
    }

    /**
     * Logs current applications memory status in format:
     * </p>
     * {@code YYYY-MM-DD:HH:MM:SS.ms I SilentLoggerManager: APP Info: Memory In Use: X kilobytes | Free Memory: X kilobytes}
     * 
     * @see SilentLogger#setLogToFile(boolean)
     */
    public void displayMemoryStatus() {
        logger.info("APP Info: Memory In Use: "
                + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
                + " kilobytes | Free Memory: "
                + (Runtime.getRuntime().freeMemory() / 1024) + " kilobytes");
    }

    /**
     * Enables periodical memory status logging.
     * </p>
     * {@code period} can NOT be less than 0. It is recommended to set
     * {@code period} to values greater than 1_000. Default value is 10_000ms (10
     * seconds)
     * 
     * @see MemoryLoggerThread#run()
     * @see MemoryLoggerThread
     * @param period between log messages in milliseconds.
     * @return self
     */
    public SilentLoggerManager setPeriodicallyDisplayMemoryStatus(int period) {
        if (MLT == null || !MLT.isAlive()) {
            if (period <= 0) {
                MLT = new MemoryLoggerThread(10_000);
            } else {
                MLT = new MemoryLoggerThread(period);
            }
            MLT.start();
        }
        return this;
    }

    /**
     * Notifies {@link FileWriterThread} that new messages were added to queue and
     * need to be saved.
     * 
     * @see FileWriterThread#run()
     * 
     * @param message to be added to {@link #logQueue}
     */
    protected static void informFileWriter(String message) {
        synchronized (FileWriterLock) {
            logQueue.add(message);
            FileWriterLock.notify();
        }
    }

    /**
     * Stops both threads from any further execution.
     * 
     * @see FileWriterThread#stopThread()
     * @see MemoryLoggerThread#stopThread()
     */
    @Override
    protected void finalize() throws Throwable {
        FileWriterThread.stopThread();
        MemoryLoggerThread.stopThread();
    }

    /**
     * @return String representing generic log format.
     * @see #GENERIC_FORMAT
     */
    public static String getGENERIC_FORMAT() {
        return GENERIC_FORMAT;
    }

}
