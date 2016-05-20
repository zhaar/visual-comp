package ch.epfl.visualComputing.Transformations.CopeOut;

public class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A _1() {
        return a;
    }

    public B _2() {
        return b;
    }

    @Override
    public String toString() {
        return "(" + a.toString() + ", " + b.toString() + ")";
    }
}
