package com.jsonhash;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class JsonHash {
    private final HashFunction hashFunction;

    public JsonHash(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    public String calculate(Object object) {
        if (object instanceof Map) {
            return calculateForMap((Map<?, ?>) object);
        }
        if (object instanceof Collection) {
            return calculateForCollection((Collection<?>) object);
        }
        if (object == null) {
            return hashFunction.apply("");
        }
        return hashFunction.apply(object.toString());
    }

    private String calculateForMap(Map<?, ?> map) {
        String hashes = map.entrySet().stream()
                .map(entry -> {
                    String keyHash = calculate(entry.getKey());
                    String valueHash = calculate(entry.getValue());
                    return hashFunction.apply(keyHash + valueHash);
                })
                .sorted()
                .collect(joining());
        return hashFunction.apply(hashes);
    }

    private String calculateForCollection(Collection<?> list) {
        String hashes = list.stream()
                .map(this::calculate)
                .sorted()
                .collect(joining());
        return hashFunction.apply(hashes);
    }
}
