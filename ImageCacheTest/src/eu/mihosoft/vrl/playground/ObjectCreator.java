package eu.mihosoft.vrl.playground;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Creates an object 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public abstract class ObjectCreator {
    private Class<?> type;
    private Object[] params;

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the params
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Object[] params) {
        this.params = params;
    }

    public abstract ObjectEntry newInstance(Object... params);
}
