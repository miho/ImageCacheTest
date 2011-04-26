package eu.mihosoft.vrl.playground;

import java.awt.image.BufferedImage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ImageCreator extends ObjectCreator {

    public ImageCreator() {
        setType(BufferedImage.class);
    }

    @Override
    public ObjectEntry newInstance(Object... params) {

        if (params.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments! Supported: int, int");
        }

        Integer w = 0;
        Integer h = 0;

        try {
            w = (Integer) params[0];
            h = (Integer) params[1];
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Wrong argument type! Supported: int, int");
        }
        return new ObjectEntry(
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB),
                params);
    }
}
