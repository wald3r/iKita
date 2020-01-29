package at.ikita.ui.controllers.helper;

/**
 * This class is a wrapper to hold a string with getter and setter.
 * I know its really sad... but this is needed for some xhtml stuff.
 *
 * @author Lucas Markovic <lucas.markovic@student.uibk.ac.at>
 */
public class StringHolder {

    private String value;

    public StringHolder() {}

    public String getValue() { return value; }
    public void setValue(String str) { value = str; }

}
