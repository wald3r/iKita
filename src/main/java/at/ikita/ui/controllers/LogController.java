package at.ikita.ui.controllers;

import java.io.Serializable;
import java.util.*;

import at.ikita.helper.LogMessageHelper;
import at.ikita.model.LogMessage;
import at.ikita.model.LogMessageType;
import at.ikita.services.LogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Controller for the Audit Log View
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("view")
public class LogController implements Serializable {

    private LogService logService;

    private Calendar startDate;
    private Calendar endDate;
    private String logMessageType = "ALL";
    private List<String> logMessageTypes;

    private List<LogMessage> logMessageList;

    @Autowired
    public LogController(LogService logService) {
        Assert.notNull(logService);
        this.logService = logService;

        logMessageTypes = new ArrayList<>(4);
        logMessageTypes.add("ALL");
        logMessageTypes.add("AUDIT");
        logMessageTypes.add("INFORMATION");
        logMessageTypes.add("WARNING");

        setToday();
        refresh();
    }

    private void setToday() {
        startDate = GregorianCalendar.getInstance();
        startDate.set(Calendar.HOUR, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate = GregorianCalendar.getInstance();
        endDate.set(Calendar.HOUR, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
    }

    public void refresh() {
        if (logMessageType == null || logMessageType.equals("ALL"))
            logMessageList = logService.loadByTime(startDate.getTime(), endDate.getTime());
        else
            logMessageList = logService.loadByTimeAndType(startDate.getTime(), endDate.getTime(),
                    typeFromString(logMessageType));

    }

    private LogMessageType typeFromString(String type) {
        return LogMessageType.valueOf(type);
    }

    public void skipOneDayBackward() {
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);
        refresh();
    }

    public void skipOneDayForward() {
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, 1);
        refresh();
    }

    public String getDateString() {
        return new DateFormatter().print(startDate.getTime(), Locale.GERMAN);
    }

    public String getLogTypeString(LogMessage message) {
        return LogMessageHelper.translateEnumToString(message.getType());
    }

    public List<String> getLogMessageTypes() {
        return logMessageTypes;
    }

    public void setLogMessageTypes(List<String> logMessageTypes) {
        this.logMessageTypes = logMessageTypes;
    }

    public String getLogMessageType() {
        return logMessageType;
    }

    public void setLogMessageType(String logMessageType) {
        this.logMessageType = logMessageType;
    }

    public List<LogMessage> getLogMessageList() {
        return logMessageList;
    }

}
