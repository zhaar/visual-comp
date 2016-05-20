package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.Transformations.CopeOut.MyList;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;
import ch.epfl.visualComputing.Transformations.HoughTransformation.HoughAccumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HoughClusters {

    private static boolean isLocalMaxima(HoughAccumulator votes, int r, int phi, int n) {
        int current = votes.get(r, phi);
        for (int radius = r - n / 2; radius < r + n / 2; radius++) {
            for (int angle = phi - n / 2; angle < phi + n / 2; angle++) {
                //if any of the votes around the value at (r, phi) is greater, then current is not a local maxima
                if (votes.getDefault(radius, angle, 0) > current) {
                    return false;
                }
            }
        }
        return true;
    }

    //Sane signature: Acc -> (Acc, [(Int, Int)]) the list of pair returned contains the index and the amount of votes associated
    public static Function<HoughAccumulator, Pair<HoughAccumulator, List<Pair<Integer, Integer>>>> mapToClusters(int minVotes, int neighborhood) {
        return (hough) -> {
            //Sane: [(Int, Int)] -> (Int, Int) -> [(Int, Int)]
            BiFunction<List<Pair<Integer, Integer>>, Pair<Integer, Integer>, List<Pair<Integer, Integer>>> accumulator = (acc, pair) -> {
                int votes = pair._1();
                int index = pair._2();
                Pair<Integer, Integer> coord = hough.convertIndex(index);
                if (votes > minVotes && isLocalMaxima(hough, coord._1(), coord._2(), neighborhood)) {
                    acc.add(new Pair<>(index, votes));
                }
                return acc;
            };
            List<Pair<Integer, Integer>> result = new ArrayList<>();
            for (Pair<Integer, Integer> element : MyList.zipWithIndex(hough.dataArray)) {
                result = accumulator.apply(result, element);
            }
            return new Pair<>(hough, result);
        };
    }

    //Sane: (Acc, [(Int, Int)]) -> [(Float, Float)]
    //takes a list of (Index, votes) and return a list of (radius, angle)
    public static Function<Pair<HoughAccumulator, List<Pair<Integer, Integer>>>, List<Pair<Float, Float>>> selectBestLines(int n) {
        return (pair) -> {
            HoughAccumulator acc = pair._1();
            List<Pair<Integer, Integer>> lines = pair._2();
            lines.sort((Pair<Integer, Integer> lhs, Pair<Integer, Integer> rhs) -> lhs._2() < rhs._2() ? 1 : -1);
            List<Pair<Float, Float>> selected = MyList.take(lines, n).stream().map(p -> acc.convertToActualValues(p._1())).collect(Collectors.toList());
            selected.forEach(System.out::println);
            return selected;
        };
    }

    public static Pair<Float, Float> accToValues(Pair<Integer, Integer> coord, int radius, int angle, float rStep, float phiStep) {
        float r = (coord._1() - (radius - 1) * 0.5f) * rStep;
        float phi = coord._2() * phiStep;
        return new Pair<>(r, phi);
    }
}
