package at.ikita.tests.helper;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.Gender;
import at.ikita.ui.controllers.helper.DataWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link DataWrapper}
 *
 * @author lucas.markovic@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class DataWrapperTest {

    Child child;
    Date birthday;

    ChildCalendar childCalendar;
    Date day;


    @Before
    public void setupTestData()
    {
        birthday = new Date(2000, 3, 14);
        child = Child.create("Lee", "Mohammed", birthday);
        child.setGender(Gender.FEMALE);

        day = new Date(2017, 4, 20);
        childCalendar = ChildCalendar.create(day, child);
    }

    @Test
    public void testDataWrapperAddGet()
    {
        String dataTyp1 = "typ1";
        String dataDescription1 = "description1";
        Object obj2 = new Object();
        String dataTyp2 = "typ2";

        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.addData(child, "typ0", "description0");
        dataWrapper.addData(childCalendar, dataTyp1, dataDescription1);
        dataWrapper.addData(obj2, dataTyp2);

        // testing object 0: child
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getFirstName(), "Mohammed");
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getLastName(), "Lee");
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getBirthday().getYear(), 2000);
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getBirthday().getMonth(), 3);
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getBirthday().getDate(), 14);
        Assert.assertEquals(((Child)dataWrapper.getData(0)).getGender(), Gender.FEMALE);
        Assert.assertEquals(dataWrapper.getDataTyp(0), "typ0");
        Assert.assertEquals(dataWrapper.getDataDescription(0), "description0");

        // testing object 1: childcalendar
        Assert.assertEquals(((ChildCalendar)dataWrapper.getData(1)).getDate().getYear(), 2017);
        Assert.assertEquals(((ChildCalendar)dataWrapper.getData(1)).getDate().getMonth(), 4);
        Assert.assertEquals(((ChildCalendar)dataWrapper.getData(1)).getDate().getDate(), 20);
        Assert.assertEquals(((ChildCalendar)dataWrapper.getData(1)).getChild().equals(child), child.equals(((ChildCalendar)dataWrapper.getData(1)).getChild()));
        Assert.assertEquals(dataWrapper.getDataTyp(1), dataTyp1);
        Assert.assertEquals(dataWrapper.getDataDescription(1), dataDescription1);

        // testing object 2: object
        Assert.assertEquals(dataWrapper.getData(2), obj2);
        Assert.assertEquals(dataWrapper.getDataTyp(2), dataTyp2);

    }

    @Test
    public void testDataWrapperCasted()
    {
        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.addData(new Integer(-1), "Integer");
        dataWrapper.addData(new Integer(1), "Integer");
        dataWrapper.addData(new Double(3.14), "Double");
        dataWrapper.addData(new String("möp"), "String");

        Assert.assertEquals(dataWrapper.getDataAsInteger(0), new Integer(-1));
        Assert.assertEquals(dataWrapper.getDataAsInteger(1), new Integer(1));
        Assert.assertEquals(dataWrapper.getDataAsDouble(2), new Double(3.14));
        Assert.assertEquals(dataWrapper.getDataAsString(3), new String("möp"));
    }


}
