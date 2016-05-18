package ch.epfl.visualComputing;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import ch.epfl.visualComputing.Transformations.Effects.EffectFunction;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import java.util.List;
import java.util.function.Function;


public class ImageProcessing extends PApplet {

    PImage img;
    Capture cam;
    PImage buffer1;
    PImage buffer2;


    private static final boolean webcam = false;

    HScrollbar upperThreshold = new HScrollbar(this, 0, 0, 800, 12);
    HScrollbar lowerThreshold = new HScrollbar(this, 0, 15, 800, 12);

    public void settings() {
        size(800, 600);
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
            lowerThreshold.update();
            lowerThreshold.display();
        }
    }


    public void computeImage(PImage img) {

        buffer1 = createImage(img.width, img.height, RGB);
        buffer2 = createImage(img.width, img.height, RGB);

        List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);

        //hue ranges: 112 - 133
        //brightness ranges: 28 - 120

        float min =lowerThreshold.getPos() * 255;
        float max = upperThreshold.getPos() * 255;
//        System.out.println("max: " + max);
//        System.out.println("min: " + min);

        Function<List<Integer>, List<Float>> filtered = new PixelTransformer<>(p -> {
            float b = this.brightness(p);
            float h = this.hue(p);
            float s = this.saturation(p);
            return s > 86
                    && 112 < h && h < 133
                    && 28 < b && b < 130
                    ? 255f : 0f;
        });
        filtered.andThen(blur)
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .andThen(DrawEffects.drawPixels(this, buffer1, 0, 0))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawEffects.drawHough(this, 0, 0))
                .andThen(HoughClusters.mapToClusters(200, 10))
                .andThen(HoughClusters.selectBestLines(10))
                .andThen(DrawEffects.drawLineArray(this, 0, img.width))
                .apply(sourcePixels);

    }

}
