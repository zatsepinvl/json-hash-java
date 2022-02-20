package com.jsonhash;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.jsonhash.testutil.JsonUtils.givenJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonHashTest {
    private final JsonHash jsonHash = new JsonHash(DigestUtils::sha256Hex);

    /**
     * a = ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
     * 1 = 6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
     * <p>
     * b = 3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d
     * 2 = d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35
     * <p>
     * c = 2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6
     * 3 = 4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce
     * <p>
     * ah = a+1 = a5a1772027bf03eac81b42d789211f388b7628eacae11d50b850026048c4576b  = > 2
     * bh = b+2 = bde6dfd024854b6603e5d156e8e98ca6c6c86dd359ed5cc151d38964d44a5498  = > 3
     * ch = c+3 = 790a2cf4aef9cfef7cb1efd1afd37930163825c5b08cd9d0dd2f370dd30ee700  = > 1
     * <p>
     * hash = ch+ah+bh = e09ab0cda1fff7d9e64a630bbad5b6c611e0ecec15629b081202268fd5d8b297
     */
    @Test
    void testHashForObject() throws IOException {
        testHash("object1.json", "e09ab0cda1fff7d9e64a630bbad5b6c611e0ecec15629b081202268fd5d8b297");
    }

    /**
     * a = ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
     * <p>
     * b = 3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d
     * <p>
     * c = 2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6
     * <p>
     * hash = c+b+a = f71b5a2b8d2b4ed795489a9044d19848db2a60b76691961c6e16d89d4867c0d3
     */
    @Test
    void testHashForArray() throws IOException {
        testHash("array1.json", "f71b5a2b8d2b4ed795489a9044d19848db2a60b76691961c6e16d89d4867c0d3");
    }

    /**
     * key = 2c70e12b7a0646f92279f427c7b38e7334d8e5389cff167a1dc30e73f826b683
     * null = empty string = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
     * key+null = b3b73bae8b3a793c30e5e5d2756ddf392df503a6108c4c52c17f781fc83ee116
     * <p>
     * hash = a08dda18609d244de3985c337f3e23f5e15183f966e55fce8fcecba51f642485
     */
    @Test
    void testHashForNull() throws IOException {
        testHash("null.json", "a08dda18609d244de3985c337f3e23f5e15183f966e55fce8fcecba51f642485");
    }

    /**
     * value = cd42404d52ad55ccfa9aca4adc828aa5800ad9d385a0671fbcbf724118320619
     * <br>
     * "" = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
     * <br>
     * value + "" = 1d36d24c6c73968533e3557a8a33d6021fbb6f584985e9ae15057032d7477f72
     * <br><br>
     * object = 2958d416d08aa5a472d7b509036cb7eafd542add84527e66a145ea64cb4cdc75
     * <br>
     * {} = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
     * <br>
     * object + {} = 478e7b1136b88348ce293f72348025122695162d58a2ea9162130373c086e6ca
     * <br><br>
     * array = dbe42cc09c16704aa3d60127c60b4e1646fc6da1d4764aa517de053e65a663d7
     * <br>
     * [] = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
     * <br>
     * array + [] = 31705afb66ed4156539ece8bdff843215f1a7787b105d39275ab4b46b319961e
     * <br><br>
     * hash = 664114d8cb171b123b78b6e3c402e046fba1f8dc4c15b6afbbb3aa5627f5d662
     */
    @Test
    void testHashForEmpty() throws IOException {
        testHash("empty.json", "664114d8cb171b123b78b6e3c402e046fba1f8dc4c15b6afbbb3aa5627f5d662");
    }

    @Test
    void testConsistencyForArrays() throws IOException {
        testConsistency("array1.json", "array2.json");
    }

    @Test
    void testConsistencyForObjects() throws IOException {
        testConsistency("object1.json", "object2.json");
    }

    @Test
    void testConsistencyForComplexObjects() throws IOException {
        testConsistency("complex1.json", "complex2.json");
    }

    @Test
    void testHashIsDifferentForDifferentObjects() throws IOException {
        Object json1 = givenJson("complex1.json");
        Object json2 = givenJson("another.json");

        String hash1 = jsonHash.calculate(json1);
        String hash2 = jsonHash.calculate(json2);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    private void testHash(String file, String expectedHash) throws IOException {
        Object json = givenJson(file);

        String hash = jsonHash.calculate(json);

        assertThat(hash).isEqualTo(expectedHash);
    }

    private void testConsistency(String file1, String file2) throws IOException {
        Object json1 = givenJson(file1);
        Object json2 = givenJson(file2);

        String hash1 = jsonHash.calculate(json1);
        String hash2 = jsonHash.calculate(json2);

        assertThat(hash1).isEqualTo(hash2);
    }
}
