package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import ch.epfl.visualComputing.Transformations.Effects.EffectFunction;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ImageProcessing extends PApplet {

    PImage buffer;
    PImage mv;
    public void settings() {
        size(640 * 3, 480);
    }

    public void setup() {
        mv = loadImage("board3.jpg");
        mv.resize(640, 480);

//        thresholdBar = new HScrollbar(this, 0, 0 , 800, 20);
//        mv = new Movie(this, "/Users/zephyz/Projects/processing/videotest/data/testvideo.mp4");
//        mv.play();
//        mv.loop();
        buffer = createImage(640, 480, RGB);
        noLoop();
    }

    public void draw() {
        if (mv.width * mv.height != 0)
            computeAndDraw(mv, buffer, this);
//        thresholdBar.display();
//        thresholdBar.update();
//        image(mv, 0, 0);
    }

    public void movieEvent(Movie m) {
        m.read();
    }

    public static Function<List<Integer>, List<Float>> filter(PApplet ctx) {
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

    private static <T> List<T> shiftLeft(List<T> ls, int c) {
        int size = ls.size();
        List<T> cpy = new ArrayList<>(ls);
        for (int i = 0; i < size; i++) {
            cpy.set(i, ls.get((i+c)%size));
        }
        return Collections.unmodifiableList(cpy);
    }

    public static Function<List<PVector>, PVector> ComputeAngles(int width, int height) {
        return list -> {
            PVector c1 = list.get(0);
            PVector c2 = list.get(2);
            PVector center = new PVector((c1.x+c2.x)/2,(c1.y+c2.y)/2);
            list.sort( (b, d) -> Math.atan2(b.y-center.y,b.x-center.x)<Math.atan2(d.y-center.y,d.x-center.x) ? -1 : 1);
            PVector smallest = list.stream().min((o1, o2) -> o1.mag() < o2.mag() ? -1 : 1).get();
            int smallestPosition = list.indexOf(smallest);
            List<PVector> ordered = shiftLeft(list, smallestPosition);
            PVector angles = new TwoDThreeD(width, height).get3DRotations(ordered);
            System.out.println("angles: "+angles.mult(90));
            return angles;
        };
    }

    private static <T> Optional<T> fst(List<T> ls) {
        return ls.size() > 0 ? Optional.of(ls.get(0)) : Optional.empty();
    }

    public void computeAndDraw(PImage img, PImage buffer, PApplet ctx) {

        System.out.println("image size: " + img.width + ", " + img.height);
        List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);

//        float upper = 255 * this.thresholdBar.getPos();
//        System.out.println("threshold: " + upper);
//        Function<List<Integer>,List<Float>> filtered = filter(ctx);
        //hue ranges: 112 - 133 or 70 - 144
        //brightness ranges: 0 - 153

        ctx.image(img, 0, 0);
        Threshold.customFilter(ctx).andThen(blur)
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
                .andThen(ImageProcessing::fst)
                .andThen(o -> o.map(ComputeAngles(img.width, img.height)))
                .apply(sourcePixels);
    }
}
