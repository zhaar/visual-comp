package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Movie;

import java.util.List;
import java.util.function.Function;

public class ImageProcessing extends PApplet {

    PImage buffer;
    Movie mv;
    HScrollbar thresholdBar;
    public void settings() {
        size(640 * 3, 480);
    }

    public void setup() {
//        mv = loadImage("board4.jpg");
//        mv.resize(640, 480);

        thresholdBar = new HScrollbar(this, 0, 0 , 800, 20);
        mv = new Movie(this, "/Users/zephyz/Projects/processing/videotest/data/testvideo.mp4");
        mv.play();
        mv.loop();
        buffer = createImage(640, 480, RGB);
    }

    public void draw() {
        if (mv.width * mv.height != 0)
            computeAndDraw(mv, buffer, this);
        thresholdBar.display();
        thresholdBar.update();
//        image(mv, 0, 0);
    }

    public void movieEvent(Movie m) {
        m.read();
    }

    public static Function<List<Integer>, List<Float>> filter(PApplet ctx, float upper) {
        return new PixelTransformer<>((Function<Integer, Float>) p -> {
            float b = ctx.brightness(p);
            float h = ctx.hue(p);
            float s = ctx.saturation(p);
            return  86 < s && s < 200
                    && 70 < h && h < 132
                    && 106 < b && b < 153
                    ? 255f : 0f;
        });
    }

    public void computeAndDraw(PImage img, PImage buffer, PApplet ctx) {

        System.out.println("image size: " + img.width + ", " + img.height);
        List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);

        float upper = 255 * this.thresholdBar.getPos();
        System.out.println("threshold: " + upper);
        Function<List<Integer>,List<Float>> filtered = filter(ctx, upper);
        //hue ranges: 112 - 133 or 70 - 144
        //brightness ranges: 0 - 153

        ctx.image(img, 0, 0);
        filtered.andThen(blur)
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .andThen(DrawEffects.drawPixels(ctx, buffer, img.width * 2, 0))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawEffects.drawHough(ctx, img.width, 0, img.width, img.height))
                .andThen(HoughClusters.mapToClusters(300, 40))
                .andThen(HoughClusters.selectBestLines(6))
                .andThen(DrawEffects.drawLineArray(ctx, 0, img.width))
                .andThen(DepressingJava.toVectors())
                .andThen(new QuadTransform(img.width, img.height))
                .andThen(DrawEffects.drawQuads(ctx))
                .apply(sourcePixels);
    }
}
