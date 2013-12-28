package com.gadawski.util.common;

/**
 * Simple counter implementation, for use in rule as a global value.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class Counter {
    /**
     * Instance value.
     */
    private long counter;

    /**
     * @param value
     *            to be instantiated.
     */
    public Counter(long value) {
        this.counter = value;
    }

    /**
     * Increments counter and return it.
     * 
     * @return incremented counter value.
     */
    public long getAndIncrement() {
        return ++(this.counter);
    }

}
