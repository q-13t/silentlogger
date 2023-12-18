package util.q_13t;

import java.util.Queue;
import java.util.LinkedList;
import java.time.LocalDateTime;

public class SilentLogger {
    private static String GENERIC_FORMAT = "%s\t%c [%s]\t\t\t: %s";
    private String className = "";
    private static Queue<String> logQueue = new LinkedList<String>();
    private boolean logToFile = false;
    private static Object FileWriterLock = new Object();
    private FileWriterThread FWT = new FileWriterThread();

    static class FileWriterThread extends Thread {

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

    private SilentLogger(Class<?> name) {
        setClassName(name);
    }

    private void setClassName(Class<?> name) {
        className = name.getSimpleName();
    }

    public static void setFormat(String format) {
        GENERIC_FORMAT = format;
    }

    public void setLogToFile(boolean val) {
        logToFile = val;
        FWT.start();
    }

    private void informFileWriter(String message) {
        synchronized (FileWriterLock) {
            logQueue.add(message);
            FileWriterLock.notify();
        }
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
            informFileWriter(debug);
    }

    @Override
    protected void finalize() throws Throwable {
        FileWriterThread.stopThread();
    }
}
