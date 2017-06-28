package eu.sollers.odata.snapgram.init;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import eu.sollers.odata.snapgram.domain.GenericRepository;
import eu.sollers.odata.snapgram.domain.category.Category;
import eu.sollers.odata.snapgram.domain.image.Image;
import eu.sollers.odata.snapgram.domain.user.User;

import lombok.extern.slf4j.Slf4j;

/**
 * Loading sample data.
 */
@Slf4j
@Component
public class SampleDataInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private GenericRepository repos;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Sample Data - start");

        User u = User.builder().username("admin").mail("admin@ad.min").build();
        User u2 = User.builder().username("test").mail("user@user.te").build();

        Category c = Category.builder().name("Adventure").build();
        Category c2 = Category.builder().name("Sci-Fi").build();
        Category c3 = Category.builder().name("Memes").build();
        Category c4 = Category.builder().name("Fun").build();

        Image img = Image.builder().name("TestImg").description("This is test data").width(800).height(600).user(u2)
                         .category(c3).build();
        Image img2 = Image.builder().name("TestPrivateImg").description("This is test private image").user(u2)
                          .category(c3).width(640).height(480).isPrivate(true).build();
        try {
            img.setContent(FileCopyUtils.copyToByteArray(new ClassPathResource("static/1.jpg").getInputStream()));
            img.setMediaContentType(Image.JPEG.toString());
            img2.setContent(FileCopyUtils.copyToByteArray(new ClassPathResource("static/2.png").getInputStream()));
            img2.setMediaContentType(Image.PNG.toString());
        } catch (IOException e) {
            log.warn("Image files were not loaded properly");
        }

        u2.setImages(Arrays.asList(img, img2));

        repos.getRepository(User.class).save(Arrays.asList(u, u2));
        repos.getRepository(Category.class).save(Arrays.asList(c, c2, c3, c4));

        log.info("Adding Sample Data finished.");
    }
}