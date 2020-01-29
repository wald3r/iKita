package at.ikita.ui.controllers.helper;


import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper to hold many other objects
 *
 * The indices of the lists stick together
 *
 * @author Lucas Markovic <lucas.markovic@student.uibk.ac.at>
 */
public class DataWrapper {

    private List<Object> dataList;
    private List<String> dataTyps;
    private List<String> dataDescriptions;


    public DataWrapper() {
        dataList = new ArrayList<>();
        dataTyps = new ArrayList<>();
        dataDescriptions = new ArrayList<>();
    }


// methods =========================================================================

    // add
    public void addData(Object object, String typ, String description)
    {
        dataList.add(object);
        dataTyps.add(typ);
        dataDescriptions.add(description);
    }
    public void addData(Object object, String typ)
    {
        dataList.add(object);
        dataTyps.add(typ);
        dataDescriptions.add("none");
    }

    // set
    //public void setData()

    // get
    public Object getData(int index) {
        return dataList.get(index);
    }
    public String getDataTyp(int index) {
        return dataTyps.get(index);
    }
    public String getDataDescription(int index) {
        return dataDescriptions.get(index);
    }

    public String getDataAsString(int index) {
        return (String)dataList.get(index);
    }
    public Integer getDataAsInteger(int index) {
        return (Integer) dataList.get(index);
    }
    public Double getDataAsDouble(int index) {
        return (Double) dataList.get(index);
    }
    public Boolean getDataAsBoolean(int index) {
        return (Boolean)dataList.get(index);
    }


// setter ======================================================================
    /*public void setDataAsBoolean(int index)
    {
        if(getDataAsBoolean(index) == true)
            dataList.set(index, false);
        else
            dataList.set(index, true);
    }*/

}
