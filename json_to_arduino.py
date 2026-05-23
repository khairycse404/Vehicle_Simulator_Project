#!/usr/bin/env python3

import json
import os
import time
import serial

import rclpy
from rclpy.node import Node


class HardwareBridge(Node):

    def __init__(self):
        super().__init__("hardware_bridge")

        self.command_path = "/home/mohamed-khairy/Documents/JavaProject/poject/hardware_command.json"
        self.telemetry_path = "/home/mohamed-khairy/Documents/JavaProject/poject/hardware_telemetry.json"

        self.seq = 0
        self.last_lcd1 = ""
        self.last_lcd2 = ""
        self.last_servo = -1

        self.arduino = serial.Serial("/dev/ttyACM0", 9600, timeout=0.1)
        time.sleep(2)

        self.timer = self.create_timer(0.2, self.loop)

    def loop(self):
        self.read_from_arduino()
        self.send_telemetry_to_arduino()

    def read_from_arduino(self):
        try:
            while self.arduino.in_waiting > 0:
                line = self.arduino.readline().decode(errors="ignore").strip()

                if line.startswith("BTN:"):
                    button = int(line.split(":")[1])
                    self.seq += 1

                    data = {
                        "seq": self.seq,
                        "button": button
                    }

                    with open(self.command_path, "w") as file:
                        json.dump(data, file)

                    self.get_logger().info(f"Button {button}")

        except Exception as e:
            self.get_logger().error(str(e))

    def send_telemetry_to_arduino(self):
        if not os.path.exists(self.telemetry_path):
            return

        try:
            with open(self.telemetry_path, "r") as file:
                data = json.load(file)

            lcd1 = str(data.get("lcd1", ""))[:16]
            lcd2 = str(data.get("lcd2", ""))[:16]
            servo = int(data.get("servo", 0))

            if lcd1 != self.last_lcd1 or lcd2 != self.last_lcd2:
                self.arduino.write(f"LCD:{lcd1}|{lcd2}\n".encode())
                self.last_lcd1 = lcd1
                self.last_lcd2 = lcd2

            if servo != self.last_servo:
                self.arduino.write(f"SERVO:{servo}\n".encode())
                self.last_servo = servo

        except Exception as e:
            self.get_logger().error(str(e))


def main(args=None):
    rclpy.init(args=args)
    node = HardwareBridge()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()


if __name__ == "__main__":
    main()