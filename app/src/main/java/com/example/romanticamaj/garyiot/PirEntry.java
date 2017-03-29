package com.example.romanticamaj.garyiot;

import java.util.Map;

/**
 * Model class for Firebase data entries
 */
public class PirEntry {

    Long timestamp;
    String name;
    Map<String, Float> annotations;

    public PirEntry() {
    }

    public PirEntry(Long timestamp, String strImgName, Map<String, Float> annotations) {
        this.timestamp = timestamp;
        this.name = strImgName;
        this.annotations = annotations;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getImageName() {
        return name;
    }

    public Map<String, Float> getAnnotations() {
        return annotations;
    }
}