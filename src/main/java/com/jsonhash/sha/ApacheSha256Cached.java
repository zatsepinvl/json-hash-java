package com.jsonhash.sha;

import com.jsonhash.HashFunction;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Not thread-safe implementation.
 */
public class ApacheSha256Cached implements HashFunction {
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public String apply(String data) {
        return cache.computeIfAbsent(data, DigestUtils::sha256Hex);
    }
}
