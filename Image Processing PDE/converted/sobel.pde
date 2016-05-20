import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.CopeOut.MyList;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public static Function<List<Float>, List<Float>> sobelDoubleConvolution(int width, int height) {

        int[][] hSobel = {{0, 1, 0}, {0, 0, 0}, {0, -1, 0}};
        int[][] vSobel = {{0, 0, 0}, {1, 0, -1}, {0, 0, 0}};
        ImageTransformation<Float, Float> vertical = ImageTransformation.convolutionTransformation(vSobel, 3, width, height);
        ImageTransformation<Float, Float> horizontal = ImageTransformation.convolutionTransformation(hSobel, 3, width, height);
        
        //Note that the following horror write 
        //vertical.mergeWith(horizontal, MyList::zip)
        //        .andThen(ls -> ls.parallelStream().map(Convolution::distance).collect(Collectors.toList()));
        //in idiomatic java 8
        return vertical.mergeWith(horizontal, new BiFunction<List<Float>, List<Float>, List<Pair<Float, Float>>>() {
            @Override
            public List<Pair<Float, Float>> apply(List<Float> xs, List<Float> ys) {
                return MyList.zip(xs, ys);
            }
        }).andThen(new Function<List<Pair<Float, Float>
                >, List<Float>>() {
                    @Override
                    public List<Float> apply(List<Pair<Float, Float>> ls) {
                      List<Float> p = new ArrayList<Float>(ls.size());
                      for (Pair<Float, Float> l : ls) {
                        p.add(distance(l));
                      }
                      return p;
                    }
                });
    }


private static float distance(Pair<Float, Float> p) {
    return PApplet.sqrt(PApplet.pow(p._1(), 2) + PApplet.pow(p._2(), 2));
}