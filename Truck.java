// Truck.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;
import java.io.File;

public class Truck extends LandVehicle {

    private static final String TRUCK_ASSETS_PATH =
            "/home/mohamed-khairy/Desktop/JavaProject/poject/src/com/vehiclefleetsimulator/vehiclefleetsimulator/Assets/Truck/";

    private static final Image IMAGE =
            new Image(new File(TRUCK_ASSETS_PATH + "up.png").toURI().toString());

    private static final double WIDTH = 120;
    private static final double LENGTH = 150;

    protected Truck(Builder builder) {
        builder.dimensions(WIDTH, LENGTH);
        super(builder);

        setImgDefault(IMAGE);
        setImgUp(new Image(new File(TRUCK_ASSETS_PATH + "up.png").toURI().toString()));
        setImgDown(new Image(new File(TRUCK_ASSETS_PATH + "down.png").toURI().toString()));
        setImgLeft(new Image(new File(TRUCK_ASSETS_PATH + "left.png").toURI().toString()));
        setImgRight(new Image(new File(TRUCK_ASSETS_PATH + "right.png").toURI().toString()));
        setImgUpLeft(new Image(new File(TRUCK_ASSETS_PATH + "upLeft.png").toURI().toString()));
        setImgUpRight(new Image(new File(TRUCK_ASSETS_PATH + "upRight.png").toURI().toString()));
        setImgDownLeft(new Image(new File(TRUCK_ASSETS_PATH + "downLeft.png").toURI().toString()));
        setImgDownRight(new Image(new File(TRUCK_ASSETS_PATH + "downRight.png").toURI().toString()));

        imageView.setImage(this.imgDefault);
    }

    public static class Builder extends LandVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Truck(this);
        }
    }
}