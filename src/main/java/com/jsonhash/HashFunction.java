package com.jsonhash;

public interface HashFunction {
    /**
     * @param data any text
     * @return HEX hash string
     */
    String apply(String data);
}
