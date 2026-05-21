// Helicopter.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Helicopter extends AirVehicle {
    private static final Image IMAGE = new Image(Helicopter.class.getResourceAsStream("Assets/helicopter.png"));
    private static final double WIDTH = 100;
    private static final double LENGTH = 140;

    protected Helicopter(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends AirVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Helicopter(this);
        }
    }
}