package ch.epfl.visualComputing;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessing extends PApplet {

    PImage img;
    Capture cam;
    PImage buffer;


    private static final boolean webcam = false;

    HScrollbar upperThreshold = new HScrollbar(this, 0, 0, 800, 12);
    HScrollbar lowerThreshold = new HScrollbar(this, 0, 15, 800, 12);

    public void settings() {
        size(2400, 800);
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
//            noLoop();
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

        buffer = createImage(img.width, img.height, RGB);

//        image(img,0, 0);
        List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
//        Convolution.gaussianBlur(img.width, img.height)
////                .andThen(new PixelTransformer<>((Float b) -> {
////                    System.out.println(this.hue(Math.round(b)));
////                    return Math.round(b);
////                }))
////                .andThen(new PixelTransformer<>(this::hue))
//                .andThen(new PixelTransformer<>(Math::round))
//                .andThen(DrawEffects.drawPixels(this, createImage(img.width, img.height, RGB), 0, 0))
//                .andThen(new PixelTransformer<>(p -> Float.compare(p, 128) < 0 ? 250f : 0f))
//                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
//                .andThen(new PixelTransformer<>(Math::round))
//                .andThen(DrawEffects.drawPixels(this, createImage(img.width, img.height, RGB), 0, 0))
//                .andThen(new PixelTransformer<>(p -> new Float(p)))
//                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
//                .andThen(DrawEffects.drawHough(this))
//                .andThen(HoughClusters.mapToClusters(200, 10))
//                .andThen(HoughClusters.selectBestLines(10))
//                .andThen(DrawEffects.drawLineArray(this, 0, img.width))
//                .apply(sourcePixels);

        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);
        new PixelTransformer<>(this::brightness)
//                .andThen(Threshold.genericBinary( p -> lowerThreshold.getPos() * 255 < p && p < upperThreshold.getPos() * 255, 250f, 0))
//                .andThen(new PixelTransformer<Float, Integer>(p -> (int) this.brightness(this.color(p))))
//                .andThen(blur)
//                .andThen(new PixelTransformer<>(p -> this.brightness((int) p)))
                .andThen(new PixelTransformer<>(this::color))
                .andThen(DrawEffects.drawPixels(this, buffer, 0, 0))
                .apply(sourcePixels);

    }

}
