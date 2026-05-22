package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.util.Locale;

public class ControlDashboard {

    private static Stage controlStage;
    private static AnimationTimer syncTimer;
    private static Stage simulationStage;
    private static Runnable onPanelClosed;

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 660;
    private static final String IMG = "src/images/";
    private static final double SPEED_SLIDER_MAX = 1.6;

    public static void setSimulationStage(Stage stage) {
        simulationStage = stage;
    }

    public static void setOnPanelClosed(Runnable action) {
        onPanelClosed = action;
    }

    private static void notifyPanelClosed() {
        if (onPanelClosed != null) {
            onPanelClosed.run();
        }
    }

    public static void selectVehicle(Vehicle vehicle) {
        Locale.setDefault(Locale.US);

        if (syncTimer != null) {
            syncTimer.stop();
        }

        if (controlStage != null && controlStage.isShowing()) {
            controlStage.close();
        }

        controlStage = new Stage();
        BorderPane layout = new BorderPane();

        if (vehicle instanceof LandVehicle) {
            openLandControl(layout, controlStage, vehicle);
        } else if (vehicle instanceof WaterVehicle) {
            openWaterControl(layout, controlStage, vehicle);
        } else if (vehicle instanceof AirVehicle) {
            openAirControl(layout, controlStage, vehicle);
        }

        controlStage.setScene(new Scene(layout, WIDTH, HEIGHT));

        controlStage.setOnCloseRequest(e -> {
            if (syncTimer != null) {
                syncTimer.stop();
            }
            notifyPanelClosed();
        });

        if (simulationStage != null) {
            controlStage.setX(simulationStage.getX() + simulationStage.getWidth() - 30);
            controlStage.setY(simulationStage.getY() + 70);
        } else {
            controlStage.centerOnScreen();
        }

        controlStage.show();
    }

    private static void openLandControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Land Control — " + vehicle.getClass().getSimpleName());

        showVehiclePanel(
                layout, stage, vehicle,
                "LAND CONTROL", "Steering Wheel", "Speed",
                IMG + "wheel.png", "km/h",
                SPEED_SLIDER_MAX,
                "Moving", "Stopped",
                60, "#111827", "#0369a1",
                false
        );
    }

    private static void openWaterControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Water Control — " + vehicle.getClass().getSimpleName());

        if (vehicle instanceof Submarine) {
            showVehiclePanel(
                    layout, stage, vehicle,
                    "SUBMARINE CONTROL", "Depth Control", "Depth",
                    IMG + "helm.png", "m",
                    Math.abs(Submarine.MAX_DEPTH),
                    "Diving", "Surface",
                    50, "#075985", "#0891b2",
                    true
            );
        } else {
            showVehiclePanel(
                    layout, stage, vehicle,
                    "WATER CONTROL", "Ship Helm", "Speed",
                    IMG + "helm.png", "knots",
                    SPEED_SLIDER_MAX,
                    "Sailing", "Anchored",
                    50, "#075985", "#0891b2",
                    false
            );
        }
    }

    private static void openAirControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Air Control — " + vehicle.getClass().getSimpleName());

        showVehiclePanel(
                layout, stage, vehicle,
                "AIR CONTROL", "Joystick", "Altitude",
                IMG + "joystick.png", "ft",
                AirVehicle.MAX_ALTITUDE,
                "Flying", "Grounded",
                30, "#0f172a", "#0284c7",
                true
        );
    }

    private static void showVehiclePanel(BorderPane layout, Stage stage, Vehicle vehicle,
                                         String title, String controlName, String valueTitle,
                                         String controlPath, String unit,
                                         double maxValue,
                                         String activeStatus, String idleStatus,
                                         double rotateAngle, String color1, String color2,
                                         boolean isAltitude) {

        boolean isSubmarine = vehicle instanceof Submarine;
        boolean needsSpeedSlider = vehicle instanceof AirVehicle || vehicle instanceof Submarine;

        ImageView controlImage = imageView(controlPath, title.contains("AIR") ? 165 : 155, 0);

        double initVal;

        if (isSubmarine) {
            initVal = Math.abs(vehicle.getCenterZ());
        } else if (isAltitude) {
            initVal = vehicle.getCenterZ();
        } else {
            initVal = Math.min(vehicle.getSpeed(), SPEED_SLIDER_MAX);
        }

        Slider mainSlider = new Slider(0, maxValue, initVal);
        setupSlider(mainSlider, maxValue);

        mainSlider.valueProperty().addListener((obs, old, newVal) -> {
            if (isSubmarine) {
                vehicle.setCenterZ(-newVal.doubleValue());
            } else if (isAltitude) {
                vehicle.setCenterZ(newVal.doubleValue());
            } else {
                vehicle.setSpeed(newVal.doubleValue());
            }
        });

        Slider speedSlider = null;

        if (needsSpeedSlider) {
            speedSlider = new Slider(
                    0,
                    SPEED_SLIDER_MAX,
                    Math.min(vehicle.getSpeed(), SPEED_SLIDER_MAX)
            );

            setupSlider(speedSlider, SPEED_SLIDER_MAX);

            speedSlider.valueProperty().addListener((obs, old, newVal) -> {
                vehicle.setSpeed(newVal.doubleValue());
            });
        }

        Label valueLabel = valueDisplay();
        Label speedLabel = valueDisplay();
        Label statusLabel = dashboardLabel("");
        Label nameLabel = dashboardLabel(vehicle.getClass().getSimpleName());

        Slider finalSpeedSlider = speedSlider;

        syncTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double mainValue;

                if (isSubmarine) {
                    mainValue = Math.abs(vehicle.getCenterZ());
                } else if (isAltitude) {
                    mainValue = vehicle.getCenterZ();
                } else {
                    mainValue = vehicle.getSpeed();

                    if (mainValue <= mainSlider.getMax()) {
                        mainSlider.setValue(mainValue);
                    }
                }

                valueLabel.setText(String.format("%.1f %s", mainValue, unit));

                if (finalSpeedSlider != null) {
                    double speed = vehicle.getSpeed();

                    if (speed <= finalSpeedSlider.getMax()) {
                        finalSpeedSlider.setValue(speed);
                    }

                    speedLabel.setText(String.format("%.1f speed", speed));
                }

                double statusValue = isAltitude ? mainValue : vehicle.getSpeed();
                statusLabel.setText("Status: " + (statusValue > 0 ? activeStatus : idleStatus));
            }
        };

        syncTimer.start();

        VBox dashboard;

        if (needsSpeedSlider) {
            HBox slidersBox = new HBox(28,
                    controlImage,
                    createSliderBlock(valueTitle, valueLabel, mainSlider),
                    createSliderBlock("Speed", speedLabel, speedSlider)
            );

            slidersBox.setAlignment(Pos.CENTER);

            dashboard = new VBox(14,
                    styledLabel(title, 26, "white"),
                    nameLabel,
                    statusLabel,
                    styledLabel(controlName, 14, "#dbeafe"),
                    slidersBox
            );

            dashboard.setAlignment(Pos.CENTER);
            dashboard.setPadding(new Insets(24));
            dashboard.setPrefWidth(650);
            dashboard.setStyle(panelStyle());
            dashboard.setEffect(shadow(24, 0.42));

        } else {
            dashboard = createDashboard(title, controlName, valueTitle,
                    nameLabel, valueLabel, statusLabel, controlImage, mainSlider);
        }

        dashboard.setFocusTraversable(true);

        dashboard.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT ->
                        controlImage.setRotate(rotateAngle);

                case LEFT ->
                        controlImage.setRotate(-rotateAngle);

                case UP ->
                        mainSlider.setValue(Math.min(maxValue, mainSlider.getValue() + maxValue / 20));

                case DOWN ->
                        mainSlider.setValue(Math.max(0, mainSlider.getValue() - maxValue / 20));

                case SPACE ->
                        new Timeline(new KeyFrame(
                                Duration.millis(1500),
                                new KeyValue(mainSlider.valueProperty(), 0)
                        )).play();
            }
        });

        dashboard.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                controlImage.setRotate(0);
            }
        });

        Button closeBtn = closeButton();

        closeBtn.setOnAction(e -> {
            if (syncTimer != null) {
                syncTimer.stop();
            }
            notifyPanelClosed();
            stage.close();
        });

        VBox leftPanel = createInfoPanel(vehicle, closeBtn);

        HBox root = new HBox(25, leftPanel, dashboard);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");");

        layout.setCenter(root);
        Platform.runLater(dashboard::requestFocus);
    }

    private static VBox createSliderBlock(String title, Label valueLabel, Slider slider) {
        VBox box = new VBox(10,
                styledLabel(title, 13, "#bae6fd"),
                centered(valueLabel),
                slider
        );

        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static VBox createInfoPanel(Vehicle vehicle, Button closeBtn) {
        ImageView vehicleImg = new ImageView(vehicle.imageView.getImage());
        vehicleImg.setFitWidth(160);
        vehicleImg.setFitHeight(110);
        vehicleImg.setPreserveRatio(true);

        Label typeLabel = styledLabel(vehicle.getClass().getSimpleName(), 17, "white");
        typeLabel.setWrapText(true);
        typeLabel.setAlignment(Pos.CENTER);

        VBox panel = new VBox(18,
                styledLabel("Vehicle Info", 16, "#bae6fd"),
                vehicleImg,
                typeLabel,
                closeBtn
        );

        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(22));
        panel.setPrefWidth(230);
        panel.setStyle(panelStyle());
        panel.setEffect(shadow(18, 0.35));

        return panel;
    }

    private static VBox createDashboard(String title, String controlName, String valueTitle,
                                        Label nameLabel, Label valueLabel, Label statusLabel,
                                        ImageView controlImage, Slider slider) {

        HBox controlBox = new HBox(34, controlImage, slider);
        controlBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(14,
                styledLabel(title, 26, "white"),
                nameLabel,
                styledLabel(valueTitle, 13, "#bae6fd"),
                centered(valueLabel),
                statusLabel,
                styledLabel(controlName, 14, "#dbeafe"),
                controlBox
        );

        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setPrefWidth(540);
        box.setStyle(panelStyle());
        box.setEffect(shadow(24, 0.42));

        return box;
    }

    private static ImageView imageView(String path, double width, double height) {
        ImageView v = new ImageView(new javafx.scene.image.Image("file:" + path));
        v.setFitWidth(width);

        if (height > 0) {
            v.setFitHeight(height);
        }

        v.setPreserveRatio(true);
        return v;
    }

    private static HBox centered(Node node) {
        HBox box = new HBox(node);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static void setupSlider(Slider slider, double max) {
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(false);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setPrefHeight(250);
        slider.setMajorTickUnit(max / 4);
        slider.setMinorTickCount(3);
        slider.setBlockIncrement(max / 20);
        slider.setStyle("-fx-control-inner-background: #0b1720; -fx-accent: #38bdf8;");
        slider.setFocusTraversable(false);
    }

    private static Button closeButton() {
        Button btn = new Button("✕  Close Panel");

        btn.setPrefSize(160, 42);
        btn.setStyle("""
                -fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 14px;
                -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand;
                """);

        btn.setFocusTraversable(false);
        return btn;
    }

    private static Label styledLabel(String text, int size, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px; -fx-font-weight: bold;");
        return l;
    }

    private static Label dashboardLabel(String text) {
        Label l = new Label(text);

        l.setTextFill(Color.web("#f8fafc"));
        l.setFont(Font.font("Segoe UI", 15));
        l.setStyle("-fx-font-weight: bold;");

        return l;
    }

    private static Label valueDisplay() {
        Label l = new Label();

        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Segoe UI", 28));
        l.setMinWidth(160);
        l.setAlignment(Pos.CENTER);
        l.setStyle("""
                -fx-font-weight: bold; -fx-background-color: rgba(56,189,248,0.18);
                -fx-border-color: rgba(125,211,252,0.65); -fx-border-width: 1.2;
                -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 10 18;
                """);

        return l;
    }

    private static DropShadow shadow(int radius, double alpha) {
        return new DropShadow(radius, Color.rgb(0, 0, 0, alpha));
    }

    private static String panelStyle() {
        return """
                -fx-background-color: rgba(8,18,28,0.78); -fx-background-radius: 24;
                -fx-border-color: rgba(148,163,184,0.45); -fx-border-width: 1.2; -fx-border-radius: 24;
                """;
    }
}
