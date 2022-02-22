package com.jsonhash;

import com.jsonhash.sha.ApacheSha256;
import com.jsonhash.sha.ApacheSha256Cached;
import com.jsonhash.sha.JdkSha256;
import com.jsonhash.testutil.Profiler;
import com.jsonhash.testutil.ResourceUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static com.jsonhash.testutil.JsonUtils.givenJson;

/**
 * Testing environment: Windows 10, Intel(R) Core(TM) i7-10510U CPU @ 1.80GHz  2.30 GHz
 */
public class JsonHashBenchmark {
    private static final Logger log = LoggerFactory.getLogger("Runner");

    /**
     * 20:23:52.620 task count = 10, running time seconds = 0.008, ops = 1250.00.
     * 20:23:52.669 task count = 100, running time seconds = 0.046, ops = 2173.91.
     * 20:23:52.931 task count = 1000, running time seconds = 0.259, ops = 3861.00.
     * 20:23:53.268 task count = 2000, running time seconds = 0.333, ops = 6006.01.
     * 20:23:53.790 task count = 3000, running time seconds = 0.519, ops = 5780.35.
     * 20:23:54.647 task count = 5000, running time seconds = 0.855, ops = 5847.95.
     * 20:23:56.477 task count = 10000, running time seconds = 1.828, ops = 5470.46.
     * 20:24:14.526 task count = 100000, running time seconds = 18.047, ops = 5541.09.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000})
    void testJdk(int n) throws IOException {
        benchmark(new JsonHash(new JdkSha256()), n);
    }

    /**
     * 20:24:14.535 task count = 10, running time seconds = 0.004, ops = 2500.00.
     * 20:24:14.559 task count = 100, running time seconds = 0.019, ops = 5263.16.
     * 20:24:14.699 task count = 1000, running time seconds = 0.135, ops = 7407.41.
     * 20:24:14.943 task count = 2000, running time seconds = 0.242, ops = 8264.46.
     * 20:24:15.310 task count = 3000, running time seconds = 0.365, ops = 8219.18.
     * 20:24:15.937 task count = 5000, running time seconds = 0.625, ops = 8000.00.
     * 20:24:17.187 task count = 10000, running time seconds = 1.246, ops = 8025.68.
     * 20:24:32.946 task count = 100000, running time seconds = 15.757, ops = 6346.39.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000})
    void testApache(int n) throws IOException {
        benchmark(new JsonHash(new ApacheSha256()), n);
    }

    /**
     * 20:30:57.081 task count = 10, running time seconds = 0.012, ops = 833.33.
     * 20:30:57.111 task count = 100, running time seconds = 0.015, ops = 6666.67.
     * 20:30:57.158 task count = 1000, running time seconds = 0.044, ops = 22727.27.
     * 20:30:57.201 task count = 2000, running time seconds = 0.039, ops = 51282.05.
     * 20:30:57.256 task count = 3000, running time seconds = 0.052, ops = 57692.30.
     * 20:30:57.346 task count = 5000, running time seconds = 0.087, ops = 57471.27.
     * 20:30:57.493 task count = 10000, running time seconds = 0.144, ops = 69444.45.
     * 20:30:58.834 task count = 100000, running time seconds = 1.339, ops = 74682.60.
     * 20:31:13.239 task count = 1000000, running time seconds = 14.402, ops = 69434.80.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000, 1_000_000})
    void testApacheCached(int n) throws IOException {
        benchmark(new JsonHash(new ApacheSha256Cached()), n);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 200, 300, 500, 800, 1000, 2000, 3000, 5000, 10_000, 100_000, 1_000_000})
    void testApacheForLarge(int n) throws IOException {
        benchmark("large.json", new JsonHash(new ApacheSha256()), n);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000, 1_000_000})
    void testApacheForXLarge(int n) throws IOException {
        benchmark("xlarge.json", new JsonHash(new ApacheSha256()), n);
    }


    /**
     * 10:43:09.912 task count = 10, running time seconds = 0.082, ops = 121.95.
     * 10:43:10.170 task count = 100, running time seconds = 0.229, ops = 436.68.
     * 10:43:10.815 task count = 1000, running time seconds = 0.635, ops = 1574.80.
     * 10:43:12.566 task count = 2000, running time seconds = 1.741, ops = 1148.77.
     * 10:43:14.903 task count = 3000, running time seconds = 2.326, ops = 1289.77.
     * 10:43:18.619 task count = 5000, running time seconds = 3.710, ops = 1347.71.
     * 10:43:26.005 task count = 10000, running time seconds = 7.377, ops = 1355.56.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000, 3000, 5000, 10_000, 100_000, 1_000_000})
    void testDummyHashWithLarge(int n) throws IOException {
        benchmark("large.json", new JsonHash(str -> "hash"), n);
    }

    private void benchmark(JsonHash jsonHash, int n) throws IOException {
        benchmark("complex1.json", jsonHash, n);
    }

    private void benchmark(String file, JsonHash jsonHash, int n) throws IOException {
        Object json = givenJson(file);
        Profiler profiler = Profiler.start("");

        IntStream.range(0, n)
                .forEach(i -> {
                    jsonHash.calculate(json);
                    profiler.tick();
                });

        profiler.finish();
        log.info(profiler.report());
    }
}
