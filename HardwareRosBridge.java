package com.vehiclefleetsimulator.vehiclefleetsimulator;

import javafx.animation.AnimationTimer;
import java.io.*;
import java.util.List;
import java.util.Locale;

public class HardwareRosBridge {

    public enum Mode {
        AUTO,
        MANUAL
    }

    private static final double SPEEDOMETER_MAX = 2.4;

    private final List<Vehicle> vehicles;
    private Vehicle selectedVehicle;

    private Mode mode = Mode.AUTO;

    private int focusedIndex = 0;
    private long lastCommandSeq = -1;

    private final File commandFile = new File("/home/mohamed-khairy/Documents/JavaProject/poject/hardware_command.json");
    private final File telemetryFile = new File("/home/mohamed-khairy/Documents/JavaProject/poject/hardware_telemetry.json");

    private AnimationTimer timer;
    private long lastUpdate = 0;

    public HardwareRosBridge(List<Vehicle> vehicles) {
        this.vehicles = vehicles;

        if (!vehicles.isEmpty()) {
            selectedVehicle = vehicles.get(0);
        }
    }

    public void selectVehicle(Vehicle vehicle) {
        selectedVehicle = vehicle;
        focusedIndex = vehicles.indexOf(vehicle);

        if (focusedIndex < 0) {
            focusedIndex = 0;
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate < 500_000_000) {
                    return;
                }

                lastUpdate = now;
                readHardwareCommand();
                writeTelemetry();
            }
        };

        timer.start();
    }

    private void readHardwareCommand() {
        if (!commandFile.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(commandFile))) {
            String json = br.readLine();

            if (json == null || json.isEmpty()) {
                return;
            }

            long seq = getLong(json, "seq", -1);
            int button = (int) getLong(json, "button", 0);

            if (seq == lastCommandSeq || button == 0) {
                return;
            }

            lastCommandSeq = seq;

            if (mode == Mode.MANUAL) {
                handleManualButton(button);
            } else {
                handleAutoButton(button);
            }

        } catch (Exception ignored) {
        }
    }

    private void handleManualButton(int button) {
        Vehicle v = selectedVehicle;

        if (v == null && !vehicles.isEmpty()) {
            selectedVehicle = vehicles.get(0);
            v = selectedVehicle;
        }

        switch (button) {
            case 2 -> nextVehicle();

            case 11 -> {
                if (v != null) {
                    v.setSpeed(0);
                    v.setVz(0);
                    v.setAcceleration(0);
                }
            }

            case 6 -> {
                if (v instanceof AirVehicle) {
                    v.moveZ(12);
                } else if (v instanceof Submarine) {
                    v.moveZ(12);
                } else if (v != null) {
                    v.setSpeed(v.getSpeed() + 0.2);
                    v.setAcceleration(0);
                }
            }

            case 10 -> {
                if (v instanceof AirVehicle) {
                    v.moveZ(-12);
                } else if (v instanceof Submarine) {
                    v.moveZ(-12);
                } else if (v != null) {
                    double newSpeed = v.getSpeed() - 0.2;

                    if (newSpeed < 0) {
                        newSpeed = 0;
                    }

                    v.setSpeed(newSpeed);
                    v.setAcceleration(0);
                }
            }
        }
    }

    private void handleAutoButton(int button) {
        switch (button) {
            case 10 -> changeAllSpeeds(1);
            case 6 -> changeAllSpeeds(-1);
            case 11 -> stopAllVehicles();
        }
    }

    private void nextVehicle() {
        if (vehicles.isEmpty()) {
            return;
        }

        focusedIndex++;

        if (focusedIndex >= vehicles.size()) {
            focusedIndex = 0;
        }

        selectedVehicle = vehicles.get(focusedIndex);
        ControlDashboard.selectVehicle(selectedVehicle);
    }

    private void changeAllSpeeds(double step) {
        for (Vehicle v : vehicles) {

            if (mode == Mode.MANUAL && v == selectedVehicle) {
                continue;
            }

            double newSpeed = v.getSpeed() + step;

            if (newSpeed < 0) {
                newSpeed = 0;
            }

            v.setSpeed(newSpeed);
            v.setAcceleration(0);
        }
    }

    private void stopAllVehicles() {
        for (Vehicle v : vehicles) {

            if (mode == Mode.MANUAL && v == selectedVehicle) {
                continue;
            }

            v.setSpeed(0);
            v.setVz(0);
            v.setAcceleration(0);
        }
    }

    private void writeTelemetry() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(telemetryFile))) {
            Telemetry t = calculateTelemetry();

            String lcd1;
            String lcd2;
            double servoSpeed;

            if (mode == Mode.MANUAL) {
                Vehicle v = selectedVehicle;

                if (v == null) {
                    lcd1 = "MANUAL";
                    lcd2 = "NO VEHICLE";
                    servoSpeed = 0;
                } else {
                    double speed = Math.abs(v.getSpeed());

                    lcd1 = "MAN " + shortName(v);

                    if (v instanceof AirVehicle) {
                        lcd2 = "ALT:" + format(v.getCenterZ());
                    } else if (v instanceof Submarine) {
                        lcd2 = "DEP:" + format(v.getCenterZ());
                    } else {
                        lcd2 = "SPD:" + format(speed);
                    }

                    servoSpeed = speed;
                }

            } else {
                lcd1 = "AUTO MODE";
                lcd2 = "AVG:" + format(t.avgSpeed);

                servoSpeed = t.avgSpeed;
            }

            int servoAngle = speedToServoAngle(servoSpeed);

            pw.println("{");
            pw.println("\"lcd1\":\"" + fit16(lcd1) + "\",");
            pw.println("\"lcd2\":\"" + fit16(lcd2) + "\",");
            pw.println("\"servo\":" + servoAngle);
            pw.println("}");

        } catch (Exception ignored) {
        }
    }

    private Telemetry calculateTelemetry() {
        Telemetry t = new Telemetry();

        for (Vehicle v : vehicles) {
            double speed = Math.abs(v.getSpeed());

            t.totalSpeed += speed;
            t.totalCount++;

            if (t.totalCount == 1) {
                t.maxSpeed = speed;
                t.minSpeed = speed;
            } else {
                if (speed > t.maxSpeed) {
                    t.maxSpeed = speed;
                }

                if (speed < t.minSpeed) {
                    t.minSpeed = speed;
                }
            }
        }

        if (t.totalCount > 0) {
            t.avgSpeed = t.totalSpeed / t.totalCount;
        }

        return t;
    }

    private int speedToServoAngle(double speed) {
        double ratio = speed / SPEEDOMETER_MAX;

        if (ratio > 1) {
            ratio = 1;
        }

        if (ratio < 0) {
            ratio = 0;
        }

        return (int) Math.round(ratio * 180);
    }

    private String shortName(Vehicle v) {
        String name = v.getClass().getSimpleName();

        if (name.length() > 8) {
            return name.substring(0, 8);
        }

        return name;
    }

    private String fit16(String s) {
        if (s == null) {
            s = "";
        }

        if (s.length() > 16) {
            return s.substring(0, 16);
        }

        return s;
    }

    private String format(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private long getLong(String json, String key, long defaultValue) {
        try {
            String target = "\"" + key + "\":";
            int start = json.indexOf(target);

            if (start == -1) {
                return defaultValue;
            }

            start += target.length();

            int end = json.indexOf(",", start);

            if (end == -1) {
                end = json.indexOf("}", start);
            }

            return Long.parseLong(json.substring(start, end).trim());

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static class Telemetry {
        int totalCount = 0;
        double totalSpeed = 0;
        double avgSpeed = 0;
        double maxSpeed = 0;
        double minSpeed = 0;
    }
}