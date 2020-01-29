package at.ikita.ui.beans;

import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;


/**
 * Bean for getting Session-specific information.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("session")
public class SessionInfoBean {

    private PersonService personService;
    private Person currentUser;

    @Autowired
    public SessionInfoBean(PersonService personService) {
        Assert.notNull(personService);

        this.personService = personService;
    }


    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }


    public String getCurrentUserEmail() {
        if (!isLoggedIn())
            return null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }


    public Person getCurrentUser() {
        if (currentUser != null)
            return currentUser;

        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null)
            return null;

        currentUser = personService.loadByEmail(currentUserEmail);
        return currentUser;
    }


    public Set<PersonRole> getCurrentUserRoles() {
        if (!isLoggedIn())
            return new HashSet<>();

        getCurrentUser();

        if (currentUser == null)
            return new HashSet<>();

        return currentUser.getRoles();
    }


    public boolean hasRole(PersonRole role) {
        if (!isLoggedIn())
            return false;

        getCurrentUser();

        return (currentUser != null) && currentUser.getRoles().contains(role);
    }
}
