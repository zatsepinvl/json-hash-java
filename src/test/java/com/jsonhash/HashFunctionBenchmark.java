package com.jsonhash;

import com.jsonhash.sha.ApacheSha256;
import com.jsonhash.testutil.Profiler;
import com.jsonhash.testutil.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class HashFunctionBenchmark {
    private static final Logger log = LoggerFactory.getLogger("Runner");

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000, 1_000_000})
    void testPureHashFromString(int n) throws IOException, URISyntaxException {
        HashFunction hashFunction = new ApacheSha256();
        byte[] bytes = Files.readAllBytes(Paths.get(ResourceUtils.class.getResource("/json/complex1.json").toURI()));
        String data = new String(bytes, StandardCharsets.UTF_8);
        Profiler profiler = Profiler.start("");

        IntStream.range(0, n)
                .forEach(i -> {
                    hashFunction.apply(data);
                    profiler.tick();
                });

        profiler.finish();
        log.info(profiler.report());
    }
}
