package ch.epfl.visualComputing;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.Convolution;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import ch.epfl.visualComputing.Transformations.HoughTransformation;
import ch.epfl.visualComputing.Transformations.PixelTransformer;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessing extends PApplet {

    PImage img;
    Capture cam;

    private static final boolean webcam = true;

    HScrollbar upperThreshold = new HScrollbar(this, 0, 0, 800, 12);
    HScrollbar lowerThreshold = new HScrollbar(this, 0, 15, 800, 12);

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        if (webcam) {
            String[] cameras = Capture.list();
            if (cameras.length == 0) {
                println("There are no cameras available for capture.");
                exit();
            } else {
                println("Available cameras:");
                for (String camera : cameras) {
                    println(camera);
                }
                cam = new Capture(this, cameras[0]);
                cam.start();
            }
        } else {
            img = loadImage("board1.jpg");
            noLoop();
        }
    }

    public void draw() {
        if (webcam) {
            if (cam.available()) {
                cam.read();
            }
            img = cam.get();
            image(img, 0, 0);
        } else {
            computeImage(img);
            upperThreshold.update();
            upperThreshold.display();
        }
    }

    public void computeImage(PImage img) {

        image(img,0, 0);
        List<Float> sourceBrightness = DepressingJava.toIntList(img.pixels)
                .stream().map(this::brightness).collect(Collectors.toList());
        Convolution.gaussianBlur(img.width, img.height)
                .andThen(new PixelTransformer<>(p -> Float.compare(p, 128) < 0 ? 250f : 0f))
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawEffects.drawHough(this))
                .andThen(DrawEffects.drawLines(this, img.width, 400))
                .apply(sourceBrightness);
    }

    public static PImage applyImage(List<Integer> pixels, PImage buffer, PApplet ctx) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = ctx.color(pixels.get(i));
        }
        return buffer;
    }

}
