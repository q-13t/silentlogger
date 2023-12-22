[![](https://jitpack.io/v/q-13t/silentlogger.svg)](https://jitpack.io/#q-13t/silentlogger)

# About

Silent Logger is a simple logging class that provides nifty features for project debugging. Please refer to [SilentLogger Features](#features) or [SilentLoggerManager Features](#features-1).

# How to get

Please refer to [jitpack](https://jitpack.io/#q-13t/silentlogger), or follow this instruction:

```xml
Step 1. Add the JitPack repository to your build file
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

Step 2. Add the dependency
        <dependency>
	    <groupId>com.github.q-13t</groupId>
	    <artifactId>silentlogger</artifactId>
	    <version>-9bfa4c690a-1</version>
	</dependency>
```

# Insight

Please refer to [Example](https://github.com/q-13t/silentlogger/blob/master/src/main/java/util/q_13t/Example.java).

## SilentLogger

This is a base logger for all the classes you can desire. You want to use this class for all the logging.

### Features

- Code execution time evaluation.
- Optional forwarding of log messages to files. Refer to [SilentLoggerManager Features](#features-1).
- 3 types of log messages.
- Builder declaration.

### Usage

```java
// The log message has format: YYYY-MM-DD:HH:MM:SS.ms OPTION [Class]: Message

//Declaration
// It is recommended to declare this variable as class field.
    SilentLogger logger = SilentLogger
                .getLogger(Main.class)// Setts class name for this logger
                .setLogToFile(true);// Setts that logs from this class need to be saved to file
// In class usage
    logger.debug("This is a debug message");
    logger.info("This is a info message");
    logger.error("This is a error message",error);
    logger.startTimeTest();
    logger.startTimeTest();
    logger.endTimeTest();
```

## SilentLoggerManager

Is a controller class that allows users to configure behavior of log messages.

### Features

- Saving logs to default or specific folder.
- Periodically displaying applications memory status.
- Defining status logging period.
- Logging applications status to Files.
- Logging memory status at specific instant.

### Usage

```java
//Declaration
// It is recommended to declare this variable as class field.
SilentLoggerManager SLM = new SilentLoggerManager()
            .enableFileLogger()// Enables logging to file
            // .enableFileLogger("C\\fake path\\fake dir") // Same as .enableFileLogger() but directory is user specific
            .setPeriodicallyDisplayMemoryStatus(5_000)// Enables and setts period between memory logs
            // Message has format: YYYY-MM-DD:HH:MM:SS.ms I [SilentLoggerManager]: APP Info: Memory In Use: X kilobytes | Free Memory: X kilobytes
            .setLogToFile(true); // informs that System Status logs needs to be saved
// In class usage
SLM.displayMemoryStatus();
```

# PS

In general this is by far not the best logger available but I gave it a shot and created the one i wanted and needed.
Also this logger is not for production if you feel like trying and modifying it go ahead, but remember to create forks and pull requests.
