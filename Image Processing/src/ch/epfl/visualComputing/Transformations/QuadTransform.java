package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.Quad;
import ch.epfl.visualComputing.Transformations.Effects.EffectFunction;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuadTransform implements Function<List<PVector>, List<List<PVector>>> {

    private final int width, height;

    public QuadTransform(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static PVector intersect(PVector lhs, PVector rhs) {

        double d = PApplet.cos(rhs.y) * PApplet.sin(lhs.y) - PApplet.cos(lhs.y) * PApplet.sin(rhs.y);
        float x = (float) ((rhs.x * PApplet.sin(lhs.y) - lhs.x * PApplet.sin(rhs.y)) / d);
        float y = (float) ((-rhs.x * PApplet.cos(lhs.y) + lhs.x * PApplet.cos(rhs.y)) / d);

        return new PVector(x, y);
    }

    public static List<PVector> linesToIntersections(int[] quad, List<PVector> lines) {
        return IntStream.range(0, 4).boxed().map((Integer i) ->
                intersect(lines.get(quad[i]), lines.get(quad[(i + 1) % 4])))
                .collect(Collectors.toList());
    }

    @Override
    public List<List<PVector>> apply(List<PVector> lines) {
        int maxArea = width * height;
        int minArea = maxArea / 6;
        Quad.QuadGraph quads = new Quad.QuadGraph(lines, width, height);
        List<int[]> cycles = quads.findCycles();

        List<List<PVector>> filtered = cycles.stream()
                .map(a -> linesToIntersections(a, lines))
                .map(quads::sortCorners)
                .filter(corners -> quads.isConvex(corners.get(0), corners.get(1), corners.get(2), corners.get(3)))
                .filter(corners -> quads.validArea(corners.get(0), corners.get(1), corners.get(2), corners.get(3), maxArea, minArea))
                .map(new EffectFunction<>(System.out::println))
                .filter(corners -> quads.nonFlatQuad(corners.get(0), corners.get(1), corners.get(2), corners.get(3)))
                .collect(Collectors.toList());
        System.out.println("quad lefts");
        filtered.forEach(System.out::println);
        return filtered;
    }
}
