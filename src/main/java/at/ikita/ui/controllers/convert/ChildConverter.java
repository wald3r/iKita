package at.ikita.ui.controllers.convert;

import at.ikita.model.Child;
import at.ikita.services.PersonService;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Created by lucas on 01.06.17.
 */

@FacesConverter("childConverter")
public class ChildConverter implements Converter{

    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                PersonService service = (PersonService) fc.getExternalContext().getApplicationMap().get("personService");
                for(Child c : service.getAuthenticatedUser().getChildren())
                    if(c.getId() == Integer.parseInt(value))
                        return c;
                return null;
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid child."));
            }
        }
        else {
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Child) object).getId());
        }
        else {
            return null;
        }
    }

}
