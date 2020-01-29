package at.ikita.configs;

import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.primefaces.webapp.filter.FileUploadFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Spring configuration for servlet context.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 * @author Michael Brunner <Michael.Brunner@uibk.ac.at>
 */

@Configuration
@ComponentScan("org.o7planning.springmvcfileud.*")
public class CustomServletContextInitializer implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
        sc.setInitParameter("primefaces.THEME", "ui-lightness");
        sc.setInitParameter("primefaces.UPLOADER", "commons");
        sc.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
    }

    @Bean
    public ServletRegistrationBean facesServletRegistraiton() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new FacesServlet(), "*.xhtml");
        registration.setName("Faces Servlet");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean facesUploadFilterRegistration() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new FileUploadFilter(), facesServletRegistraiton());
        registrationBean.setName("PrimeFaces FileUpload Filter");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registrationBean;
    }

}
