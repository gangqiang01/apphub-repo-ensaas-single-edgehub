package com.m2m.management.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfig {
    public static String path = System.getProperty("java.io.tmpdir");
}
