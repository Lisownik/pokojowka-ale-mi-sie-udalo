import math
import random
import time
from math import floor, isnan
import socket

import json
from time import sleep

import adafruit_minimqtt.adafruit_minimqtt as MQTT
import sys
import env

# CONST VALS
LOG_CHAR_CNT = 60
client = "None"

# MY STATE

light_lux = 0
rain_analog = 0

# ID DEF
EVERY_ID = "<every>"
MY_ID = "none"

def get_arg_id(argv):
    for a in argv[1:]:
        if a.startswith("--id="):
            return a[5:]
    return None

def print_with_style(text, char_cnt = LOG_CHAR_CNT):
    text = text.strip()
    char = '-'
    chars = math.floor((char_cnt - len(text) - 2)/2)
    print(char * chars + " " + text + " " + char * chars)


def read_id_file():
    try:
        with open(env.ID_FILEPATH, 'r') as file:
            text = file.read()
            if text == "":
                raise ValueError("Id not stored in id.txt")
            return text
    except OSError or ValueError:
        content = ''
        reg_id = "tried-to-register-id-no-implementation"
        if not reg_id:
            raise Exception("No server no id file or id in file")
        with open(env.ID_FILEPATH, 'w') as file:
            file.write(reg_id)
        return reg_id

def get_my_id():
    arg_id = get_arg_id(sys.argv)
    if arg_id:
        return arg_id
    return read_id_file()

def get_path_status():
    return f"devices/roof/{MY_ID}/status"

def get_path_state():
    return f"devices/roof/{MY_ID}/state"

def get_initial_state(mqtt_client, timeout_ms=1000):
    global light_lux, rain_analog
    received = []

    print_with_style("INITIAL STATE")
    mqtt_client.on_message = lambda _client, topic, msg: received.append(msg)
    mqtt_client.connect()
    try:
        mqtt_client.subscribe(get_path_state())
        t = 0
        while not received and t < timeout_ms:
            mqtt_client.loop(timeout_ms/1000)
    finally:
        mqtt_client.disconnect()

    if not received:
        print("no retained state found")
        print('-' * LOG_CHAR_CNT)
        return

    try:
        data = json.loads(received[0])
    except ValueError:
        print("invalid JSON in state")
        print('-' * LOG_CHAR_CNT)
        return

    print(received[0])

    _light = data.get('light')
    if isinstance(_light, (int, float)):
        print(f"\tLight Intensity: {_light}")
        light_lux = _light

    _rain = data.get('rain')
    if isinstance(_rain, (int, float)):
        print(f"\tRain: {_rain}")
        rain_analog = _rain

    print('-' * LOG_CHAR_CNT)

def process_state():
    global rain_analog, light_lux
    print_with_style("CURRENT STATE", 40)
    print(f"Light Intensity: {light_lux}")
    print(f"Rain: {rain_analog}")
    print("-" * 40)

def on_message(client, topic, msg):
    global rain_analog, light_lux
    print("Message incoming: ", topic, msg)

    _, pr_id, event = topic.split("/")


    process_state()

def publish_state(publisher):
    publisher.publish(
        get_path_state(),
        json.dumps({
            "light": light_lux,
            "rain": rain_analog
        }),
        retain=True,
        qos=1,
    )

def publish_status(publisher):
    publisher.publish(
        get_path_status(),
        json.dumps({
            "online": True
        }).encode(),
        retain=True,
        qos=1,
    )

def get_subscriptions():
    return [
    ]

def subscribe(client):
    print("SUBSCRIBING")
    for sub in get_subscriptions():
        print("\tto topic: ", sub)
        client.subscribe(sub.encode())
#
def init_adafruit():
    import board
    import adafruit_veml7700

    if STATIC:
        return None

    i2c = board.I2C()

    try:
        veml7700 = adafruit_veml7700.VEML7700(i2c)
        print(f"VEML7700 detected")
        return veml7700
    except Exception:
        pass
    print("VEML7700 not found on I2C bus.")
    return None

def get_random(min, max):
    return min + floor((random.random() * (max - min)))

def get_adafruit_data(adafruit):
    return adafruit.autolux()

def get_real_data(adafruit):
    return {
        "ligth": get_adafruit_data(adafruit),
        "rain": get_random(0,100) # TODO()
    }

def get_static_data():
    return {
        "light": get_random(0,100),
        "rain": get_random(0,100)
    }

def get_data(adafruit):
    global light_lux, rain_analog
    data = {}
    if env.STATIC:
        data = get_static_data()
    else:
        data = get_adafruit_data(adafruit)

    light_lux = data.get('light')
    rain_analog = data.get('rain')

def print_configuration():
    print("Starting MUJ ROOF firmware")
    print_with_style("CONFIGURATION DATA")
    print("MQTT BROKER and REST HOST: " + env.BROKER_HOST)
    print("MQTT BROKER PORT: " + str(env.BROKER_PORT))
    print("REST PORT: " + str(env.REST_POT))
    print("ID FILE PATH: " +  env.ID_FILEPATH)
    print("STATIC MODE: " + "true" if env.STATIC else "false")
    print("-" * LOG_CHAR_CNT)

def main():
    global MY_ID
    global client
    STATIC = env.STATIC

    print_configuration()

    MY_ID = get_my_id()

    print("MY ID: " + MY_ID)
    print("STATUS PATH: " + get_path_status())
    print("STATE PATH: " + get_path_state())

    adafruit_sensor = None if STATIC else init_adafruit()
    if not STATIC and adafruit_sensor is None:
        raise SystemExit("No sensor found, exiting...")

    client = MQTT.MQTT( broker = env.BROKER_HOST, port = env.BROKER_PORT, socket_pool=socket)

    get_initial_state(client)

    client.on_message = on_message
    client.will_set(
        get_path_status(),
        json.dumps({
            "online": False
        }).encode(),
        retain = True,
        qos = 1
    )

    client.connect()

    # subscribe(client)

    publish_status(client)

    publish_state(client)

    while True:
        get_data(adafruit_sensor)
        publish_state(client)
        sleep(5)

def connect_to_wifi():
    import wifi
    while not wifi.radio.connected:
        try:
            wifi.radio.connect(env.WIFI_SSID, env.WIFI_PASSWORD)
        except ConnectionError as e:
            print("Not connected to WLAN:", e)
            time.sleep(1)
    print("Connected, IP:", wifi.radio.ipv4_address)

if sys.platform == "esp32":
    connect_to_wifi()

if __name__ == "__main__":
    main()