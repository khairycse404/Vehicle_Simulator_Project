// Boat.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Boat extends WaterVehicle {
    private static final Image IMAGE = new Image(Boat.class.getResourceAsStream("Assets/boat.png"));
    private static final double WIDTH = 60;
    private static final double LENGTH = 120;

    protected Boat(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends WaterVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Boat(this);
        }
    }

    @Override
    public void setCenterZ(double z) {} // boats don't sink

    @Override
    public double getVz() {
        return 0;
    }

    @Override
    public void setVz(double vz) {} // boats don't go underwater
}