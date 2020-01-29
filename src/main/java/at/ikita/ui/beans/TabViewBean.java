package at.ikita.ui.beans;

import at.ikita.model.Child;
import at.ikita.model.Person;
import at.ikita.services.PersonService;
import at.ikita.ui.controllers.ChildCalendarController;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


/**
 * Created by lucas on 30.05.17.
 */

@Component
@Scope("session")
public class TabViewBean {

    @Autowired
    private ChildCalendarController childCalendarController;
    @Autowired
    private PersonService personService;

    private Person person;

    private TabView tabView;


    public TabViewBean(){

        // get current logged in user
        person = personService.getAuthenticatedUser();
        Set<Child> children = person.getChildren();

        //generate tabs by code
        tabView = new TabView();

        for(Child c : children) {
            Tab t = new Tab();
            t.setTitle(c.getFirstName() + " " + c.getLastName());
            tabView.getChildren().add(t);
        }
    }


// getter setter
    public TabView getTabView() { return tabView; }
    public void setTabView(TabView tabView) { this.tabView = tabView; }
}
