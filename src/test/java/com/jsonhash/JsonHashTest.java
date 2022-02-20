package com.jsonhash;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.jsonhash.testutil.JsonUtils.givenJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonHashTest {
    private final JsonHash jsonHash = new JsonHash(DigestUtils::sha256Hex);

    /**
     * a=ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
     * 1=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
     * <br><br>
     * b=3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d
     * 2=d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35
     * <br><br>
     * c=2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6
     * 3=4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce
     * <br><br>
     * ah=a+1=a5a1772027bf03eac81b42d789211f388b7628eacae11d50b850026048c4576b => 2
     * bh=b+2=bde6dfd024854b6603e5d156e8e98ca6c6c86dd359ed5cc151d38964d44a5498 => 3
     * ch=c+3=790a2cf4aef9cfef7cb1efd1afd37930163825c5b08cd9d0dd2f370dd30ee700 => 1
     * <br><br>
     * hash=ch+ah+bh=e09ab0cda1fff7d9e64a630bbad5b6c611e0ecec15629b081202268fd5d8b297
     */
    @Test
    void testHashForMap() throws IOException {
        testHash("map1.json", "e09ab0cda1fff7d9e64a630bbad5b6c611e0ecec15629b081202268fd5d8b297");
    }

    /**
     * a=ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
     * <br><br>
     * b=3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d
     * <br><br>
     * c=2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6
     * <br><br>
     * hash=c+b+a=f71b5a2b8d2b4ed795489a9044d19848db2a60b76691961c6e16d89d4867c0d3
     */
    @Test
    void testHashForList() throws IOException {
        testHash("list1.json", "f71b5a2b8d2b4ed795489a9044d19848db2a60b76691961c6e16d89d4867c0d3");
    }

    @Test
    void testConsistencyForLists() throws IOException {
        testConsistency("list1.json", "list2.json");
    }

    @Test
    void testConsistencyForMaps() throws IOException {
        testConsistency("map1.json", "map2.json");
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
