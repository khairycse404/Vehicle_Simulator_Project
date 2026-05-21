// Motorcycle.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Motorcycle extends LandVehicle {
    private static final Image IMAGE = new Image(Motorcycle.class.getResourceAsStream("Assets/motorcycle.png"));
    private static final double WIDTH = 30;
    private static final double LENGTH = 70;

    protected Motorcycle(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends LandVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Motorcycle(this);
        }
    }
}