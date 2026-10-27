import math
import random
import time
from math import floor, isnan

import json
from time import sleep

from umqtt.simple import MQTTClient
import sys
import env

LOG_CHAR_CNT = 60

client = "None"

# MY STATE
power = True
brightness = 80
temperature = 2700


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


def get_initial_state(client, timeout_ms=1000):
    global brightness, power, temperature
    received = []

    print_with_style("INITIAL STATE")
    client.set_callback(lambda topic, msg: received.append(msg))
    client.connect()
    try:
        client.subscribe(get_path_state())
        t = 0
        while not received and t < timeout_ms:
            client.check_msg()
            t += 50
            sleep(0.05)
    finally:
        client.disconnect()

    if not received:
        print("no retained state found")
        print('-' * LOG_CHAR_CNT)
        return

    try:
        data = json.loads(received[0].decode())
    except ValueError:
        print("invalid JSON in state")
        print('-' * LOG_CHAR_CNT)
        return

    print(received[0].decode())

    b = data.get('brightness')
    if isinstance(b, (int, float)) and not isnan(b):
        print(f"\tBRIGHTNESS: {b}")
        brightness = b

    p = data.get('power')
    if p is "true" or p is "false":
        print(f"\tPOWER: {p}")
        power = p == "true"

    t = data.get('temperature')
    if isinstance(t, (int, float)) and not isnan(t):
        print(f"\tTEMPERATURE: {t}")
        temperature = t

    print('-' * LOG_CHAR_CNT)

def connect_to_wifi():
    import network
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    wlan.connect(env.WIFI_SSID, env.WIFI_PASSWORD)
    while not wlan.isconnected():
        print("Not connected to WLAN")
        time.sleep(1)

def get_path_status():
    return f"devices/smart_led/{MY_ID}/status".encode()

def get_path_state():
    return f"devices/smart_led/{MY_ID}/state".encode()


def process_state():
    global power
    global brightness
    global temperature
    print_with_style("CURRENT STATE", 40)
    print(f"power state: {'on' if power else 'off'}")
    print(f"brightness: {brightness}")
    print(f"color temperature: {temperature}")
    print("-" * 40)

def on_message(topic, msg):
    print("Message incoming: ", topic.decode(), msg.decode())

    _, id, event = topic.decode().split("/")

    global power
    global brightness

    if event == "on":
        power = True
    elif event == "off":
        power = False
    elif event == "brightness":
        new_brightness = json.loads(msg.decode())['brightness']
        if not isnan(new_brightness):
            brightness = new_brightness




    process_state()
    if client:
        publish_state(client)

def get_subscriptions():
    return [
        f"events/{MY_ID}/on",
        f"events/{MY_ID}/off",
        f"events/{MY_ID}/brightness",
        f"events/smart_led-{EVERY_ID}/on",
        f"events/smart_led-{EVERY_ID}/off",
        f"events/smart_led-{EVERY_ID}/brightness",
    ]

def subscribe(client):
    print("SUBSCRIBING")
    for sub in get_subscriptions():
        print("\tto topic: ", sub)
        client.subscribe(sub.encode())

def getRandom(_min, _max):
    return _min + floor((random.random() * (_max - _min)))

def publish_state(publisher):
    publisher.publish(
        get_path_state(),
        json.dumps({
            "brightness": brightness,
            "power": "true" if power else "false",
            "temperature": getRandom(2700, 4000),
        }).encode(),
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

def print_configuration():
    print("Starting Smart Ledzik firmware")
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
    print_configuration()

    MY_ID = get_my_id()

    print("MY ID: " + MY_ID)
    print("STATUS PATH: " + get_path_status().decode())
    print("STATE PATH: " + get_path_state().decode())

    client = MQTTClient(f"smart-led-client-{MY_ID}", env.BROKER_HOST, env.BROKER_PORT)

    get_initial_state(client)

    client.set_callback(on_message)
    client.set_last_will(
        get_path_status(),
        json.dumps({
            "online": False
        }).encode(),
        retain = True,
        qos = 1
    )



    client.connect()

    subscribe(client)

    publish_status(client)

    publish_state(client)

    print("Now listening for messages")
    while True:
        client.wait_msg()


if sys.platform == "esp32":
    connect_to_wifi()

if __name__ == "__main__":
    main()