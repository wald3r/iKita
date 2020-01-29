package at.ikita.tests.helper;

import at.ikita.Main;
import at.ikita.configs.AppConfiguration;
import at.ikita.configs.AppConfiguration2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
    * Tests for {@link AppConfiguration2}
    *
    * @author lucas.markovic@student.uibk.ac.at
 */


@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class AppConfiguration2Test {

    @Autowired
    AppConfiguration2 appConfig;

    @Test
    public void testOpeningTimes()
    {
        LocalTime localTimeOpen1 = LocalTime.of(8, 0);
        LocalTime localTimeOpen2 = LocalTime.of(0, 0);

        Assert.assertEquals(appConfig.getOpeningTime(Calendar.MONDAY), localTimeOpen1);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.TUESDAY), localTimeOpen1);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.WEDNESDAY), localTimeOpen1);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.THURSDAY), localTimeOpen1);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.FRIDAY), localTimeOpen1);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.SATURDAY), localTimeOpen2);
        Assert.assertEquals(appConfig.getOpeningTime(Calendar.SUNDAY), localTimeOpen2);
    }

    @Test
    public void testClosingTimes()
    {
        LocalTime localTimeClose1 = LocalTime.of(18, 0);
        LocalTime localTimeClose2 = LocalTime.of(16, 0);
        LocalTime localTimeClose3 = LocalTime.of(0, 0);

        Assert.assertEquals(appConfig.getClosingTime(Calendar.MONDAY), localTimeClose1);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.TUESDAY), localTimeClose1);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.WEDNESDAY), localTimeClose1);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.THURSDAY), localTimeClose1);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.FRIDAY), localTimeClose2);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.SATURDAY), localTimeClose3);
        Assert.assertEquals(appConfig.getClosingTime(Calendar.SUNDAY), localTimeClose3);
    }

    @Test
    public void testTimeConverters()
    {
        LocalTime localTime1 = LocalTime.of(3, 14);
        Date date1 = AppConfiguration2.dateFromLocalTime(localTime1);

        Assert.assertEquals(date1.getHours(), 4);
        Assert.assertEquals(date1.getMinutes(), 14);


        Date date2 = new Date(2000, 1, 2, 3, 4, 5);
        LocalTime localTime2 = AppConfiguration2.localTimeFromDate(date2);

        Assert.assertEquals(localTime2.getHour(), 3);
        Assert.assertEquals(localTime2.getMinute(), 4);
    }

}
