package com.pedestriamc.namecolor;

import java.util.HashMap;
import java.util.Map;

public class GradientManager {

    private final Map<String, Gradient> map;

    public GradientManager() {
        map = new HashMap<>();
    }

    public Gradient getGradient(String name) {
        return map.get(name);
    }

}
