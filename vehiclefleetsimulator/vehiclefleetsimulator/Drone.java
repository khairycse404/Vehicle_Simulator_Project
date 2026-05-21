// Drone.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Drone extends AirVehicle {
    private static final Image IMAGE = new Image(Drone.class.getResourceAsStream("Assets/drone.png"));
    private static final double WIDTH = 40;
    private static final double LENGTH = 40;

    protected Drone(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends AirVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Drone(this);
        }
    }
}