package at.ikita.tests.helper;

import at.ikita.Main;
import at.ikita.helper.GenderHelper;
import at.ikita.model.Gender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Tests for {@link GenderHelper}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class GenderHelperTest {

    @Test
    public void testTranslateEnumToString() {
        Assert.assertEquals(GenderHelper.MALE, GenderHelper.translateEnumToString(Gender.MALE));
        Assert.assertEquals(GenderHelper.FEMALE, GenderHelper.translateEnumToString(Gender.FEMALE));
        Assert.assertEquals(GenderHelper.SPECIAL, GenderHelper.translateEnumToString(Gender.OTHER));
    }

    @Test
    public void testTranslateStringToEnum() {
        Assert.assertEquals(Gender.MALE, GenderHelper.translateStringToEnum(GenderHelper.MALE));
        Assert.assertEquals(Gender.FEMALE, GenderHelper.translateStringToEnum(GenderHelper.FEMALE));
        Assert.assertEquals(Gender.OTHER, GenderHelper.translateStringToEnum(GenderHelper.SPECIAL));
    }

    @Test
    public void testTranslationSanity() {
        Assert.assertEquals(GenderHelper.MALE,
                GenderHelper.translateEnumToString(GenderHelper.translateStringToEnum(GenderHelper.MALE)));
        Assert.assertEquals(GenderHelper.FEMALE,
                GenderHelper.translateEnumToString(GenderHelper.translateStringToEnum(GenderHelper.FEMALE)));
        Assert.assertEquals(GenderHelper.SPECIAL,
                GenderHelper.translateEnumToString(GenderHelper.translateStringToEnum(GenderHelper.SPECIAL)));

        Assert.assertEquals(Gender.MALE,
                GenderHelper.translateStringToEnum(GenderHelper.translateEnumToString(Gender.MALE)));
        Assert.assertEquals(Gender.FEMALE,
                GenderHelper.translateStringToEnum(GenderHelper.translateEnumToString(Gender.FEMALE)));
        Assert.assertEquals(Gender.OTHER,
                GenderHelper.translateStringToEnum(GenderHelper.translateEnumToString(Gender.OTHER)));
    }
}
