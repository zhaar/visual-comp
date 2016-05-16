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



        //hue ranges: 116 - 136
        //brightness ranges: 28 - 120

        Function<List<Integer>, List<Float>> filtered = new PixelTransformer<>(p -> {
            float b = this.brightness(p);
            float h = this.hue(p);
            return 116 < h && h < 136 && 28 < b && b < 155 ? 255f : 0f;
        });
        filtered.andThen(blur)
                                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))

//        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);
//        hue
//                .andThen(Threshold.genericBinary(i -> i < 128, 255, 0))
////                .andThen(brightness)
////                .andThen(new PixelTransformer<Float, Integer>(p -> (int) this.brightness(this.color(p))))
////                .andThen(blur)
////                .andThen(new PixelTransformer<>(p -> this.brightness((int) p))
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
//                .andThen(new PixelTransformer<>(this::color))
                .andThen(DrawEffects.drawPixels(this, buffer1, 0, 0))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawEffects.drawLines(this, 0, img.width))
                .andThen(HoughClusters.mapToClusters(200, 10))
                .andThen(HoughClusters.selectBestLines(10))
//                .andThen(DrawEffects.drawLineArray(this, 0, img.width))
                .apply(sourcePixels);

    }

}
