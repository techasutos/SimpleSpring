package com.asu.resource;

import java.io.InputStream;

public interface Resource {
    InputStream getInputStream() throws Exception;
    String getFilename();
}