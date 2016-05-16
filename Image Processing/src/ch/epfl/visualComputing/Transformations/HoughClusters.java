package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.CopeOut.MyList;
import ch.epfl.visualComputing.CopeOut.Pair;
import ch.epfl.visualComputing.ImageTransformation;
import ch.epfl.visualComputing.Transformations.HoughTransformation.HoughAccumulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public static Function<HoughAccumulator, Pair<HoughAccumulator,List<Pair<Integer, Integer>>>> mapToClusters(int minVotes, int neighborhood) {
        return (hough) -> {
            BiFunction<List<Pair<Integer, Integer>>, Pair<Integer, Integer>, List<Pair<Integer, Integer>>> accumulator = (acc, pair) -> {
                int votes = pair._1();
                int index = pair._2();
                Pair<Integer, Integer> coord = hough.convertIndex(index);
                if (votes > minVotes && isLocalMaxima(hough, coord._1(), coord._2(), neighborhood)) {
                    acc.add(coord);
                }
                return acc;
            };
            List<Pair<Integer, Integer>> result = new ArrayList<>();
            for (Pair<Integer, Integer> element : MyList.zipWithIndex(hough.dataArray)){
                result = accumulator.apply(result, element);
            }
            return new Pair<>(hough, result);
        };
    }

    public static Function<Pair<HoughAccumulator, List<Pair<Integer, Integer>>>, List<Pair<Integer, Integer>>> selectBestLines(int n) {
        return (pair) -> {
            HoughAccumulator acc = pair._1();
            List<Pair<Integer, Integer>> lines = pair._2();
            lines.sort((lhs, rhs) -> acc.get(lhs._1(), lhs._2()) > acc.get(rhs._1(), rhs._2()) ? 1 : -1);
            return MyList.take(lines, n);
        };
    }
}
