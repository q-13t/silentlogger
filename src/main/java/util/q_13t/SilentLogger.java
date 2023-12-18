package util.q_13t;

import java.sql.Time;
import java.util.Date;

public class SilentLogger {
    private String className = "";

    public SilentLogger(Class<?> name) {
        if (name != null) {
            className = name.getSimpleName();
        } else {
            debug("NULL Value Passed");
        }
    }

    public void debug(String message) {
        if (className.isEmpty()) {
            System.out.println(new Time(new Date().getTime()) + " D SilentLogger: " + message);
        } else {
            System.out.println(new Time(new Date().getTime()) + " D " + className + ": " + message);
        }
    }
}