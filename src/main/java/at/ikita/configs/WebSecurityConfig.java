package at.ikita.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Spring configuration for web security.
 *
 * @author Michael Brunner <Michael.Brunner@uibk.ac.at>
 */

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.headers().frameOptions().disable(); // needed for H2 console

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login.xhtml")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        http.authorizeRequests()
                //Permit access to the H2 console
                .antMatchers("/h2-console/**").permitAll()
                //Permit access for all to error pages
                .antMatchers("/error/**")
                .permitAll()
                // Only access with admin role
                .antMatchers("/admin/**")
                .hasAnyAuthority("ADMIN")
                //Permit access only for some roles
                .antMatchers("/secured/**")
                .hasAnyAuthority("ADMIN", "PARENT", "TEACHER")
                //If user doesn't have permission, forward him to login page
                .and()
                .formLogin()
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/secured/welcome.xhtml")
                // if user password combination does not match any entry, error message will be displayed
                .failureUrl("/login.xhtml?error=true");
        
        http.exceptionHandling().accessDeniedPage("/error/access_denied.xhtml");

        http.sessionManagement().invalidSessionUrl("/error/invalid_session.xhtml");

    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
        		.passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT email, password, enabled FROM person WHERE email=?")
                .authoritiesByUsernameQuery("SELECT person.email, person_person_role.roles FROM person JOIN person_person_role ON person.id = person_person_role.person_id WHERE person.email=?");
    }

    /**
     * BCrypt password encoder to hash passwords
     * 
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(); 	
    }

}
