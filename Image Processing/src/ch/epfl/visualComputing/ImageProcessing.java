package ch.epfl.visualComputing;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageProcessing extends PApplet {

    PImage img;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        img = loadImage("board1.jpg");
        noLoop();
    }

    public void draw() {
        image(computeImage(img), 0, 0);
    }

    public PImage computeImage(PImage img) {
        PImage result = createImage(img.width, img.height, RGB);
        List<Float> transformed = Threshold.Binary(128, 255).transform(
                DepressingJava.toIntList(img.pixels)
                        .stream()
                        .map(this::brightness)
                        .collect(Collectors.toList()));
        return applyImage(transformed, result);
    }

    public PImage applyImage(List<Float> pixels, PImage buffer) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = color(pixels.get(i));
        }
        return buffer;
    }

}
