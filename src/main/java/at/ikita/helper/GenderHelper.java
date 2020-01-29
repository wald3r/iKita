package at.ikita.helper;

import at.ikita.model.Gender;

/**
 * Simple helper class for translating Genders to German Strings.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public class GenderHelper {

    public static final String MALE = "Männlich";
    public static final String FEMALE = "Weiblich";
    public static final String SPECIAL = "Special";

    public static String translateEnumToString(Gender gender) {
        switch (gender) {
            case MALE:
                return MALE;
            case FEMALE:
                return FEMALE;
            default:
                return SPECIAL;
        }
    }

    public static Gender translateStringToEnum(String string) {
        switch (string) {
            case MALE:
                return Gender.MALE;
            case FEMALE:
                return Gender.FEMALE;
            default:
                return Gender.OTHER;
        }
    }
}
