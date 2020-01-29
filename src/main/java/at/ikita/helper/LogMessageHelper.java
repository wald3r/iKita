package at.ikita.helper;

import at.ikita.model.LogMessageType;

public class LogMessageHelper {

    public static final String INFORMATION = "INFO.";
    public static final String WARNING = "WARN!";
    public static final String AUDIT = "AUDIT";

    public static String translateEnumToString(LogMessageType type) {
        switch (type) {
            case INFORMATION:
                return INFORMATION;
            case WARNING:
                return WARNING;
            case AUDIT:
                return AUDIT;
            default:
                throw new IllegalArgumentException("Unexpected Log Message Type");
        }
    }

}
