package at.ikita.helper;

import at.ikita.model.PersonRole;

/**
 * Simple helper class for translating Roles to German Strings.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public class PersonRoleHelper {

    public static final String ADMIN = "Administrator";
    public static final String PARENT = "Elternteil";
    public static final String GUARDIAN = "Bezugsperson";
    public static final String TEACHER = "Pädagoge";

    public static String translateEnumToString(PersonRole role) {
        switch (role) {
            case ADMIN:
                return ADMIN;
            case PARENT:
                return PARENT;
            case GUARDIAN:
                return GUARDIAN;
            case TEACHER:
                return TEACHER;
            default:
                throw new IllegalArgumentException("Unexpected Person Role");
        }
    }

}
