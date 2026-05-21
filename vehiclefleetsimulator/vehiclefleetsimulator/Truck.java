// Truck.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Truck extends LandVehicle {
    private static final Image IMAGE = new Image(Truck.class.getResourceAsStream("Assets/truck.png"));
    private static final double WIDTH = 60;
    private static final double LENGTH = 160;

    protected Truck(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends LandVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Truck(this);
        }
    }
}