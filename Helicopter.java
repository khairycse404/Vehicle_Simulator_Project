// Helicopter.java
package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;
import java.io.File;

public class Helicopter extends AirVehicle {

    private static final String HELI_GROUND_PATH =
            "/home/mohamed-khairy/Desktop/JavaProject/poject/src/com/vehiclefleetsimulator/vehiclefleetsimulator/Assets/Helicopter/Ground/";

    private static final String HELI_FLYING_PATH =
            "/home/mohamed-khairy/Desktop/JavaProject/poject/src/com/vehiclefleetsimulator/vehiclefleetsimulator/Assets/Helicopter/Flying/";

    private static final Image IMAGE =
            new Image(new File(HELI_GROUND_PATH + "up.png").toURI().toString());

    private static final double WIDTH = 100;
    private static final double LENGTH = 140;

    protected Helicopter(Builder builder) {
        builder.dimensions(WIDTH, LENGTH);
        super(builder);

        setImgDefault(IMAGE);

        setImgUp(new Image(new File(HELI_GROUND_PATH + "up.png").toURI().toString()));
        setImgDown(new Image(new File(HELI_GROUND_PATH + "down.png").toURI().toString()));
        setImgLeft(new Image(new File(HELI_GROUND_PATH + "left.png").toURI().toString()));
        setImgRight(new Image(new File(HELI_GROUND_PATH + "right.png").toURI().toString()));
        setImgUpLeft(new Image(new File(HELI_GROUND_PATH + "upLeft.png").toURI().toString()));
        setImgUpRight(new Image(new File(HELI_GROUND_PATH + "upRight.png").toURI().toString()));
        setImgDownLeft(new Image(new File(HELI_GROUND_PATH + "downLeft.png").toURI().toString()));
        setImgDownRight(new Image(new File(HELI_GROUND_PATH + "downRight.png").toURI().toString()));

        imageView.setImage(this.imgDefault);
    }

    public static class Builder extends AirVehicle.Builder {
        @Override
        public Vehicle build() {
            return new Helicopter(this);
        }
    }

    @Override
    public void setFlightState(FlightState flightState) {

        if (flightState != this.flightState) {

            switch (flightState) {

                case FLYING -> {

                    setImgUp(new Image(new File(HELI_FLYING_PATH + "up.png").toURI().toString()));
                    setImgDown(new Image(new File(HELI_FLYING_PATH + "down.png").toURI().toString()));
                    setImgLeft(new Image(new File(HELI_FLYING_PATH + "left.png").toURI().toString()));
                    setImgRight(new Image(new File(HELI_FLYING_PATH + "right.png").toURI().toString()));
                    setImgUpLeft(new Image(new File(HELI_FLYING_PATH + "upLeft.png").toURI().toString()));
                    setImgUpRight(new Image(new File(HELI_FLYING_PATH + "upRight.png").toURI().toString()));
                    setImgDownLeft(new Image(new File(HELI_FLYING_PATH + "downLeft.png").toURI().toString()));
                    setImgDownRight(new Image(new File(HELI_FLYING_PATH + "downRight.png").toURI().toString()));
                }

                case LANDING -> {

                    setImgUp(new Image(new File(HELI_GROUND_PATH + "up.png").toURI().toString()));
                    setImgDown(new Image(new File(HELI_GROUND_PATH + "down.png").toURI().toString()));
                    setImgLeft(new Image(new File(HELI_GROUND_PATH + "left.png").toURI().toString()));
                    setImgRight(new Image(new File(HELI_GROUND_PATH + "right.png").toURI().toString()));
                    setImgUpLeft(new Image(new File(HELI_GROUND_PATH + "upLeft.png").toURI().toString()));
                    setImgUpRight(new Image(new File(HELI_GROUND_PATH + "upRight.png").toURI().toString()));
                    setImgDownLeft(new Image(new File(HELI_GROUND_PATH + "downLeft.png").toURI().toString()));
                    setImgDownRight(new Image(new File(HELI_GROUND_PATH + "downRight.png").toURI().toString()));
                }
            }

            rotate(0);
        }

        super.setFlightState(flightState);
    }
}