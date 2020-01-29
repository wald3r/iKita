package at.ikita.utils;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;


/**
 * Custom scope implementation for spring to enable JSF2-like view-scoped beans.
 *
 * @Author Fabio.Valentini@student.uibk.ac.at
 * @author Michael Brunner <Michael.Brunner@uibk.ac.at>
 */

public class ViewScope implements Scope {

    private Map<String, Object> fallbackMapping = new HashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        // retrieve the view map of the faces context
        FacesContext currentContext = FacesContext.getCurrentInstance();

        // when running tests, no Context is available.
        // fall back to local mapping.
        if (currentContext == null) {
            Object object = objectFactory.getObject();
            fallbackMapping.put(name, object);
            return object;
        }

        Map<String, Object> viewMap = currentContext.getViewRoot().getViewMap();
        // get the bean by its name from the view map
        Object bean = viewMap.get(name);
        if (bean == null) {
            // if the bean has not been initialized, do so now and add it to the view map
            bean = objectFactory.getObject();
            viewMap.put(name, bean);
        }
        return bean;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // nothing to do
    }

    @Override
    public Object remove(String name) {
        // retrieve the view map of the faces context
        FacesContext currentContext = FacesContext.getCurrentInstance();

        // when running tests, no Context is available.
        // fall back to local mapping.
        if (currentContext == null) {
            return fallbackMapping.remove(name);
        }

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        // remove the object from the view map
        return viewMap.remove(name);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

}
