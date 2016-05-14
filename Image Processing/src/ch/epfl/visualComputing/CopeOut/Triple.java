package ch.epfl.visualComputing.CopeOut;

public class Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A _1() { return a; }
    public B _2() { return b; }
    public C _3() { return c; }
}
