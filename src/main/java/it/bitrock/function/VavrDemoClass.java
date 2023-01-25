package it.bitrock.function;

import io.vavr.control.Try;

public class VavrDemoClass {

    /** Functional Programming **/

    // old school
    public Integer divide(Integer dividend, Integer divisor) {
        // throws if divisor is zero
        return dividend / divisor;
    }

    // vavr
    public Try<Integer> divideVavr(Integer dividend, Integer divisor) {
        return Try.of(() -> dividend / divisor);
    }
}
