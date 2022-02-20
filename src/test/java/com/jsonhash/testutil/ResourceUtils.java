package com.jsonhash.testutil;

import com.jsonhash.JsonHashTest;

import java.io.InputStream;

public class ResourceUtils {

    public static InputStream getTestResource(String file) {
        return ResourceUtils.class.getResourceAsStream(file);
    }
}
