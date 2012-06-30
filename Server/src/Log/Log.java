/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Log;

import java.io.IOException;
import java.util.logging.*;

class LogLevel extends Level {
    public LogLevel(String name, int value) {
        super(name, value);
    }
}

public class Log {

    private static Log SINGLETON = null;
    private Logger logger;
    private static FileHandler fileTxt;
    private static ConsoleHandler console;
    private static LogFormatter logFormatter;
    private static FileHandler fileHTML;
    private static boolean append = true;
    private static Formatter formatterHTML;
    
    private static final Level DEBUG = new LogLevel("DEBUG", Level.FINE.intValue());
    private static final Level INFO = new LogLevel("INFO", Level.INFO.intValue());
    private static final Level WARNING = new LogLevel("WARN", Level.WARNING.intValue());
    private static final Level ERROR = new LogLevel("ERROR", Level.WARNING.intValue()+1);
    private static final Level FATAL = new LogLevel("FATAL", Level.SEVERE.intValue());

    private Log() throws IOException {

        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for (Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }
        
        fileTxt = new FileHandler("iGeom.log", append);
        fileHTML = new FileHandler("iGeom.html");
        console = new ConsoleHandler();

        // Create txt Formatter
        logFormatter = new LogFormatter();
        fileTxt.setFormatter(logFormatter);
        console.setFormatter(logFormatter);
        formatterHTML = new HtmlFormatter();
        fileHTML.setFormatter(formatterHTML);
        
        // Create Logger        
        logger = Logger.getLogger(callMeAnyTime());
        logger.setLevel(Level.ALL);
        logger.addHandler(fileTxt);
        logger.addHandler(console);
        logger.addHandler(fileHTML);

        LogManager lm = LogManager.getLogManager();
        lm.addLogger(logger);
    }

    private String callMeAnyTime() {
        String answer = "";
        try {
            throw new Exception("Who called me?");
        } catch (Exception e) {
            answer = e.getStackTrace()[4].getClassName() + "."
                    + e.getStackTrace()[4].getMethodName();
        }
        return answer;
    }

    private static Log getInstance() throws IOException {
        if (SINGLETON == null) {
            SINGLETON = new Log();
        }
        //SINGLETON.config();
        return SINGLETON;
    }

    public static void debug(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("DEBUG"), message);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void info(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("INFO"), message);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void warn(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("WARN"), message);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void error(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("ERROR"), message);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void fatal(String message) {
        try {
            Log.getInstance().logger.log(Level.parse("FATAL"), message);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

