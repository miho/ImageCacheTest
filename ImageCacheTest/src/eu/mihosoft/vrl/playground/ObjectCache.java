/*
 * Copyright 2011 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */

package eu.mihosoft.vrl.playground;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>
 * A simple object cache that can be used to reduce object instanciation.
 * </p>
 * <p>
 * <b>Warning:</b>This is useful for heavy wight objects only! Do not use it
 * for objects that are cheap to create. This cache has been designed
 * for testing porposes only. Do not expect much.
 * </p>
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ObjectCache {

    private Map classEntries = new HashMap<Class<?>, ArrayDeque<ObjectEntry>>();
    private Map<Class<?>, ObjectCreator> creators =
            new HashMap<Class<?>, ObjectCreator>();
    private static final int MAX_ENTRIES = 30;

    /**
     * Returns an instance of a given class. If the class has been instanciated
     * with the same parameters already, i.e., if an unused object with the
     * requested properties already exists in the cache this will be returned
     * instead of creating a new one.
     * @param c the class to use for instanciation
     * @param params the parameters to use for instanciation
     *               (usually the constructor parameters)
     * @return an instance of class c.
     * @throws ClassNotSupportedException
     */
    public Object getInstance(Class<?> c, Object... params)
            throws ClassNotSupportedException {

        Object result = findInstanceByParams(c, params);

        if (result == null) {
            result = createNewObject(c, params);
        }

        return result;
    }

    /**
     * Registers an object creator with this cache.
     * @param creator the creator to add
     */
    public void registerCreator(ObjectCreator creator) {
        creators.put(creator.getType(), creator);
    }

    /**
     * Disposes an object. The object is free to be returned by another
     * <code>getInstance()</code> call.
     * @param o the object to dispose
     */
    public void dispose(Object o) {
        ObjectEntry oE = findObjectEntryByInstance(o);
        if (oE!=null) {
            oE.setInUse(false);
        }
    }

    /**
     * Converts parameters to string.
     * @param params the parameters to convert
     * @return the parameter string
     */
    public static String paramsToString(Object... params) {
        String result = "";

        for (Object p : params) {
            result += p.toString();
        }

        return result;
    }

    /**
     * Tries to find an object entry by its object instance
     * @param o the object
     * @return the requested object entry or <code>null</code> if no such
     *         entry exists
     */
    private ObjectEntry findObjectEntryByInstance(Object o) {
        ObjectEntry result = null;

        Deque<ObjectEntry> objects =
                (Deque<ObjectEntry>) classEntries.get(o.getClass());

        if (objects != null) {
            for (ObjectEntry oE : objects) {
                if (oE.getObject() == o) {
                    result = oE;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Tries to find an object instance by the parameter list it has been
     * created with.
     * @param c the class of the object
     * @param params the object parameters
     * @return the requested object or <code>null</code> if no such object
     *         exists
     */
    private Object findInstanceByParams(Class<?> c, Object... params) {
        Object result = null;

        Deque<ObjectEntry> objectList = (Deque) classEntries.get(c);

        if (objectList != null) {
            for (ObjectEntry oE : objectList) {
                if (!oE.isInUse() && oE.getParams().
                        equals(ObjectEntry.paramsToString(params))) {
                    result = oE.getObject();
                    oE.setInUse(true);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Creates a new object and adds a corresponding object entry to the cache.
     * @param c the class of the object to create
     * @param params the paramters used for instanciation
     * @return the new object
     * @throws ClassNotSupportedException
     */
    private Object createNewObject(Class<?> c, Object... params)
            throws ClassNotSupportedException {
        Object result = null;

        if (creators.containsKey(c)) {
            ObjectCreator creator = creators.get(c);
            ObjectEntry oE = creator.newInstance(params);
            result = oE.getObject();
            add(oE);
        } else {
            throw new ClassNotSupportedException(
                    "No Creator for " + c.getName() + " found!");
        }

        return result;
    }

    /**
     * Adds an object entry to the cache.
     * @param oEntry the entry to add
     */
    private void add(ObjectEntry oEntry) {

        Class<?> c = oEntry.getObject().getClass();
        ArrayDeque<ObjectEntry> objects =
                (ArrayDeque<ObjectEntry>) classEntries.get(c);

        if (objects == null) {
            objects = new ArrayDeque<ObjectEntry>();
            classEntries.put(c, objects);
        }

        cleanup(objects);
        objects.add(oEntry);
    }

    /**
     * Removes old instances to free memory.
     * @param objects the object list to cleanup
     */
    private void cleanup(ArrayDeque<ObjectEntry> objects) {
        while (objects.size() > MAX_ENTRIES) {
            ObjectEntry oE = null;
            boolean found = false;

            // check if an unused instance exists
            for (Iterator<ObjectEntry> it =
                    objects.descendingIterator(); it.hasNext();) {
                oE = it.next();
                if (!oE.isInUse()) {
                    found = true;
                    break;
                }
            }

            // if we found an unused instance remove it, remove the oldest
            // instance otherwise
            if (found) {
                objects.remove(oE);
            } else {
                objects.removeLast();
            }
        }
    }
} // end class

class ClassNotSupportedException extends Exception {

    public ClassNotSupportedException() {
    }

    public ClassNotSupportedException(String message) {
        super(message);
    }
}
