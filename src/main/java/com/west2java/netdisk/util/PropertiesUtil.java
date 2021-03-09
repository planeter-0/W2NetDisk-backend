package com.west2java.netdisk.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesUtil {
    @Value("${filepath}")
    public String filepath;
}
