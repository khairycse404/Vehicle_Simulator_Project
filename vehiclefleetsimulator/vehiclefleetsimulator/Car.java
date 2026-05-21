package com.vehiclefleetsimulator.vehiclefleetsimulator;
import javafx.scene.image.Image;

public class Car extends LandVehicle{
    private static final Image IMAGE = new Image(LandVehicle.class.getResourceAsStream("Assets/car.png"));
    private static final double WIDTH = 50;
    private static final double LENGTH = 100;


    protected Car(Builder builder) {
        super((Builder) builder.dimensions(WIDTH, LENGTH));
        imageView.setImage(IMAGE);
    }
    public static class Builder extends LandVehicle.Builder{
//        public Builder(){
//            super();
//        }
        @Override
        public Vehicle build(){
            return new Car(this);
        }
    }

    public static int getCount(){
        int c = 0;
        for(Trackable t: Trackable.observed){
            if (t instanceof Car){
                c++;
            }
        }

        return c;
    }







    /*
        #TODO: a way to handle side (horizontal) image and vertical image in the update method
        @override
        update(){
        super.update()
        switch allignmenmt ...

        or the allignment method should handel it in the block (modularity)
        }
     */
}
