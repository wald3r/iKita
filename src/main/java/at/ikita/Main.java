package at.ikita;

import java.util.HashMap;

import javax.faces.webapp.FacesServlet;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import at.ikita.configs.CustomServletContextInitializer;
import at.ikita.configs.WebSecurityConfig;
import at.ikita.utils.ViewScope;


/**
 * Spring boot application. Execute maven with <code>mvn spring-boot:run</code>
 * to start this web application.
 *
 * @author Michael Brunner <Michael.Brunner@uibk.ac.at>
 */

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class, CustomServletContextInitializer.class, WebSecurityConfig.class);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();
        return new ServletRegistrationBean(servlet, "*.xhtml");
    }

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        HashMap<String, Object> customScopes = new HashMap<>();
        customScopes.put("view", new ViewScope());
        customScopeConfigurer.setScopes(customScopes);
        return customScopeConfigurer;
    }

}
