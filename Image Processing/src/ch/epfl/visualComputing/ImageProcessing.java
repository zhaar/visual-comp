package ch.epfl.visualComputing;

import processing.core.PApplet;
import processing.core.PImage;

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
        int[][] gaussianMatrix = {{9, 12, 9},
                {12, 15, 12},
                {9, 12, 9}};
        int[][] idMatrix = {{0,0,0}, {0,0,1}, {0,0,0}};
        PImage result = createImage(img.width, img.height, RGB);
        ImageTransformation.Convolution c = new ImageTransformation.Convolution(gaussianMatrix, 3);
        List<Float> transformed = new ImageTransformation<>(c.toGeneric(img.width, img.height)).transform(
                DepressingJava.toIntList(img.pixels)
                        .stream().map(this::brightness).collect(Collectors.toList()));
        return applyImage(transformed, result);
    }

    public PImage applyImage(List<Float> pixels, PImage buffer) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = color(pixels.get(i));
        }
        return buffer;
    }

}
