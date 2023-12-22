package util.q_13t;


public class Example {
    static SilentLogger logger = SilentLogger
            .getLogger(Example.class)// Setts class name for this logger
            .setLogToFile(true);// Setts that logs from this class need to be saved to file
    static SilentLoggerManager SLM = new SilentLoggerManager()
            .enableFileLogger()// Enables logging to file
            // .enableFileLogger("C\\fake path\\fake dir") // Same as .enableFileLogger()
            // but directory is user specific
            .setPeriodicallyDisplayMemoryStatus(5_000)// Enables and setts period between memory status logs
            .setLogToFile(true); // informs that SLMs logs needs to be saved

    public static void main(String[] args) throws InterruptedException {

        logger.startTimeTest();
        StringBuilder SB = new StringBuilder();
        for (int i = 0; i < 100_000; i++) {
            SB.append("a");
        }
        logger.checkpointTimeTest();
        logger.checkpointTimeTest();
        String str = "";
        for (int i = 0; i < 100_000; i++) {
            str += "a";
        }
        logger.endTimeTest();
        SLM.displayMemoryStatus();
        try {
            System.out.println(15 / 0);
        } catch (Exception e) {
            logger.error("Message ", e);
        }

        for (int i = 1; i <= 25; i++) {
            logger.info("Value is " + i);
        }
    }

}
////////////////////////////////////////////////////// EXAMPLE LOG//////////////////////////////////////////////////////////////////////////

// 2023-12-22T15:52:17.024 I [MemoryLoggerThread]                  : APP Info: Memory In Use: 3993 kilobytes | Free Memory: 121958 kilobytes
// 2023-12-22T15:52:17.024 D [Example]                     : Time Elapsed: 12 milliseconds 
// 2023-12-22T15:52:17.128 D [Example]                     : Time Elapsed: 140 milliseconds
// 2023-12-22T15:52:21.927 D [Example]                     : Time Elapsed: 4796 milliseconds
// 2023-12-22T15:52:21.928 I [SilentLoggerManager]                 : APP Info: Memory In Use: 177510 kilobytes | Free Memory: 459929 kilobytes
// 2023-12-22T15:52:21.929 E [Example]                     : Message / by zero
// 36 util.q_13t.Example.main(Example.java:36)

// 2023-12-22T15:52:21.935 I [Example]                     : Value is 1
// 2023-12-22T15:52:21.936 I [Example]                     : Value is 2
// 2023-12-22T15:52:21.936 I [Example]                     : Value is 3
// 2023-12-22T15:52:21.937 I [Example]                     : Value is 4
// 2023-12-22T15:52:21.938 I [Example]                     : Value is 5
// 2023-12-22T15:52:21.938 I [Example]                     : Value is 6
// 2023-12-22T15:52:21.939 I [Example]                     : Value is 7
// 2023-12-22T15:52:21.940 I [Example]                     : Value is 8
// 2023-12-22T15:52:21.940 I [Example]                     : Value is 9
// 2023-12-22T15:52:21.941 I [Example]                     : Value is 10
// 2023-12-22T15:52:21.941 I [Example]                     : Value is 11
// 2023-12-22T15:52:21.942 I [Example]                     : Value is 12
// 2023-12-22T15:52:21.942 I [Example]                     : Value is 13
// 2023-12-22T15:52:21.943 I [Example]                     : Value is 14
// 2023-12-22T15:52:21.944 I [Example]                     : Value is 15
// 2023-12-22T15:52:21.946 I [Example]                     : Value is 16
// 2023-12-22T15:52:21.947 I [Example]                     : Value is 17
// 2023-12-22T15:52:21.947 I [Example]                     : Value is 18
// 2023-12-22T15:52:21.948 I [Example]                     : Value is 19
// 2023-12-22T15:52:21.948 I [Example]                     : Value is 20
// 2023-12-22T15:52:21.949 I [Example]                     : Value is 21
// 2023-12-22T15:52:21.949 I [Example]                     : Value is 22
// 2023-12-22T15:52:21.950 I [Example]                     : Value is 23
// 2023-12-22T15:52:21.950 I [Example]                     : Value is 24
// 2023-12-22T15:52:21.952 I [Example]                     : Value is 25
