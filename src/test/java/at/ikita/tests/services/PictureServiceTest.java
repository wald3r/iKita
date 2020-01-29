package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.Picture;
import at.ikita.services.PictureService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Tests for {@link PictureService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class PictureServiceTest {

    @Autowired
    private PictureService pictureService;

    private Picture testPictureNew1;
    private Picture testPictureNew2;
    private Picture testPictureDuplicate;


    @Before
    public void setupTestData() {
        testPictureNew1 = Picture.create(
                "http://ikita.at/content/images/test_image000.jpg");
        testPictureNew2 = Picture.create(
                "file:///var/www/ikita/content/images/test_image001.jpg");
        testPictureDuplicate = Picture.create(
                "file:///var/www/ikita/content/images/photo0001.jpg");
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadPicture() {
        Picture picture = pictureService.loadEntry(1);
        Assert.assertNotNull(picture);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSavePicture() {
        Picture picture = pictureService.saveEntry(testPictureNew1);
        Assert.assertTrue(testPictureNew1.equals(picture));
        Assert.assertNotEquals(0, (long) picture.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeletePicture() {
        Picture picture = pictureService.loadEntry(2);
        Assert.assertNotNull(picture);

        pictureService.deleteEntry(picture);

        picture = pictureService.loadEntry(2);
        Assert.assertNull(picture);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<Picture> state0 = pictureService.loadAll();
        pictureService.saveEntry(testPictureNew2);
        Collection<Picture> state1 = pictureService.loadAll();
        pictureService.deleteEntry(testPictureNew2);
        Collection<Picture> state2 = pictureService.loadAll();

        Assert.assertEquals(state0.size(), state1.size() - 1);
        Assert.assertEquals(state1.size(), state2.size() + 1);
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadPicture() {
        Picture picture = pictureService.loadEntry(99);
        Assert.assertNull(picture);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSavePicture() {
        pictureService.saveEntry(testPictureDuplicate);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByURI() {
        Picture picture = pictureService.loadByURI(
                "file:///var/www/ikita/content/images/photo0001.jpg");
        Assert.assertNotNull(picture);
    }

}
