package ch.epfl.visualComputing;

import java.util.ArrayList;
import java.util.List;

public final class DepressingJava {

    public static List<Integer> toIntList(int[] arr) {
        List<Integer> intList = new ArrayList<Integer>(arr.length);
        for (int elem : arr) {
            intList.add(elem);
        }
        return intList;
    }
}
