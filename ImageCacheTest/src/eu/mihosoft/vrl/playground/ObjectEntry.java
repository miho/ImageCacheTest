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

/**
 * Object Entry.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ObjectEntry {
    private Object object;
    private boolean inUse;
    private String params;

    /**
     * Constructor.
     * @param object object of this entry
     * @param params parameters used for object creation
     */
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
