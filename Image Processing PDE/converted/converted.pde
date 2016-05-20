import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

PImage img;
PImage buffer;

public void settings() {
    size(800 * 3, 600);
}

public void setup() {
  img = loadImage("board1.jpg");
  buffer = createImage(img.width, img.height, RGB);
  noLoop();
}

public void draw() {
  computeAndDraw(img, buffer, this);
}

void computeAndDraw(PImage img, PImage buffer, PApplet ctx) {
  List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
  ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);
  Function<List<Integer>, List<Float>> filtered = new PixelTransformer<Integer, Float>((Function<Integer, Float>) new Function<Integer, Float>() {
            @Override
            public Float apply(Integer p) {
                float b = brightness(p);
                float h = hue(p);
                float s = saturation(p);
                return s > 86
                        && 70 < h && h < 144
                        && 0 < b && b < 153
                        ? 255f : 0f;
            }
        });
        
  ctx.image(img, 0, 0);
  filtered.andThen(blur)
          .andThen(sobelDoubleConvolution(img.width, img.height))
          .andThen(DrawEffects.drawPixels(ctx, buffer, 1600, 0))
          .andThen(new HoughTransformation_(0.06f, 2.5f, img.width, img.height))
          .andThen(DrawEffects.drawHough(ctx, 800, 0, 800, 600))
          .andThen(HoughClusters.mapToClusters(300, 20))
          .andThen(HoughClusters.selectBestLines(6))
          .andThen(DrawEffects.drawLineArray(ctx, 0, img.width))
          .andThen(DepressingJava.toVectors())
          .andThen(new QuadTransform(img.width, img.height))
          .andThen(DrawEffects.drawQuads(ctx))
          .apply(sourcePixels);
}