package util.q_13t;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class SilentLoggerManager {
    private static SilentLogger logger = SilentLogger.getLogger(SilentLoggerManager.class);
    private static String GENERIC_FORMAT = "%s\t%c [%s]\t\t\t: %s";
    private static Queue<String> logQueue = new LinkedList<String>();
    private static Object FileWriterLock = new Object();
    private static FileWriterThread FWT = null;
    private static MemoryLoggerThread MLT = null;

    private static class FileWriterThread extends Thread {
        private static boolean threadIsAlive = true;
        private static File logFile;

        public static void stopThread() {
            synchronized (FileWriterLock) {
                threadIsAlive = false;
                FileWriterLock.notify();
            }
        }

        public FileWriterThread(String directory) {
            setDaemon(true);
            logFile = new File(checkDir(directory), getDate() + ".txt");
            if (!logFile.exists()) {
                try {
                    if (logFile.createNewFile())
                        logger.info("Created new Log File");
                } catch (IOException e) {
                    logger.error("Exception At creating new File", e);
                }
            }
        }

        private void updateLogFile() {
            logFile = new File(checkDir(null), getDate() + ".txt");
            if (!logFile.exists()) {
                try {
                    if (logFile.createNewFile())
                        logger.info("Created new Log File");

                } catch (IOException e) {
                    logger.error("Exception At creating new File", e);
                }
            }
        }

        public static String getDate() {
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH);
        }

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

    private static class MemoryLoggerThread extends Thread {
        private static SilentLogger logger = SilentLogger.getLogger(MemoryLoggerThread.class).setLogToFile(true);
        private static int messageInterval = 10_000;
        private static boolean threadIsAlive = true;

        public MemoryLoggerThread(int interval) {
            setDaemon(true);
            messageInterval = interval;
        }

        public static void stopThread() {
            threadIsAlive = false;
        }

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

    public SilentLoggerManager setLogToFile(Boolean val) {
        logger.setLogToFile(val);
        return this;
    }

    public SilentLoggerManager enableFileLogger(boolean b) {
        if (FWT == null || !FWT.isAlive()) {
            FWT = new FileWriterThread(null);
            FWT.start();
        }
        return this;
    }

    public SilentLoggerManager enableFileLogger(String b) {
        if (FWT == null || !FWT.isAlive()) {
            FWT = new FileWriterThread(b);
            FWT.start();
        }
        return this;
    }

    public static void displayMemoryStatus() {
        logger.info("APP Info: Memory In Use: "
                + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
                + " kilobytes | Free Memory: "
                + (Runtime.getRuntime().freeMemory() / 1024) + " kilobytes");
    }

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

    protected static void informFileWriter(String message) {
        synchronized (FileWriterLock) {
            logQueue.add(message);
            FileWriterLock.notify();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        FileWriterThread.stopThread();
        MemoryLoggerThread.stopThread();
    }

    public static String getGENERIC_FORMAT() {
        return GENERIC_FORMAT;
    }

}
