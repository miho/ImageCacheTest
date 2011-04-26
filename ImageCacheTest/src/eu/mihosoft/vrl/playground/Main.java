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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main {

    /**
     * Compares object creation strategies (cached vs. uncached)
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        for (int i = 0; i < 3; i++) {
            System.out.println("\n********* TEST " + (i+1) + " *********");
            experimentOne(false);
            experimentOne(true);

            experimentTwo(false);
            experimentTwo(true);

            experimentThree(false);
            experimentThree(true);
        }
    }

    /**
     * Operates on sub images instead of creating images of correct size.
     * @param reuse defines whether to reuse already created images
     */
     public static void experimentThree(boolean reuse) {
        System.out.println("EXPERIMENT THREE: cache=" + reuse);
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        images.add(new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB));

        System.out.println("--> Objects Initialized!");

        long timeBefore = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            BufferedImage img = null;

            if (reuse) {
                img = images.get(0).getSubimage(0, 0, 640, 480);

                // THIS TAKES SO MUCH TIME!!!
                clearImage(img);
            } else {
                img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
            }

            Graphics2D g2 = img.createGraphics();

            g2.fillRect(10, 10, 600, 400);

            g2.dispose();
        }

        long timeAfter = System.nanoTime();

        System.out.println("--> Duration: " + (timeAfter - timeBefore) * 1E-9);
    }

     /**
      * Uses an object cache to reduce object creation.
      * @param reuse defines whether to reuse already created images
      */
    public static void experimentTwo(boolean reuse) {
        System.out.println("EXPERIMENT TWO: cache=" + reuse);
        ObjectCache cache = new ObjectCache();
        cache.registerCreator(new ImageCreator());

        int[] widths = {120, 320, 400, 640, 800};
        int[] heights = {80, 240, 300, 480, 600};

        for (int i = 0; i < 20; i++) {
            try {
                Object o =
                        cache.getInstance(
                        BufferedImage.class, widths[i % 5], heights[i % 5]);
                cache.dispose(o);
            } catch (ClassNotSupportedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("--> Objects Initialized!");

        long timeBefore = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            BufferedImage img = null;

            if (reuse) {
                try {
                    img = (BufferedImage) cache.getInstance(BufferedImage.class, 640, 480);
                } catch (ClassNotSupportedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                // THIS TAKES SO MUCH TIME!!!
                clearImage(img);
            } else {
                img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
            }

            Graphics2D g2 = img.createGraphics();

            g2.fillRect(10, 10, 600, 400);

            g2.dispose();

            cache.dispose(img);
        }

        long timeAfter = System.nanoTime();

        System.out.println("--> Duration: " + (timeAfter - timeBefore) * 1E-9);
    }

    /**
     * Uses an array list to reduce object creation.
     * @param reuse defines whether to reuse already created images
     */
    public static void experimentOne(boolean reuse) {
        System.out.println("EXPERIMENT ONE: cache=" + reuse);
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        images.add(new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB));

        System.out.println("--> Objects Initialized!");

        long timeBefore = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            BufferedImage img = null;

            if (reuse) {
                img = images.get(0);
                // THIS TAKES SO MUCH TIME!!!
                clearImage(img);
            } else {
                img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
            }

            Graphics2D g2 = img.createGraphics();

            g2.fillRect(10, 10, 600, 400);

            g2.dispose();
        }

        long timeAfter = System.nanoTime();

        System.out.println("--> Duration: " + (timeAfter - timeBefore) * 1E-9);
    }

    /**
     * Clears an imager.
     * @param image the image to clear
     */
    public static void clearImage(BufferedImage image) {
        Graphics2D g2 = image.createGraphics();

        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());
    }
}
