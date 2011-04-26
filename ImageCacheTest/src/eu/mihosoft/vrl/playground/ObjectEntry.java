package eu.mihosoft.vrl.playground;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ObjectEntry {
    private Object object;
    private boolean inUse;
    private String params;

    public ObjectEntry(Object object, Object... params) {
        this.object = object;
        this.params = paramsToString(params);
    }

    public static String paramsToString(Object... params) {
        String result = "";

        for (Object p : params) {
            result+=p.toString()+":";
        }

        return result;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }


    /**
     * @return the inUse
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * @param inUse the inUse to set
     */
    void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params;
    }
}
