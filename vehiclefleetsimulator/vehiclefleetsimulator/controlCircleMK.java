package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;

public class controlCircleMK extends Application {

    @Override
    public void start(Stage stage) {

        HBox root = new HBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #06405a;");

        String inputStyle = "-fx-background-color: #163a4a; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: #189AB4; -fx-border-radius: 10; -fx-padding: 6; -fx-font-size: 14px;";
        String boxStyle = "-fx-background-color: #1b263b; -fx-background-radius: 15; -fx-padding: 10;";
        String labelStyle = "-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;";

        TextField txtLand = new TextField("0");
        txtLand.setStyle(inputStyle);
        txtLand.setPrefWidth(45);
        txtLand.setAlignment(Pos.CENTER);
        txtLand.setTooltip(new Tooltip("Number of Land Vehicles"));

        TextField txtWater = new TextField("0");
        txtWater.setStyle(inputStyle);
        txtWater.setPrefWidth(45);
        txtWater.setAlignment(Pos.CENTER);
        txtWater.setTooltip(new Tooltip("Number of Water Vehicles"));

        TextField txtAir = new TextField("0");
        txtAir.setStyle(inputStyle);
        txtAir.setPrefWidth(45);
        txtAir.setAlignment(Pos.CENTER);
        txtAir.setTooltip(new Tooltip("Number of Air Vehicles"));

        GridPane rightSide = new GridPane();
        rightSide.setHgap(15);
        rightSide.setVgap(20);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setPadding(new Insets(30));
        rightSide.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label lblMax = new Label("Maximum Speed:");
        lblMax.setStyle(labelStyle);
        TextField txtMaxSpeed = new TextField("0.0");
        txtMaxSpeed.setStyle(inputStyle);
        txtMaxSpeed.setPrefWidth(150);
        txtMaxSpeed.setEditable(false);

        Label lblAvg = new Label("Average Speed:");
        lblAvg.setStyle(labelStyle);
        TextField txtAvgSpeed = new TextField("0.0");
        txtAvgSpeed.setStyle(inputStyle);
        txtAvgSpeed.setPrefWidth(150);
        txtAvgSpeed.setEditable(false);

        Label lblMin = new Label("Minimum Speed:");
        lblMin.setStyle(labelStyle);
        TextField txtMinSpeed = new TextField("0.0");
        txtMinSpeed.setStyle(inputStyle);
        txtMinSpeed.setPrefWidth(150);
        txtMinSpeed.setEditable(false);

        VBox left = new VBox(25);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(30));
        left.setPrefWidth(300);
        left.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label title = new Label("VEHICLE STATUS");
        title.setStyle("-fx-text-fill:white; -fx-font-size:20px; -fx-font-weight:bold;");

        String carPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Car2.jpg";
        ImageView car = new ImageView(new Image(new File(carPath).toURI().toString()));
        car.setFitWidth(60);
        car.setFitHeight(60);

        VBox landBox = new VBox(10, car, txtLand);
        landBox.setAlignment(Pos.CENTER);
        landBox.setStyle(boxStyle);
        landBox.setPrefWidth(120);

        String boatPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Ship2.jpg";
        ImageView boat = new ImageView(new Image(new File(boatPath).toURI().toString()));
        boat.setFitWidth(60);
        boat.setFitHeight(60);

        VBox waterBox = new VBox(10, boat, txtWater);
        waterBox.setAlignment(Pos.CENTER);
        waterBox.setStyle(boxStyle);
        waterBox.setPrefWidth(120);

        String airPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Plane2.jpg";
        ImageView plane = new ImageView(new Image(new File(airPath).toURI().toString()));
        plane.setFitWidth(60);
        plane.setFitHeight(60);

        VBox airBox = new VBox(10, plane, txtAir);
        airBox.setAlignment(Pos.CENTER);
        airBox.setStyle(boxStyle);
        airBox.setPrefWidth(120);

        HBox topRow = new HBox(15, landBox, waterBox);
        topRow.setAlignment(Pos.CENTER);

        VBox vehiclesContainer = new VBox(15, topRow, airBox);
        vehiclesContainer.setAlignment(Pos.CENTER);

        left.getChildren().addAll(title, vehiclesContainer);

        Label l2 = new Label("Speed Step:");
        l2.setStyle(labelStyle);

        TextField txtAcc = new TextField("1");
        txtAcc.setEditable(false);
        txtAcc.setStyle(inputStyle + "-fx-pref-width: 60; -fx-alignment: center;");

        Button btnPlus = new Button("+");
        Button btnMinus = new Button("-");
        String smallBtnStyle = "-fx-background-color: #38bdf8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-min-width: 30;";
        btnPlus.setStyle(smallBtnStyle);
        btnMinus.setStyle(smallBtnStyle);

        btnPlus.setOnAction(e -> {
            int val = Integer.parseInt(txtAcc.getText());
            txtAcc.setText(String.valueOf(val + 1));
        });

        btnMinus.setOnAction(e -> {
            int val = Integer.parseInt(txtAcc.getText());
            if (val > 0) {
                txtAcc.setText(String.valueOf(val - 1));
            }
        });

        HBox accControl = new HBox(5, btnMinus, txtAcc, btnPlus);
        accControl.setAlignment(Pos.CENTER_LEFT);

        Label l7 = new Label("Constant:");
        l7.setStyle(labelStyle);

        TextField txtConst = new TextField("0");
        txtConst.setStyle(inputStyle);

        Button apply = new Button("Apply");
        apply.setStyle("-fx-background-color:#22c55e; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-padding:8;");

        rightSide.add(lblMax, 0, 0);
        rightSide.add(txtMaxSpeed, 1, 0);

        rightSide.add(lblAvg, 0, 1);
        rightSide.add(txtAvgSpeed, 1, 1);

        rightSide.add(lblMin, 0, 2);
        rightSide.add(txtMinSpeed, 1, 2);

        rightSide.add(l2, 0, 3);
        rightSide.add(accControl, 1, 3);

        rightSide.add(l7, 0, 4);
        rightSide.add(txtConst, 1, 4);
        rightSide.add(apply, 2, 4);

        Button inc = new Button("Increase");
        Button dec = new Button("Decrease");
        Button stop = new Button("STOP");

        String actionBtnStyle = "-fx-background-color:#38bdf8; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-pref-width:95; -fx-padding:8;";
        inc.setStyle(actionBtnStyle);
        dec.setStyle(actionBtnStyle);
        stop.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-pref-width:95; -fx-padding:8;");

        inc.setOnAction(e -> {
            double value = Double.parseDouble(txtAcc.getText());
            for (Trackable t : Trackable.observed) {
                if (t instanceof Vehicle v) {
                    v.setSpeed(v.getSpeed() + value);
                }
            }
        });

        dec.setOnAction(e -> {
            double value = Double.parseDouble(txtAcc.getText());
            for (Trackable t : Trackable.observed) {
                if (t instanceof Vehicle v) {
                    double currentSpeed = v.getSpeed();
                    double newSpeed = currentSpeed - value;

                    if (currentSpeed > 0 && newSpeed < 0) {
                        newSpeed = 0;
                    } else if (currentSpeed < 0 && newSpeed > 0) {
                        newSpeed = 0;
                    }

                    v.setSpeed(newSpeed);
                }
            }
        });

        stop.setOnAction(e -> {
            for (Trackable t : Trackable.observed) {
                if (t instanceof Vehicle v) {
                    v.setSpeed(0);
                }
            }
        });

        apply.setOnAction(e -> {
            double baseSpeed = Double.parseDouble(txtConst.getText());
            int index = 0;

            for (Trackable t : Trackable.observed) {
                if (t instanceof Vehicle v) {
                    double offset = (index % 5) * 0.3;
                    v.setSpeed(baseSpeed + offset);
                    index++;
                }
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int countL = 0;
                int countW = 0;
                int countA = 0;

                double totalSpeed = 0;
                double maxSpeed = 0;
                double minSpeed = 0;
                int vehicleCount = 0;

                for (Trackable t : Trackable.observed) {

                    if (t instanceof LandVehicle) {
                        if (t instanceof Vehicle v && v.getCenterX() > 0 && v.getCenterY() > 0) {
                            countL++;
                        }
                    } else if (t instanceof WaterVehicle) {
                        countW++;
                    } else if (t instanceof AirVehicle) {
                        countA++;
                    }

                    if (t instanceof Vehicle v) {
                        double speed = Math.abs(v.getSpeed());

                        if (vehicleCount == 0) {
                            maxSpeed = speed;
                            minSpeed = speed;
                        } else {
                            if (speed > maxSpeed) maxSpeed = speed;
                            if (speed < minSpeed) minSpeed = speed;
                        }

                        totalSpeed += speed;
                        vehicleCount++;
                    }
                }

                txtLand.setText(String.valueOf(countL));
                txtWater.setText(String.valueOf(countW));
                txtAir.setText(String.valueOf(countA));

                if (vehicleCount > 0) {
                    txtMaxSpeed.setText(String.format("%.2f", maxSpeed));
                    txtAvgSpeed.setText(String.format("%.2f", totalSpeed / vehicleCount));
                    txtMinSpeed.setText(String.format("%.2f", minSpeed));
                } else {
                    txtMaxSpeed.setText("0.00");
                    txtAvgSpeed.setText("0.00");
                    txtMinSpeed.setText("0.00");
                }
            }
        };

        timer.start();

        HBox buttons = new HBox(15, inc, dec, stop);
        buttons.setAlignment(Pos.CENTER);

        VBox rightWrapper = new VBox(20, rightSide, buttons);
        rightWrapper.setAlignment(Pos.CENTER);

        root.getChildren().addAll(left, rightWrapper);

        Scene scene = new Scene(root, 950, 650);
        stage.setScene(scene);
        stage.setTitle("Vehicle Simulation System");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
