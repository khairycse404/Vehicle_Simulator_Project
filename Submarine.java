// Submarine.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;
import java.io.File;

public class Submarine extends WaterVehicle {

    private static final String SUBMARINE_ASSETS_PATH =
            "/home/mohamed-khairy/Desktop/JavaProject/poject/src/com/vehiclefleetsimulator/vehiclefleetsimulator/Assets/Submarine/";

    private static final Image IMAGE =
            new Image(new File(SUBMARINE_ASSETS_PATH + "up.png").toURI().toString());

    private static final double WIDTH = 110;
    private static final double LENGTH = 160;

    public static final double MAX_DEPTH = -100;

    protected Submarine(Builder builder) {
        builder.dimensions(WIDTH, LENGTH);
        super(builder);

        setImgDefault(IMAGE);

        setImgUp(new Image(new File(SUBMARINE_ASSETS_PATH + "up.png").toURI().toString()));
        setImgDown(new Image(new File(SUBMARINE_ASSETS_PATH + "down.png").toURI().toString()));
        setImgLeft(new Image(new File(SUBMARINE_ASSETS_PATH + "left.png").toURI().toString()));
        setImgRight(new Image(new File(SUBMARINE_ASSETS_PATH + "right.png").toURI().toString()));
        setImgUpLeft(new Image(new File(SUBMARINE_ASSETS_PATH + "upLeft.png").toURI().toString()));
        setImgUpRight(new Image(new File(SUBMARINE_ASSETS_PATH + "upRight.png").toURI().toString()));
        setImgDownLeft(new Image(new File(SUBMARINE_ASSETS_PATH + "downLeft.png").toURI().toString()));
        setImgDownRight(new Image(new File(SUBMARINE_ASSETS_PATH + "downRight.png").toURI().toString()));

        imageView.setImage(this.imgDefault);
    }

    public static class Builder extends WaterVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Submarine(this);
        }
    }

    @Override
    public void setCenterZ(double z) {
        this.centerZ = Math.max(MAX_DEPTH, Math.min(0, z));
    }

    @Override
    public void setVz(double vz) {
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

        double depthRatio = centerZ / MAX_DEPTH;
        imageView.setOpacity(1.0 - Math.min(7.7 * depthRatio, 0.8));

        double scale = 1.0 - Math.min(0.5 * depthRatio, 0.6);

        this.length = LENGTH * scale;
        this.width = WIDTH * scale;

        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }
}