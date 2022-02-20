package com.jsonhash.sha;

import com.jsonhash.HashFunction;
import org.apache.commons.codec.digest.DigestUtils;

public class ApacheSha256 implements HashFunction {

    @Override
    public String apply(String data) {
        return DigestUtils.sha256Hex(data);
    }
}
