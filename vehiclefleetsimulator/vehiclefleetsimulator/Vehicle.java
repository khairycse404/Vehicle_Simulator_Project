package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.scene.image.Image;

import javafx.scene.paint.Color;


public abstract class Vehicle extends Block implements Movable{
    private double nextX;
    private double nextY;
    private double nextZ;
//    private double nextDirection;
    private double speed;
    private double vX;
    private double vY;
    private double vZ;
    private double a;
    private Alignment allignment;

    private Trackable lastCollide;

    static int collisionsCount = 0;

    boolean controlled = false;

    protected Vehicle(Builder builder){
        super(builder.centerX, builder.centerY, builder.width, builder.length, builder.image, builder.mass);
        this.speed = builder.speed;
        this.a = builder.a;
        this.direction = builder.direction;
//        this.nexdDirection = builder.direction;
//        this.nextX = centerX; // initialization
//        this.nextY = centerY;
        this.nextX = getCenterX() + vX;
        this.nextY = getCenterY() + vY;
        this.allignment = builder.allignment;

        this.enableControls();
    }

    public abstract static class Builder{
        protected double centerX;
        protected double centerY;
        protected double centerZ;
        protected double width; // in X-axis
        protected double length; // in Y-axis
        protected Image image;
        protected double mass;
        protected double speed;
        protected double vZ;
        protected double direction;
        protected double a = 0;
        protected Alignment allignment = Alignment.VERTICAL;

        public Builder setCenterX(double centerX) {
            this.centerX = centerX;
            return this;
        }

        public Builder setCenterY(double centerY) {
            this.centerY = centerY;
            return this;
        }

        public Builder setCenterZ(double centerZ) {
            this.centerZ = centerZ;
            return this;
        }

        public Builder setWidth(double width) {
            this.width = width;
            return this;
        }

        public Builder setLength(double length) {
            this.length = length;
            return this;
        }

        public Builder dimensions(double width, double length){
            this.width = width;
            this.length = length;
            return this;
        }

        public Builder setImage(Image image) {
            this.image = image;
            return this;
        }

        public Builder setMass(double mass) {
            this.mass = mass;
            return this;
        }

        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }

        public Builder vZ(double vZ){
            this.vZ =vZ;
            return this;
        }

        public Builder direction(double direction) {
            this.direction = direction;
            return this;
        }

        public Builder acceleration(double acceleration){
            this.a = acceleration;
            return this;
        }

        public Builder alignment(Alignment alignment) {
            this.allignment = alignment;
            return this;
        }

        public abstract Vehicle build();
    }

    @Override
    public void moveX(double vX) {
        nextX += vX;
    }
    @Override
    public void moveY(double vY) {
        nextY += vY;
    }
    @Override
    public void moveZ(double vZ) {
         setCenterZ(getCenterZ() + vZ);
    }
    @Override
    public void move(double vX, double vY) {
        moveX(vX); moveY(vY);
    }

    @Override
    public void accelerate(double a) {
        setSpeed(speed + a); // the method handles speed constraints
    }

    /*
    * This method was intended to apply turning animation by turning by chunks, but it's commented for now for simplicity ( Easier Collision handeling)

    double turningDegrees; // turning degrees left
    @Override
    public void turn(double degrees, double chunk) {
        if( ((degrees > 0) && (degrees > turningDegrees)) || ((degrees < 0) && (degrees < turningDegrees))){
            turningDegrees = degrees;
            turn(turningDegrees, chunk);
        } else {
            turningDegrees = (degrees > 0) ? (turningDegrees - chunk) : (turningDegrees + chunk);
            direction = (degrees > 0) ? (direction - chunk) : (direction + chunk);
        }
    }
     */

    public void turnRight(){
        rotate(Directions.RIGHT);
    }
    public void turnLeft(){
       rotate(Directions.LEFT);
    }

    @Override
    public Trackable getLastCollide() {
        return lastCollide;
    }

    @Override
    public boolean isControlled(){
        return controlled;
    }
    public void setControlled(boolean state){
        this.controlled = state;
    }

    protected double[] collisionCheck() {
        for(Trackable t: Trackable.observed){
            if (t == this) continue; // don't collide with myself
            if (t instanceof Block){
                Block other = (Block) t;
                double zDist = 0;
                if (!(t instanceof Map.Barrier barrier && (barrier.TYPE == Map.Barrier.TYPES.EDGE || this instanceof Submarine) ) ) {
                    zDist = Math.abs(this.getCenterZ() - other.getCenterZ());
                } /*
                if the barrier is edge barrier, set its Z coordinate to be same as this vehicle (collide at any altitude)
                for the submarine it should collide with all barriers*/
                if (zDist > 5) continue;
            /* Logic for collision checking and handling
               #NOTE: Old approach
                    #TODO: Update the notes.
                 1- Collision Checking
                    i. First Condition
                        *This object is vertically aligned and the other is horizontal or vice versa:
                            - this.collisionCircle has a diameter of this vehicle's length.
                            - other's has a diameter of other's width
                            - ignore no collision conditions
                            - apply circle (ball) collision
                    ii. Second Condition
                        *Both have same alignment:
                            - Both have same circle diameter -> vehicle length
                            - case 1:
                                one is behind the other:
                                    apply ball collision
                            - case 2:
                                - distance (in direction of their shared allignment) between two vehicles is less than the safe distance but they are not overlapping:
                                    no collision : continue.
                 2- Overlapping Checking
                 3- Overlapping Handling
                 4- Collision Handling
             */

                /* vectors break things
                // this's allignment unit vector
                double uThis = Math.cos(Math.toRadians(this.getDirection()));
                // other's (it's normal to this's unit vector)
                double uOther = Math.cos(Math.toRadians(other.getDirection()));
                // normal vectors (for code readability)
                double nThis = uOther;
                double nOther = uThis;
                 */

                double criticalDist;
                double sideCriticalDist;

                // calculate distance
                double yDist = Math.abs(this.getNextY() - other.getNextY());
                double xDist = Math.abs(this.getNextX() - other.getNextX());
//                double dist = Math.sqrt(yDist * yDist + xDist * xDist);

                // check collision
// helper reads to avoid repeating this logic
                double thisHalfW = (this.getAllignment() == Alignment.HORIZONTAL) ? this.length/2 : this.width/2;
                double thisHalfL = (this.getAllignment() == Alignment.HORIZONTAL) ? this.width/2 : this.length/2;
                double otherHalfW = (other.getAllignment() == Alignment.HORIZONTAL) ? other.length/2 : other.width/2;
                double otherHalfL = (other.getAllignment() == Alignment.HORIZONTAL) ? other.width/2 : other.length/2;

                boolean colliding = xDist < (thisHalfW + otherHalfW)
                        && yDist < (thisHalfL + otherHalfL);

                if (!colliding) continue;

                double overlapX = (thisHalfW + otherHalfW) - xDist;
                double overlapY = (thisHalfL + otherHalfL) - yDist;

                if (overlapX < overlapY) {
                    this.nextX += (this.getNextX() > other.getNextX()) ? overlapX : -overlapX;
                } else {
                    this.nextY += (this.getNextY() > other.getNextY()) ? overlapY : -overlapY;
                }

                handleCollision(this, other); this.lastCollide = other;

            }
        }
        return new double[0];
    } /* This has high time complixity (n^2)
        #TODO: Move the collision checking logic to the Movable Interface,
            separate collision checking from collision handling.
            the static collision checking method in the interface should call the collision handeling object method if collision detected.

        #TODO: collisionCircles are actually nothing, remove them
    */


    /* same as collisions check, would be moved to interface, or could be a seperate list of turning points */
    private TurningPoint lastTurningPoint = null;
    public double turningPointsCheck() {
        for (Trackable t : Trackable.observed) {
            if (t instanceof TurningPoint tp) {
                double distX = this.getCenterX() - tp.getCenterX();
                double distY = this.getCenterY() - tp.getCenterY();
                double distance = Math.sqrt(distX * distX + distY * distY);

                if (distance < tp.RADIUS) {
                    if (lastTurningPoint == tp) continue; // already handled
                    lastTurningPoint = tp;

                    switch (tp.TYPE) {
                        case TURN -> {
                            switch (tp.DIRECTION) {
                                case RIGHT -> turnRight();
                                case LEFT  -> turnLeft();
                            }
                        }
                        case TAKE_OFF -> {
                            if (this instanceof AirVehicle av) av.setVz(tp.VZ_VALUE);
                        }
                        case LAND -> {
                            if (this instanceof AirVehicle av) av.setVz(tp.VZ_VALUE);
                        }
                        case HOLD_ALT -> {
                            if (this instanceof AirVehicle av) av.setVz(0);
                        }
                    }
                } else {
//                    if (lastTurningPoint == tp) lastTurningPoint = null; // remove last turning point if it's not handled
                }
            }
        }
        return direction;
    }

    @Override
    public double[] calculateNextPoint() {

        collisionCheck();
        turningPointsCheck();

        this.vX = speed * Math.sin(direction * Math.PI / 180.0);
        this.vY = - speed * Math.cos(direction * Math.PI / 180.0); // the negative is to make positive Y up
        move(vX, vY); moveZ(vZ);
        accelerate(a); // accelerate after everything

        // all the previous lines alter nextX, nextY, nextZ and nextDirection

        return new double[]{nextX, nextY, nextZ};
    }

    @Override
    public void update() {
        this.setCenter(nextX, nextY);
        moveZ(vZ);

//        System.out.println(getCenterX());
//        System.out.println(getCenterY());
    }

    public void launchControlPanel(){
        setControlled(true);
        // #TODO
//        Testing testing = new Testing(this);
//        testing.launch();
    }

    // Setters & Getters
    @Override
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if(Math.abs(speed) > Movable.MAX_SPEED) speed = Movable.MAX_SPEED * (speed / Math.abs(speed)) ; // multiply by its sign
        this.speed = speed;
    }

    @Override
    public double getVz() {
        return vZ;
    }

    public void setVz(double vZ) {
        if(Math.abs(vZ) > Movable.MAX_SPEED) vZ = Movable.MAX_SPEED * (vZ / Math.abs(vZ)) ; // multiply by its sign
        this.vZ = vZ;
    }

    @Override
    public double getAcceleration() {
        return a;
    }

    public void setAcceleration(double a) {
        this.a = a;
    }

    @Override
    public double getNextX(){
        return this.nextX;
    }
    @Override
    public double getNextY(){
        return this.nextY;
    }

    @Override
    public String toString() {
        return "Vehicle" + super.toString();
    }



    private double lastMouseX;
    private double lastMouseY;

    public void enableControls() {
        imageView.setFocusTraversable(true);
        imageView.setOnMousePressed(e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();

            imageView.requestFocus();
        });
  //ASSEM
    imageView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
               //DropShadow:effect 
                javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
                glow.setColor(Color.DODGERBLUE);
                glow.setRadius(20);
                glow.setSpread(0.6);
                imageView.setEffect(glow);
            } else {
               
                imageView.setEffect(null);
            }
        });
        imageView.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - lastMouseX;
            double dy = e.getSceneY() - lastMouseY;

            move(dx, dy); // move directly instead of changing speed

            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

//        imageView.setOnMouseClicked(e -> {
//            
//            switch (e.getButton()) {
//                case PRIMARY -> turnLeft();
//                case SECONDARY -> turnRight();
//            }
//        });


        imageView.setOnKeyPressed(e -> {
            //assem
            boolean handled = false;// بسبب هروب الفوكس
            switch (e.getCode()) {
                case SPACE,UP -> {  this.moveZ(1);
                    System.out.println(this.toString() + " ascend " + this.getCenterZ()); 
                    handled = true;
                     this.accelerate(0.8);
                }
                case C  ,DOWN   -> {  this.moveZ(-1);
                    System.out.println(this.toString() + " descend " + this.getCenterZ()); 
                    handled = true;    
                    this.accelerate(-0.8);
                }
                case LEFT -> {turnLeft();  handled = true;}
                 case RIGHT -> {turnRight();   handled = true;}
                 
                 
            }
            if (handled) {
                e.consume();//تمنع التشتت
            }
        });
    }
    public void enableControls() {
        imageView.setFocusTraversable(true);
        imageView.setOnMousePressed(e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();

            imageView.requestFocus();
        });
        //ASSEM
        imageView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                //DropShadow:effect 
                javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
                glow.setColor(Color.DODGERBLUE);
                glow.setRadius(20);
                glow.setSpread(0.6);
                imageView.setEffect(glow);
            } else {

                imageView.setEffect(null);
            }
        });
        imageView.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - lastMouseX;
            double dy = e.getSceneY() - lastMouseY;

            move(dx, dy); // move directly instead of changing speed

            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

imageView.setOnMouseClicked(e -> {
    // فرق بين الكليك والدراج (لو الماوس اتحرك أكتر من 5 بكسل → دراج مش كليك)
    double movedX = Math.abs(e.getSceneX() - lastMouseX);
    double movedY = Math.abs(e.getSceneY() - lastMouseY);
 
    if (movedX < 5 && movedY < 5 && e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
        launchControlPanel();
    }
});
        imageView.setOnKeyPressed(e -> {
            //assem
            boolean handled = false;// بسبب هروب الفوكس
            switch (e.getCode()) {
                case SPACE, UP -> {
                    this.moveZ(1);
                    System.out.println(this.toString() + " ascend " + this.getCenterZ());
                    handled = true;
                    this.accelerate(0.8);
                }
                case C, DOWN -> {
                    this.moveZ(-1);
                    System.out.println(this.toString() + " descend " + this.getCenterZ());
                    handled = true;
                    this.accelerate(-0.8);
                }
                case LEFT -> {
                    turnLeft();
                    handled = true;
                }
                case RIGHT -> {
                    turnRight();
                    handled = true;
                }

            }
            if (handled) {
                e.consume();//تمنع التشتت
            }
        });
    }


public void launchControlPanel() {
    setControlled(true);
    HelloApplication.selectVehicle(this);  
}

    
    /*
    Rotation is handled in Block.rotate()
    the mentioned method insures that Allignment, Direction are set correctly.
    #TODO: Remove any direction setters or getters and replace them with rotate.
     */
}

// #TODO: overload rotate method to have left and right from the enum DONE in Block class
