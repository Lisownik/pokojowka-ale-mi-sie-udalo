import random
from math import floor
from time import sleep

import paho.mqtt.client as mqtt
import json

import os

MY_ID = os.environ["ROOM_ID"]
BROKER_HOST = os.getenv("API_URL", "localhost")
BROKER_PORT = int(os.getenv("API_PORT", "1883"))

STATIC = os.getenv("STATIC", "true") == "true"

def init_adafruit():
    import board
    import busio
    import adafruit_bme680

    if STATIC:
        return None

    i2c = busio.I2C(board.SCL, board.SDA)

    for addr in [0x77, 0x76]:
        try:
            bme680 = adafruit_bme680.Adafruit_BME680_I2C(i2c, address=addr)
            print(f"BME680 detected at 0x{addr:02X}")
            bme680.sea_level_pressure = 1013.25
            return bme680
        except Exception:
            continue
    else:
        print("BME680 not found on I2C bus.")
        return None
    return None

def interpret_air_quality(gas_resistance):
    if gas_resistance > 50000:
        return "Excellent"
    elif gas_resistance > 20000:
        return "Good"
    elif gas_resistance > 10000:
        return "Moderate"
    elif gas_resistance > 5000:
        return "Poor"
    else:
        return "Unhealthy"

def get_static_data():
    return {
        "temperature": 20 + get_random(-10, 40),
        "humidity": 30 + get_random(-10, 10),
        "pressure": 1000 + get_random(-100, 100),
        "quality": 20000 + get_random(-1000, 4000)
    }

def get_random(min, max):
    return min + floor((random.random() * (max - min)))

def get_adafruit_data(bme680):
    return {
        "temperature": bme680.temperature,
        "humidity": bme680.humidity,
        "pressure": bme680.pressure,
        "quality": bme680.gas
    }

def get_data(bme680):
    if STATIC:
        return get_static_data()
    else:
        return get_adafruit_data(bme680)

def on_message(client, _, msg):
    print("GOT THE WINDOW-1 CLOSE EVENT! YUPI")
    print(msg.topic, msg.payload.decode())




def main():
    print(f"Starting with mode: {'STATIC (fake data)' if STATIC else 'REAL data'}")
    print(f"CONFIG DATA: uri: {BROKER_HOST}; port: {BROKER_PORT}")
    sensor = None if STATIC else init_adafruit()
    if not STATIC and sensor is None:
        raise SystemExit("No sensor found, exiting.")

    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.will_set(f"devices/room/{MY_ID}/status", json.dumps({"online": False}), qos=1, retain=True)

    client.on_message = on_message
    client.connect(BROKER_HOST, BROKER_PORT)

    client.publish(f"devices/room/{MY_ID}/status", json.dumps({"online": True}), qos=1, retain=True)
    client.loop_start()

    try:
        while True:
            client.publish(f"devices/room/{MY_ID}/state", json.dumps(get_data(sensor)))
            sleep(5)
    except KeyboardInterrupt:
        pass
    finally:
        client.loop_stop()
        client.disconnect()


if __name__ == "__main__":
    main()