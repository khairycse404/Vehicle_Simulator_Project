
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

       
        double[] targetSpeed = {-1}; 

        HBox root = new HBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #06405a;");

        String inputStyle = "-fx-background-color: #163a4a; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: #189AB4; -fx-border-radius: 10; -fx-padding: 6; -fx-font-size: 14px;";
        String boxStyle = "-fx-background-color: #1b263b; -fx-background-radius: 15; -fx-padding: 10;";

        TextField txtLand = new TextField("0"); 
        txtLand.setStyle(inputStyle); 
        txtLand.setPrefColumnCount(2); 
        txtLand.setPrefWidth(45);     
        txtLand.setAlignment(Pos.CENTER);
        txtLand.setTooltip(new Tooltip("Number of Land Vehicles"));  

        TextField txtWater = new TextField("0"); 
        txtWater.setStyle(inputStyle); 
        txtWater.setPrefColumnCount(2);
        txtWater.setPrefWidth(45);
        txtWater.setAlignment(Pos.CENTER);
        txtWater.setTooltip(new Tooltip("Number of Water Vehicles"));

        TextField txtAir = new TextField("0"); 
        txtAir.setStyle(inputStyle); 
        txtAir.setPrefColumnCount(2);
        txtAir.setPrefWidth(45);
        txtAir.setAlignment(Pos.CENTER);
        txtAir.setTooltip(new Tooltip("Number of Air Vehicles"));
        
        // ===== RIGHT SIDE ===== 
        GridPane rightSide = new GridPane(); 
        rightSide.setHgap(15); rightSide.setVgap(20); rightSide.setAlignment(Pos.CENTER);
        rightSide.setPadding(new Insets(30));
        rightSide.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        String labelStyle = "-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;";

        Label l1 = new Label("Speed:"); l1.setStyle(labelStyle);
        TextField txtSpeed = new TextField("0"); 
        txtSpeed.setStyle(inputStyle); 
        txtSpeed.setPrefWidth(150);
        txtSpeed.setEditable(false); 

        // -----------------------------------------------------------------
        // ----- الرادار بتاعنا (AnimationTimer) لتحديث البيانات لحظياً -----
        // -----------------------------------------------------------------
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int countL = 0;
                int countW = 0;
                int countA = 0;
                double totalSpeed = 0;
                int vehicleCount = 0;

                // بنلف على كل المركبات الموجودة في المحاكاة حالياً
                for (Trackable t : Trackable.observed) {
                    
                    // --- التعديل هنا: تطبيق السرعة المطلوبة بقوة على كل المركبات لحظياً ---
                    if (t instanceof Vehicle v) {
                        if (targetSpeed[0] != -1) {
                            v.setSpeed(targetSpeed[0]);
                        }
                    }

                    // التعديل الذكي هنا: بنفحص إن الكائن مركبة أرضية حقيقية داخل حدود الشاشة الظاهرة
                    if (t instanceof LandVehicle) {
                        if (t instanceof Vehicle v && v.getCenterX() > 0 && v.getCenterY() > 0) {
                            countL++;
                        }
                    } 
                    else if (t instanceof WaterVehicle) countW++;
                    else if (t instanceof AirVehicle) countA++;

                    // بنحسب السرعة (هناخد متوسط السرعة لكل المركبات عشان نعرضه)
                    if (t instanceof Vehicle v) {
                        totalSpeed += v.getSpeed();
                        vehicleCount++;
                    }
                }

                // تحديث العدادات اللي على الشمال بالقيم الجديدة المفلترة
                txtLand.setText(String.valueOf(countL));
                txtWater.setText(String.valueOf(countW));
                txtAir.setText(String.valueOf(countA));

                // تحديث السرعة (بنعرض متوسط السرعة برقم عشري بسيط)
                if (vehicleCount > 0) {
                    txtSpeed.setText(String.format("%.1f", (totalSpeed / vehicleCount)));
                } else {
                    txtSpeed.setText("0.0");
                }
            }
        };
        timer.start(); // تشغيل الرادار
        // -----------------------------------------------------------------

        VBox left = new VBox(25);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(30));
        left.setPrefWidth(300); 
        left.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label title = new Label("VEHICLE STATUS");
        title.setStyle("-fx-text-fill:white; -fx-font-size:20px; -fx-font-weight:bold;");

        // LAND
        String carPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Car2.jpg";
        ImageView car = new ImageView(new Image(new File(carPath).toURI().toString()));
        car.setFitWidth(60); car.setFitHeight(60);
        
        VBox landBox = new VBox(10, car, txtLand);
        landBox.setAlignment(Pos.CENTER);
        landBox.setStyle(boxStyle); landBox.setPrefWidth(120);

        // WATER
        String boatPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Ship2.jpg";
        ImageView boat = new ImageView(new Image(new File(boatPath).toURI().toString()));
        boat.setFitWidth(60); boat.setFitHeight(60);
        
        VBox waterBox = new VBox(10, boat, txtWater);
        waterBox.setAlignment(Pos.CENTER);
        waterBox.setStyle(boxStyle); waterBox.setPrefWidth(120);

        // AIR
        String airPath = "/home/mohamed-khairy/Documents/JavaProject/poject/src/Images2/Plane2.jpg";
        ImageView plane = new ImageView(new Image(new File(airPath).toURI().toString()));
        plane.setFitWidth(60); plane.setFitHeight(60);
        
        VBox airBox = new VBox(10, plane, txtAir);
        airBox.setAlignment(Pos.CENTER);
        airBox.setStyle(boxStyle); airBox.setPrefWidth(120);

        HBox topRow = new HBox(15, landBox, waterBox);
        topRow.setAlignment(Pos.CENTER);
        VBox vehiclesContainer = new VBox(15, topRow, airBox);
        vehiclesContainer.setAlignment(Pos.CENTER);
        left.getChildren().addAll(title, vehiclesContainer);

        // --- جزء الـ Acceleration ---
        Label l2 = new Label("Acceleration:"); l2.setStyle(labelStyle);
        TextField txtAcc = new TextField("0");
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
            if (val > 0) txtAcc.setText(String.valueOf(val - 1));
        });

        HBox accControl = new HBox(5, btnMinus, txtAcc, btnPlus);
        accControl.setAlignment(Pos.CENTER_LEFT);
        // ---------------------------------

        Label l7 = new Label("Constant:"); l7.setStyle(labelStyle);
        TextField txtConst = new TextField("0"); txtConst.setStyle(inputStyle);
        Button apply = new Button("Apply"); apply.setStyle("-fx-background-color:#22c55e; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-padding:8;");

        rightSide.add(l1, 0, 0); rightSide.add(txtSpeed, 1, 0);
        rightSide.add(l2, 0, 1); rightSide.add(accControl, 1, 1);
        rightSide.add(l7, 0, 2); rightSide.add(txtConst, 1, 2); rightSide.add(apply, 2, 2);
        
        // الزراير اللي تحت 
        Button inc = new Button("Increase"); Button dec = new Button("Decrease"); Button stop = new Button("STOP");
        String actionBtnStyle = "-fx-background-color:#38bdf8; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-pref-width:95; -fx-padding:8;";
        inc.setStyle(actionBtnStyle); dec.setStyle(actionBtnStyle); 
        stop.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10; -fx-pref-width:95; -fx-padding:8;");
        
        // --- التعديل في جزء الأزرار لتحديث الـ targetSpeed ---
        inc.setOnAction(e -> {
            double value = Double.parseDouble(txtAcc.getText());
            if (targetSpeed[0] == -1) {
                targetSpeed[0] = Double.parseDouble(txtSpeed.getText());
            }
            targetSpeed[0] += value; // بنزود على السرعة المطلوبة
        });
        
        dec.setOnAction(e -> {
            double value = Double.parseDouble(txtAcc.getText());
            if (targetSpeed[0] == -1) {
                targetSpeed[0] = Double.parseDouble(txtSpeed.getText());
            }
            targetSpeed[0] -= value; // بنقلل من السرعة المطلوبة
            if (targetSpeed[0] < 0) targetSpeed[0] = 0; // عشان السرعة مترجعش بالسالب
        });

        stop.setOnAction(e -> {
            targetSpeed[0] = 0; // وقف تماماً
        });     
        
        apply.setOnAction(e -> {
            targetSpeed[0] = Double.parseDouble(txtConst.getText()); // طبق سرعة ثابتة
        });
                
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