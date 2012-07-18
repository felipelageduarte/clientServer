package Log;

import java.io.IOException;
import java.util.logging.*;

//Class used to create custom log level
class LogLevel extends Level {

    public LogLevel(String name, int value) {
        super(name, value);
    }
}

public class Log {

    private static Log SINGLETON = null;
    private Logger logger;
    // this attribute is used to determine if log file should be truncated
    // or append in the end.
    private static boolean append = true;
    //Handlers
    private static FileHandler fileTxt;
    private static FileHandler fileHTML;
    private static FileHandler fileXML;
    private static ConsoleHandler console;
    //Log Type
    private static final boolean logConsole = true;
    private static final boolean logFile = true;
    private static final boolean logHTML = false;
    private static final boolean logXML = false;

    private Log() throws IOException {

        //Disable all log that is attached to globalLogger by default
        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for (Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }

        logger = Logger.getLogger(callMeAnyTime());
        logger.setLevel(Level.ALL);

        if (logConsole) {
            console = new ConsoleHandler();
            console.setLevel(Level.ALL);
            console.setFormatter(new LogFormatter());
            logger.addHandler(console);
        }
        if (logFile) {
            fileTxt = new FileHandler("iGeom.log", append);
            fileTxt.setLevel(Level.ALL);
            fileTxt.setFormatter(new LogFormatter());
            logger.addHandler(fileTxt);
        }
        if (logHTML) {
            fileHTML = new FileHandler("iGeom.html");
            fileHTML.setLevel(Level.ALL);
            fileHTML.setFormatter(new HtmlFormatter());
            logger.addHandler(fileHTML);
        }
        if (logXML) {
            fileXML = new FileHandler("iGeom.xml");
            fileXML.setLevel(Level.ALL);
            fileXML.setFormatter(new XMLFormatter());
            logger.addHandler(fileXML);
        }

        // creating custom log levels on Logging API. That is the reason why 
        // we don't need to assign the new object to no variable
        new LogLevel("DEBUG", Level.FINE.intValue());
        new LogLevel("INFO", Level.INFO.intValue());
        new LogLevel("WARN", Level.WARNING.intValue());
        new LogLevel("ERROR", Level.WARNING.intValue() + 1);
        new LogLevel("FATAL", Level.SEVERE.intValue());

        LogManager lm = LogManager.getLogManager();
        lm.addLogger(logger);
    }

    private static String callMeAnyTime() {
        String answer = "";
        try {
            throw new Exception("");
        } catch (Exception e) {
            answer = e.getStackTrace()[2].getClassName() + "."
                    + e.getStackTrace()[2].getMethodName();
        }
        return answer;
    }

    private static Log getInstance() throws IOException {
        if (SINGLETON == null) {
            SINGLETON = new Log();
        }
        return SINGLETON;
    }

    public static void debug(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("DEBUG"), callMeAnyTime() + " - " + message);
        } catch (IOException ex) {
        }
    }

    public static void debug(String message, Exception e) {
        try {
            Log.getInstance().logger.log(Level.parse("DEBUG"), callMeAnyTime() + " - " + message + " - " + e.getMessage());
        } catch (IOException ex) {
        }
    }

    public static void info(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("INFO"), callMeAnyTime() + " - " + message);
        } catch (IOException ex) {
        }
    }

    public static void info(String message, Exception e) {
        try {
            Log.getInstance().logger.log(Level.parse("INFO"), callMeAnyTime() + " - " + message + " - " + e.getMessage());
        } catch (IOException ex) {
        }
    }

    public static void warn(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("WARN"), callMeAnyTime() + " - " + message);
        } catch (IOException ex) {
        }
    }

    public static void warn(String message, Exception e) {
        try {
            Log.getInstance().logger.log(Level.parse("WARN"), callMeAnyTime() + " - " + message + " - " + e.getMessage());
        } catch (IOException ex) {
        }
    }

    public static void error(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("ERROR"), callMeAnyTime() + " - " + message);
        } catch (IOException ex) {
        }
    }

    public static void error(String message, Exception e) {
        try {
            Log.getInstance().logger.log(Level.parse("ERROR"), callMeAnyTime() + " - " + message + " - " + e.getMessage());
        } catch (IOException ex) {
        }
    }

    public static void fatal(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("FATAL"), callMeAnyTime() + " - " + message);
        } catch (IOException ex) {
        }
    }

    public static void fatal(String message, Exception e) {
        try {
            Log.getInstance().logger.log(Level.parse("FATAL"), callMeAnyTime() + " - " + message + " - " + e.getMessage());
        } catch (IOException ex) {
        }
    }
}
