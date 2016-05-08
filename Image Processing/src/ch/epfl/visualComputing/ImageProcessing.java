package ch.epfl.visualComputing;

import processing.core.PApplet;
import processing.core.PImage;

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
        image(img, 0, 0);
    }

}
