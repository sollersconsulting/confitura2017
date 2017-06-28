package eu.sollers.odata.snapgram.init;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import eu.sollers.odata.snapgram.domain.GenericRepository;
import eu.sollers.odata.snapgram.domain.image.Image;

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

        Image img = Image.builder().name("TestImg").description("This is test data").width(800).height(600).build();
        Image img2 = Image.builder().name("TestPrivateImg").description("This is test private image").width(640)
                          .height(480).isPrivate(true).build();
        try {
            img.setContent(FileCopyUtils.copyToByteArray(new ClassPathResource("static/1.jpg").getInputStream()));
            img.setMediaContentType(Image.JPEG.toString());
            img2.setContent(FileCopyUtils.copyToByteArray(new ClassPathResource("static/2.png").getInputStream()));
            img2.setMediaContentType(Image.PNG.toString());
        } catch (IOException e) {
            log.warn("Image files were not loaded properly");
        }

        repos.getRepository(Image.class).save(img);
        repos.getRepository(Image.class).save(img2);

        log.info("Adding Sample Data finished.");
    }
}
