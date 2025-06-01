package com.pedestriamc.namecolor.manager;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.api.color.Gradient;
import com.pedestriamc.namecolor.api.color.painter.Painter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GradientManager {

    private final NameColor nameColor;

    private final Map<String, Gradient> gradients;

    public GradientManager(NameColor nameColor) {
        this.nameColor = nameColor;
        gradients = new HashMap<>();
    }
}
