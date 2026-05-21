// Submarine.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

public class Submarine extends WaterVehicle {
    private static final Image IMAGE = new Image(Submarine.class.getResourceAsStream("Assets/submarine.png"));
    private static final double WIDTH = 60;
    private static final double LENGTH = 160;
    public static final double MAX_DEPTH = -100; // maximum negative Z (deepest point)

    protected Submarine(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }

    public static class Builder extends WaterVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Submarine(this);
        }
    }

    @Override
    public void setCenterZ(double z) {
        // clamp Z between MAX_DEPTH and 0 (submarines can't go above water surface)
        this.centerZ = Math.max(MAX_DEPTH, Math.min(0, z));
    }

    @Override
    public void setVz(double vz) {
        // prevent going above surface or below max depth
        if (centerZ >= 0 && vz > 0) vz = 0;
        if (centerZ <= MAX_DEPTH && vz < 0) vz = 0;
        super.setVz(vz);
    }

    @Override
    public void moveZ(double vZ) {
        double next = centerZ + vZ * Movable.PERIODIC_TIME_FACTOR;
        setCenterZ(next);
    }

    @Override
    public void update() {
        super.update();
        // fade out imageView as submarine goes deeper: 0 = fully transparent at MAX_DEPTH
        double depthRatio = centerZ / MAX_DEPTH; // 0 at surface, 1 at max depth
        imageView.setOpacity(1.0 - (Math.min(7.7*depthRatio, 0.8))); // multiplied by 7.7 to make animation faster
    }
}