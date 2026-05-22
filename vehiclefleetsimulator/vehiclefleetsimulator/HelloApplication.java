
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

public class HelloApplication {

    private static Stage controlStage;
    private static AnimationTimer syncTimer;
    // أضفهم مع باقي الـ static fields في الأول
    private static Stage simulationStage;

// أضف الـ method دي
    public static void setSimulationStage(Stage stage) {
        simulationStage = stage;
    }

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 660;
    private static final String IMG = "src/images/";

    // ══════════════════════════════════════════════════════════════════
    //  الدالة الأساسية — تستقبل Vehicle وتحدد لوحة التحكم المناسبة
    // ══════════════════════════════════════════════════════════════════
    public static void selectVehicle(Vehicle vehicle) {
        Locale.setDefault(Locale.US);

        // وقّف الـ timer القديم لو شغال
        if (syncTimer != null) {
            syncTimer.stop();
        }

        // أغلق لوحة التحكم القديمة لو مفتوحة
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
        });

        if (simulationStage != null) {
            controlStage.setX(simulationStage.getX() + simulationStage.getWidth() -30);
            controlStage.setY(simulationStage.getY()+70);
        } else {
            // لو مفيش simulation stage، حطه في المنتصف
            controlStage.centerOnScreen();
        }
        controlStage.show();
        // بعد controlStage.show()

    }

    // ══════════════════════════════════════════════════════════════════
    //  لوحة تحكم العربيات البرية  — عداد السرعة
    // ══════════════════════════════════════════════════════════════════
    private static void openLandControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Land Control — " + vehicle.getClass().getSimpleName());
        showVehiclePanel(
                layout, stage, vehicle,
                "LAND CONTROL", "Steering Wheel", "Speed",
                IMG + "wheel.png", "km/h",
                Movable.MAX_SPEED,
                "Moving", "Stopped",
                60, "#111827", "#0369a1",
                false // isAltitude = false  →  نعرض السرعة
        );
    }

    // ══════════════════════════════════════════════════════════════════
    //  لوحة تحكم المراكب المائية  — عداد السرعة
    // ══════════════════════════════════════════════════════════════════
    private static void openWaterControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Water Control — " + vehicle.getClass().getSimpleName());
        showVehiclePanel(
                layout, stage, vehicle,
                "WATER CONTROL", "Ship Helm", "Speed",
                IMG + "helm.png", "knots",
                Movable.MAX_SPEED,
                "Sailing", "Anchored",
                50, "#075985", "#0891b2",
                false
        );
    }

    // ══════════════════════════════════════════════════════════════════
    //  لوحة تحكم الطيارات  — عداد الارتفاع
    // ══════════════════════════════════════════════════════════════════
    private static void openAirControl(BorderPane layout, Stage stage, Vehicle vehicle) {
        stage.setTitle("Air Control — " + vehicle.getClass().getSimpleName());
        showVehiclePanel(
                layout, stage, vehicle,
                "AIR CONTROL", "Joystick", "Altitude",
                IMG + "joystick.png", "ft",
                AirVehicle.MAX_ALTITUDE, // = 100
                "Flying", "Grounded",
                30, "#0f172a", "#0284c7",
                true // isAltitude = true  →  نعرض الارتفاع
        );
    }

    // ══════════════════════════════════════════════════════════════════
    //  بناء لوحة التحكم الفعلية
    // ══════════════════════════════════════════════════════════════════
    private static void showVehiclePanel(BorderPane layout, Stage stage, Vehicle vehicle,
            String title, String controlName, String valueTitle,
            String controlPath, String unit,
            double maxValue,
            String activeStatus, String idleStatus,
            double rotateAngle, String color1, String color2,
            boolean isAltitude) {

        ImageView controlImage = imageView(controlPath, title.contains("AIR") ? 165 : 155, 0);

        // القيمة الابتدائية من الـ vehicle نفسه
        double initVal = isAltitude ? vehicle.getCenterZ() : vehicle.getSpeed();

        Slider slider = new Slider(0, maxValue, initVal);
        setupSlider(slider, maxValue);

        // ── Slider → Vehicle ─────────────────────────────────────────
        slider.valueProperty().addListener((obs, old, newVal) -> {
            if (isAltitude) {
                vehicle.setCenterZ(newVal.doubleValue());
            } else {
                vehicle.setSpeed(newVal.doubleValue());
            }
        });

        // ── Labels ───────────────────────────────────────────────────
        Label valueLabel = valueDisplay();
        Label statusLabel = dashboardLabel("");
        Label nameLabel = dashboardLabel(vehicle.getClass().getSimpleName());

        // ── AnimationTimer: يزامن العداد مع قيم الـ vehicle الفعلية ──
        syncTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double current = isAltitude ? vehicle.getCenterZ() : vehicle.getSpeed();
                valueLabel.setText(String.format("%.1f %s", current, unit));
                statusLabel.setText("Status: " + (current > 0 ? activeStatus : idleStatus));
            }
        };
        syncTimer.start();

        // ── Dashboard ─────────────────────────────────────────────────
        VBox dashboard = createDashboard(title, controlName, valueTitle,
                nameLabel, valueLabel, statusLabel, controlImage, slider);

        dashboard.setFocusTraversable(true);
        dashboard.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT ->
                    controlImage.setRotate(rotateAngle);
                case LEFT ->
                    controlImage.setRotate(-rotateAngle);
                case UP ->
                    slider.setValue(Math.min(maxValue, slider.getValue() + maxValue / 20));
                case DOWN ->
                    slider.setValue(Math.max(0, slider.getValue() - maxValue / 20));
                case SPACE ->
                    new Timeline(new KeyFrame(
                            Duration.millis(1500),
                            new KeyValue(slider.valueProperty(), 0)
                    )).play();
            }
        });
        dashboard.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                controlImage.setRotate(0);
            }
        });

        // ── Close button ─────────────────────────────────────────────
        Button closeBtn = closeButton();
        closeBtn.setOnAction(e -> {
            syncTimer.stop();
            stage.close();
        });

        // ── Left panel: صورة الـ vehicle + اسمه + زر الإغلاق ────────
        VBox leftPanel = createInfoPanel(vehicle, closeBtn);

        // ── Final layout ─────────────────────────────────────────────
        HBox root = new HBox(25, leftPanel, dashboard);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");");

        layout.setCenter(root);
        Platform.runLater(dashboard::requestFocus);
    }

    // ── Panel على الشمال: صورة الـ vehicle + اسمه ────────────────────
    private static VBox createInfoPanel(Vehicle vehicle, Button closeBtn) {
        // نجيب صورة الـ vehicle من الـ imageView بتاعه مباشرة
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

    // ══════════════════════════════════════════════════════════════════
    //  Helper Methods
    // ══════════════════════════════════════════════════════════════════
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
        l.setFont(Font.font("Segoe UI", 32));
        l.setMinWidth(200);
        l.setAlignment(Pos.CENTER);
        l.setStyle("""
                -fx-font-weight: bold; -fx-background-color: rgba(56,189,248,0.18);
                -fx-border-color: rgba(125,211,252,0.65); -fx-border-width: 1.2;
                -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 10 22;
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
