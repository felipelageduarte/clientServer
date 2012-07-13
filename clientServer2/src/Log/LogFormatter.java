package Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord rec) {
        StringBuilder buf = new StringBuilder(1000);
        buf.append(rec.getLevel());
        buf.append("   ");
        buf.append(calcDate(rec.getMillis()));
        buf.append("   ");
        buf.append(rec.getThreadID());
        buf.append(":");
        buf.append(rec.getLoggerName());
        buf.append("   ");
        buf.append(formatMessage(rec));
        buf.append("\n");
        return buf.toString();
    }

    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }
}
