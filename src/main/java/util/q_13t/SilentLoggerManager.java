package util.q_13t;

import java.util.LinkedList;
import java.util.Queue;

public class SilentLoggerManager {
    private static String GENERIC_FORMAT = "%s\t%c [%s]\t\t\t: %s";
    private static Queue<String> logQueue = new LinkedList<String>();
    private static Object FileWriterLock = new Object();
    private static FileWriterThread FWT = null;
    private static MemoryLoggerThread MLT = null;

    private static class FileWriterThread extends Thread {

        private static boolean threadIsAlive = true;

        public static void stopThread() {
            synchronized (FileWriterLock) {
                threadIsAlive = false;
                FileWriterLock.notify();
            }
        }

        public FileWriterThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            synchronized (FileWriterLock) {
                try {
                    while (threadIsAlive) {
                        if (!logQueue.isEmpty()) {
                            System.out.println("*Writing To File Imitation*\t\t" + logQueue.poll());
                        }
                        if (logQueue.isEmpty())
                            FileWriterLock.wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("FileWriterThread Thread Stopped");
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
                    long useMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

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

    public SilentLoggerManager setFormat(String format) {
        GENERIC_FORMAT = format;
        return this;
    }

    public SilentLoggerManager setLogToFile(boolean val) {
        if (FWT == null || !FWT.isAlive()) {
            FWT = new FileWriterThread();
            FWT.start();
        }
        return this;
    }

    public static void displayMemoryStatus() {

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
