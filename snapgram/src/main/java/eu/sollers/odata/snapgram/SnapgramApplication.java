package eu.sollers.odata.snapgram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import eu.sollers.odata.snapgram.servlet.ODataServlet;

@SpringBootApplication
public class SnapgramApplication {

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new ODataServlet(), "/OData.svc/*");
    }

    public static void main(String[] args) {
        SpringApplication.run(SnapgramApplication.class, args);
    }
}
