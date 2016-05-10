package ch.epfl.visualComputing;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

        PImage buffer = createImage(img.width, img.height, RGB);

        int[][] hSobel = {{0,1,0}, {0,0,0}, {0,-1,0}};
        int[][] vSobel = {{0,0,0}, {1,0,-1}, {0,0,0}};
        List<Float> sourceBrightness = DepressingJava.toIntList(img.pixels)
                .stream().map(this::brightness).collect(Collectors.toList());


        List<Float> vertical = ImageTransformation.convolutionTransformation(vSobel, 3, img.width, img.height).transform(sourceBrightness);
        List<Float> horizontal = ImageTransformation.convolutionTransformation(hSobel, 3, img.width, img.height).transform(sourceBrightness);
        List<Float> sobeled = IntStream.range(0, horizontal.size())
                .mapToObj(i -> PApplet.sqrt(PApplet.pow(vertical.get(i), 2) + PApplet.pow(horizontal.get(i), 2)))
                .collect(Collectors.toList());
        float max = sobeled.stream().max((l, r) -> l < r ? -1 : 1).get();
//        System.out.println("max: " + max);
        sobeled = sobeled.stream().map(v -> v > max * 0.25f ? 255f : 0f).collect(Collectors.toList());
        return applyImage(sobeled, buffer);
    }

    public PImage applyImage(List<Float> pixels, PImage buffer) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = color(pixels.get(i));
        }
        return buffer;
    }

}
