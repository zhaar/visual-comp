package ch.epfl.visualComputing.Transformations.Effects;

import java.util.function.Consumer;
import java.util.function.Function;

public class EffectFunction<A> implements Function<A, A> {

    private final Consumer<A> effect;

    public EffectFunction(Consumer<A> effect) {
        this.effect = effect;
    }

    @Override
    public A apply(A a) {
        effect.accept(a);
        return a;
    }
}
