package ch.epfl.visualComputing.Transformations;

import java.util.List;
import java.util.function.Function;

public class HoughLines implements Function<HoughTransformation.HoughAccumulator, List<HoughLines.Line>> {
    @Override
    public List<Line> apply(HoughTransformation.HoughAccumulator houghAccumulator) {

        return null;
    }

    public static class Line {
        private final int r, phi;
        public Line(int r, int phi) {
            this.r = r;
            this.phi = phi;
        }
    }
}
