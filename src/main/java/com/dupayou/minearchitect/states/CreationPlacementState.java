package com.dupayou.minearchitect.states;

import com.dupayou.minearchitect.model.Creation;

public class CreationPlacementState {
    private static Creation activeCreation = null;
    private static int rotation = 0;

    public static void setActiveCreation(Creation creation) {
        activeCreation = creation;
        rotation = 0;
    }

    public static void rotate() {
        rotation = (rotation + 1) % 4;
    }

    public static Creation getActiveCreation() {
        return activeCreation;
    }

    public static int getRotation() {
        return rotation;
    }

    public static void clearActiveCreation() {
        activeCreation = null;
        rotation = 0;
    }
}
